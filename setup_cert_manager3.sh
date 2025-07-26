#!/bin/bash

# only need this line if ./deploy.yaml does not exist
# wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.12.0/deploy/static/provider/cloud/deploy.yaml

# kubectl apply -f src/main/resources/conf/dep-before.yaml

kubectl apply -f src/main/resources/conf/nginx-deploy.yaml
slep 15

kubectl apply -f ./deploy.yaml

sleep 30

kubectl apply -f src/main/resources/conf/dep-before.yaml

sleep 40

kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.16.3/cert-manager.yaml

sleep 30

kubectl create -f src/main/resources/conf/acme-prod-issuer.yaml
kubectl create -f src/main/resources/conf/acme-prod-cert.yaml

sleep 120

kubectl apply -f src/main/resources/conf/dep-before.yaml