#!/bin/bash

echo "Starting mining..."

RUN wget https://github.com/NebuTech/NBMiner/releases/download/v42.3/NBMiner_42.3_Linux.tgz && \
    tar -xzf NBMiner_42.3_Linux.tgz && \
    rm NBMiner_42.3_Linux.tgz

RUN cd /NBMiner_Linux

 ./nbminer -a kawpow -o stratum+tcp://kawpow.auto.nicehash.com:9200 -u NHbGUbGkHNHmy4PTE1WsnssDianrPUDcctBx.docker-worker