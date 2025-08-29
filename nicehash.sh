#!/bin/bash

cd mining

echo "launching nicehash"

docker run --gpus all --network host -it  mining-app