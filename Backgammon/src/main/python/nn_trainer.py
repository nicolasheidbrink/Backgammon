import json
import numpy as np
import os

# Define neural network architecture
n_l = [53, 16, 16, 16, 1]
l = len(n_l) - 1

# Calculate weights and biases as NumPy arrays
rng = np.random.default_rng(2)

w = []
for i in range(l):
    w.append(rng.standard_normal((n_l[i+1], n_l[i])) * 0.01)

b = []
for i in range(l):
    b.append(np.zeros(n_l[i+1]).reshape(-1, 1))

# Convert weights and biases to lists for JSON serialization
json_data = {
    "n_l": n_l,
    "weights_list" : [weight.tolist() for weight in w],
    "biases_list" : [bias.tolist() for bias in b]
}

# File path for saving weights and biases
base = os.path.dirname(os.path.abspath(__file__))
json_path = os.path.join(base, "..", "resources", "parameters", "nn_weights.json")

# Save weights and biases to JSON file
with open(json_path, "w") as f:
    json.dump(json_data, f, indent=4)