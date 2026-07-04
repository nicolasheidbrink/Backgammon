import json
import numpy as np
import os
import concurrent.futures

np.set_printoptions(suppress=True, linewidth=200)


base = os.path.dirname(os.path.abspath(__file__))
json_path = os.path.join(base, "..", "resources", "parameters", "nn_weights.json")
if os.path.exists(json_path):
    with open(json_path) as f:
        json_data_in = json.load(f)
else:
    rng = np.random.default_rng(0)
    n_l_init = [203, 20, 20, 6] # insert desired nodes per layer here
    l_init = len(n_l_init) - 1
    w_init = []
    for i in range(l_init):
        w_init.append(rng.standard_normal((n_l_init[i+1], n_l_init[i])) * 0.2)
    b_init = []
    for i in range(l_init):
        b_init.append(np.full(n_l_init[i+1], 0.01))
    json_data_out_init = {
        "n_l": n_l_init,
        "weights_list" : [weight.tolist() for weight in w_init],
        "biases_list" : [bias.tolist() for bias in b_init]
    }
    with open(json_path, "w") as f:
        json.dump(json_data_out_init, f, indent=4)
        json_data_in = json_data_out_init

n_l = json_data_in["n_l"]
l = len(n_l) - 1
w = [np.array(x) for x in json_data_in["weights_list"]]
b = [np.array(x) for x in json_data_in["biases_list"]]

# Evaluate position
def calcEval(x):
    a = []
    a.append(np.array(x))
    for i in range(l):
        z = w[i] @ a[-1] + b[i]
        a.append(relu(z) if i < l - 1 else sigmoid(z))
    return a

def relu(x):
    return np.maximum(0, x)

def sigmoid(x):
    return 1 / (1 + np.exp(-x))



alpha = 0.01
td_lambda = 0.92

def calc_grads(core_nr):
    with open(f"C:/Users/nicol/Downloads/training_data_{core_nr}.csv", "r") as f:
        training_data = json.load(f)

    game_counter = int(0)

    b_gradients_avgs_for_process = []
    w_gradients_avgs_for_process = []

    for game in training_data:
        print (f"core {core_nr}, game {game_counter}", flush = True)
        game_counter += 1
        
        game["result"] = np.array(game["result"])

        # Compute y_hats (evaluations) for each state
        for state in game["states"]:
            state["a"] = calcEval(state["x"])
            state["y_hat"] = state["a"][-1]                      #if not state["reversed"] else state["a"][-1][::-1]

        # Update goal values & Costs
        game["states"][-1]["y"] = game["result"]

        running_total = game["states"][-1]["y_hat"]
        counter = 1

        # TD(\lambda) calculation of target; see Samsung Notes for explanation
        for i in reversed(range(len(game["states"]) - 1)):
            game["states"][i]["y"] = (1-td_lambda) * running_total + td_lambda ** counter * game["result"]
            running_total = running_total * td_lambda + game["states"][i]["y_hat"]
            counter += 1

        for state in game["states"]:
            #print(f"y: {state["y"]}\ny_hat: {state["y_hat"]}\n")
            #######################################################if state["reversed"]:
        #######################################################     state["y"] = state["y"][::-1]
        #######################################################     state["y_hat"] = state["y_hat"][::-1]
            state["C"] = np.sum(  1/2 * (state["y"] - state["y_hat"]) ** 2  )
        
        w_gradients_sum = [np.zeros_like(ww) for ww in w]
        b_gradients_sum = [np.zeros_like(bb) for bb in b]

        for state in game["states"]:

            dC_dZ = [np.zeros_like(a) for a in state["a"]]
            dC_dZ[-1] = -state["y_hat"] * (1 - state["y_hat"]) * (state["y"] - state["y_hat"])

            for i in reversed(range(1, l+1)):
                for j in range(n_l[i]):
                    if i < l:
                        dC_dZ[i][j] = dC_dZ[i+1] @ w[i][:,j] if state["a"][i][j] > 0 else 0
                    b_gradients_sum[i-1][j] += dC_dZ[i][j]
                    for k in range(n_l[i-1]):
                        w_gradients_sum[i-1][j][k] += dC_dZ[i][j] * state["a"][i-1][k]

        b_gradients_avgs_for_process.append( [grad / len(game["states"]) for grad in b_gradients_sum] )
        w_gradients_avgs_for_process.append( [grad / len(game["states"]) for grad in w_gradients_sum] )

    b_gradients_avg_for_process_sum = [sum(gradient) for gradient in zip(*b_gradients_avgs_for_process)]
    w_gradients_avg_for_process_sum = [sum(gradient) for gradient in zip(*w_gradients_avgs_for_process)]

    return b_gradients_avg_for_process_sum, w_gradients_avg_for_process_sum


if __name__ == '__main__':
    num_cores = 7
    with concurrent.futures.ProcessPoolExecutor(max_workers=num_cores) as executor:
        futures = [executor.submit(calc_grads, core_nr) for core_nr in range(num_cores)]
    
        results = []
        for future in concurrent.futures.as_completed(futures):
            # Collect the dictionary returned by each process
            results.append(future.result())
    
    list_of_b_grads_by_process, list_of_w_grads_by_process = zip(*results)

    # 2. Sum each group positionally
    total_summed_b_grads = [sum(col) for col in zip(*list_of_b_grads_by_process)]
    total_summed_w_grads = [sum(col) for col in zip(*list_of_w_grads_by_process)]

    b = [prev - grad * alpha for prev, grad in zip(b, total_summed_b_grads)]
    w = [prev - grad * alpha for prev, grad in zip(w, total_summed_w_grads)]

    # Convert weights and biases to lists for JSON serialization
    json_data_out = {
        "n_l": n_l,
        "weights_list" : [weight.tolist() for weight in w],
        "biases_list" : [bias.tolist() for bias in b]
    }

    # Save weights and biases to JSON file
    with open(json_path, "w") as f:
        json.dump(json_data_out, f, indent=4)