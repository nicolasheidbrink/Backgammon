import numpy as np
import sys
import json

rng = np.random.default_rng(1)

n_l = [53, 16, 16, 16, 1]
l = len(n_l) - 1

# input = np.array([[0,0,0,0,0,5/15,0,3/15,0,0,0,0,5/15,0,0,0,0,0,0,0,0,0,0,2/15, # White checkers from point 1 to point 24,
#                    2/15,0,0,0,0,0,0,0,0,0,0,5/15,0,0,0,0,3/15,0,5/15,0,0,0,0,0, # black checkers from point 1 to point 24,
#                    0,0,0,0,1]]).reshape(-1, 1)                                  # barO, barX, trayO, trayX, turn[O=1, X=0]

inputList = json.loads(sys.argv[1])
input = np.array(inputList).reshape(-1, 1)

w = []
for i in range(l):
    w.append(rng.standard_normal((n_l[i+1], n_l[i])) * 0.01)

b = []
for i in range(l):
    b.append(np.zeros(n_l[i+1]).reshape(-1, 1))

def relu(x):
    return np.maximum(0, x)

a = input

for i in range(l):
    a = relu(w[i] @ a + b[i])

print(0.57)
# print(a)