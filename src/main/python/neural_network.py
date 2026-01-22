import numpy as np
import sys
import json
import os

np.set_printoptions(suppress=True, linewidth=200)

# Load weights and biases from JSON
base = os.path.dirname(os.path.abspath(__file__))
json_path_git_repo = os.path.join(base, "..", "resources", "parameters", "nn_weights.json")
json_path_deployment_folder = os.path.join(base, "nn_weights.json")

if os.path.exists(json_path_git_repo):
    json_path = json_path_git_repo
elif os.path.exists(json_path_deployment_folder):
    json_path = json_path_deployment_folder
else:
    json_path = ""

with open(json_path) as f:
    json_data = json.load(f)

# Save weights and biases as NumPy arrays
n_l = json_data["n_l"]
l = len(n_l) - 1
w = [np.array(x) for x in json_data["weights_list"]]
b = [np.array(x) for x in json_data["biases_list"]]

def relu(x):
    return np.maximum(0, x)

def sigmoid(x):
    return 1 / (1 + np.exp(-x))

#a = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1]#
#
#for i in range(l):
#    z = w[i] @ a + b[i]
#    a = relu(z) if i < l - 1 else sigmoid(z)
#print(a)

# Read input from stdin, process through the neural network, and output result
for line in sys.stdin:
    inputList = json.loads(line)
    input = np.array(inputList)
    a = input
    for i in range(l):
        z = w[i] @ a + b[i]
        a = relu(z) if i < l - 1 else sigmoid(z)
    print(a, flush=True)