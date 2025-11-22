import json
import numpy as np
import os

base = os.path.dirname(os.path.abspath(__file__))
json_path = os.path.join(base, "..", "resources", "parameters", "nn_weights.json")
if os.path.exists(json_path):
    with open(json_path) as f:
        json_data_in = json.load(f)
else:
    rng = np.random.default_rng(2)
    n_l_init = [53, 16, 16, 16, 1]
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
        json_data_in = json.load(f)

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
        a.append(relu(z) if i < l - 1 else z)
    return a
def relu(x):
    return np.maximum(0, x)

# Load training data from JSON file
with open("C:/Users/nicol/Downloads/training_data.csv", "r") as f:
    training_data = json.load(f)

# Update  goal values in training data using temporal difference learning
alpha = 0.01
ii = int(0)
for game in training_data:
    print (ii)
    ii += 1
    # Compute y_hats (evaluations) for each state
    for state in game["states"]:
        state["a"] = calcEval(state["x"])
        state["y_hat"] = state["a"][-1][0]

    # Update goal values & Costs
    game["states"][-1]["y"] = (1 - alpha) * game["states"][-1]["y_hat"] + alpha * game["result"]
    game["states"][-1]["C"] = 1/2 * (game["states"][-1]["y"] - game["states"][-1]["y_hat"]) ** 2
    for i in reversed(range(len(game["states"]) - 1)):
        game["states"][i]["y"] = (1 - alpha) * game["states"][i]["y_hat"] + alpha * game["states"][i+1]["y"]
        game["states"][i]["C"] = 1/2 * (game["states"][i]["y"] - game["states"][i]["y_hat"]) ** 2

    w_gradients_sum = [np.zeros_like(ww) for ww in w]
    b_gradients_sum = [np.zeros_like(bb) for bb in b]

    for state in game["states"]:

        dC_dA = [np.zeros_like(a) for a in state["a"]]
        dC_dA[-1][0] = state["y_hat"] - state["y"]
        b_gradients_sum[-1][0] += dC_dA[-1][0]
        for k in range(n_l[-2]):
            w_gradients_sum[-1][0][k] += dC_dA[-1][0] * state["a"][-2][k]

        for i in reversed(range(1, l)):
            for j in range(n_l[i]):
                dC_dA[i][j] = dC_dA[i+1] @ w[i][:,j] if state["a"][i][j] > 0 else 0
                b_gradients_sum[i-1][j] += dC_dA[i][j]
                for k in range(n_l[i-1]):
                    w_gradients_sum[i-1][j][k] += dC_dA[i][j] * state["a"][i-1][k]

    b_gradients_avg = [grad / len(game["states"]) for grad in b_gradients_sum]
    w_gradients_avg = [grad / len(game["states"]) for grad in w_gradients_sum]

    b = [prev - grad * 0.2 for prev, grad in zip(b, b_gradients_avg)]
    w = [prev - grad * 0.2 for prev, grad in zip(w, w_gradients_avg)]


# Convert weights and biases to lists for JSON serialization
json_data_out = {
    "n_l": n_l,
    "weights_list" : [weight.tolist() for weight in w],
    "biases_list" : [bias.tolist() for bias in b]
}


# Save weights and biases to JSON file
with open(json_path, "w") as f:
#with open("C:/Users/nicol/Downloads/resulting_weights_and_biases.json", "w") as f:
    json.dump(json_data_out, f, indent=4)