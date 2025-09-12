#!/bin/bash

echo "starting mining"

CMD wget https://github.com/NebuTech/NBMiner/releases/download/v42.3/NBMiner_42.3_Linux.tgz
CMD tar -xzf NBMiner_42.3_Linux.tgz
CMD cd /NBMiner_Linux

CMD ./nbminer -a kawpow -o stratum+tcp://kawpow.auto.nicehashcom:9200 -u NHbGUbGkHNHmy4PTE1WsnssDianrPUDcctBx.docker-worker