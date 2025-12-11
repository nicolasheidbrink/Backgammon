import numpy as np
import sys
import json
import os

# Load weights and biases from JSON
base = os.path.dirname(os.path.abspath(__file__))
json_path = os.path.join(base, "..", "resources", "parameters", "nn_weights.json")

with open(json_path) as f:
    json_data = json.load(f)

# Save weights and biases as NumPy arrays
n_l = json_data["n_l"]
l = len(n_l) - 1
w = [np.array(x) for x in json_data["weights_list"]]
b = [np.array(x) for x in json_data["biases_list"]]

# Define ReLU activation function
def relu(x):
    return np.maximum(0, x)

#a = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1]

#for i in range(l):
#    a = relu(w[i] @ a + b[i])
#print(a[0], flush=True)

# Read input from stdin, process through the neural network, and output result
for line in sys.stdin:
    inputList = json.loads(line)
    input = np.array(inputList)
    a = input
    for i in range(l):
        z = w[i] @ a + b[i]
        a = relu(z) if i < l - 1 else z
    print(a[0], flush=True)