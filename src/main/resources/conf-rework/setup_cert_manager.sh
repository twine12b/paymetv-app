#!/bin/bash

namespace=default

#wget https://raw.githubusercontent.com/gurlal-1/devops-avenue/refs/heads/main/yt-videos/kind-cert-manager/script/set_cluster.sh

kubectl create namespace ingress-nginx
kubectl -n ${namespace} apply -f deploy.yaml

# Wait for ingress-nginx pod with 'controller' in its name to be ready
echo "Waiting for ingress-nginx 'controller' pod to be ready..."
until kubectl -n ${namespace} get pods --no-headers | grep controller | awk '{print $1}' | \
  xargs -I{} kubectl -n ${namespace} get pod {} -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' | grep -q True; do
  sleep 5
  echo "Still waiting..."
done
echo "ingress-nginx 'controller' pod is ready."

#Deploy the app and wait until it is ready
kubectl apply -f dep-before.yaml
echo "Waiting for paymetv-app' pod to be ready..."
until kubectl -n ${namespace} get pods --no-headers | grep paymetv-app-deployment | awk '{print $1}' | \
    xargs -I{} kubectl -n ${namespace} get pod {} -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' | grep -q True; do
  sleep 5
  echo "Still waiting..."
done
echo "paymetv-app 'deployment' pod is ready."

#Install cert-manager
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.16.3/cert-manager.yaml
echo "Waiting for cert-manager' pod to be ready..."
until kubectl -n cert-manager get pods --no-headers | grep cert-manager-webhook | awk '{print $1}' | \
    xargs -I{} kubectl -n cert-manager get pod {} -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' | grep -q True; do
  sleep 5
  echo "Still waiting..."
done
echo "cert-manager pod is ready."

#install issuer
echo "Installing prod issuer"
kubectl apply -f self-signed-issuer.yaml
kubectl apply -f acme-staging-issuer.yaml
#kubectl apply -f acme-prod-issuer.yaml

#Create certificate
kubectl apply -f self-signed-cert.yaml
kubectl apply -f acme-staging-cert.yaml
#kubectl apply -f acme-prod-cert.yaml

#Deploy the app with secret and wait until it is ready
kubectl apply -f dep-after.yaml
echo "Waiting for paymetv-app' pod to be ready..."
until kubectl -n ${namespace} get pods --no-headers | grep paymetv-app-deployment | awk '{print $1}' | \
    xargs -I{} kubectl -n ${namespace} get pod {} -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' | grep -q True; do
  sleep 5
  echo "Still waiting..."
done
echo "paymetv-app 'deployment' pod is ready."









# -------------------------------------------------------

#namespace="uk-paymetv-1"

#kubectl config get-contexts -o name
#kubectl config use-context docker-desktop

#create a cluster
#kind create cluster --name certmanager --image kindest/node:v1.19.1
#kubectl get nodes
#kubectl create ns cert-manager
#kubectl apply --validate=false -f src/main/resources/conf6/cert-manager-1.0.4.yaml
#kubectl -n cert-manager get all

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
#kubectl apply -f src/main/resources/conf6/cert-issuer-nginx-ingress.yaml
#kubectl describe clusterissuer letsencrypt-cluster-issuer

# check the self-signed-issuer.yaml
#kubectl describe clusterissuer letsencrypt-cluster-issuer

#kubectl port-forward svc/paymetv-app-service 80:80
