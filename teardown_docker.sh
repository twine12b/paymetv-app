#!/bin/bash

echo "tearing down paymetv-app"
kubectl -n default scale deployment paymetv-app-deployment --replicas=0
kubectl -n default delete deployment paymetv-app-deployment

kubectl -n default delete certs pmtv-acme-http-stag-cert
kubectl -n default delete certs pmtv-acme-http-stage-cert
kubectl -n default delete certs pmtv-acme-http-stage-sec

echo "pruning docker "
docker system prune -a --filter label!="myimage=keepit"