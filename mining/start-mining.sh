#!/bin/bash

echo "Starting mining..."

# Download NBMiner
wget https://github.com/NebuTech/NBMiner/releases/download/v42.3/NBMiner_42.3_Linux.tgz

# Extract the archive
tar -xzf NBMiner_42.3_Linux.tgz

# Change to NBMiner directory
cd NBMiner_Linux

# Start mining
./nbminer -a kawpow -o stratum+tcp://kawpow.auto.nicehash.com:9200 -u NHbGUbGkHNHmy4PTE1WsnssDianrPUDcctBx.docker-worker
