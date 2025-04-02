#!/bin/bash

namespace="uk-paymetv-1"

#kubectl config get-contexts -o name
kubectl config use-context docker-desktop

#create a cluster
kind create cluster --name certmanager --image kindest/node:v1.19.1
kubectl get nodes
kubectl create ns cert-manager
kubectl apply --validate=false -f src/main/resources/conf6/cert-manager-1.0.4.yaml
kubectl -n cert-manager get all

## test certificate creation
#kubectl create ns cert-manager-test
#kubectl apply -f src/main/resources/conf6/issuer_ss.yaml
#kubectl apply -f src/main/resources/conf6/certificate.yaml
#kubectl -n cert-manager-test describe certificate
#kubectl -n cert-manager-test get secrets
#kubectl delete ns cert-manager-test

# Deploy an ingress controller
#kubectl create ns ingress-nginx
#kubectl -n ingress-nginx apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v0.41.2/deploy/static/provider/cloud/deploy.yaml
#kubectl -n ingress-nginx get pods
#kubectl -n ingress-nginx --address 0.0.0.0 port-forward svc/ingress-nginx-controller 80
#kubectl -n ingress-nginx --address 0.0.0.0 port-forward svc/ingress-nginx-controller 443
#nohup kubectl -n ingress-nginx --address 0.0.0.0 port-forward svc/ingress-nginx-controller 80 > port-forward.log 2>&1 &
#nohup kubectl -n ingress-nginx --address 0.0.0.0 port-forward svc/ingress-nginx-controller 443 > port-forward.log 2>&1 &


# Deploy a self-signed-issuer.yaml
kubectl apply -f src/main/resources/conf6/cert-issuer-nginx-ingress.yaml
kubectl describe clusterissuer letsencrypt-cluster-issuer

# check the self-signed-issuer.yaml
kubectl describe clusterissuer letsencrypt-cluster-issuer

#kubectl port-forward svc/paymetv-app-service 80:80
