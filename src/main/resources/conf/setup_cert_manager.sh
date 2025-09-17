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
kubectl apply -f acme-prod-issuer.yaml

#Create certificate
kubectl apply -f self-signed-cert.yaml
kubectl apply -f acme-staging-cert.yaml
kubectl apply -f acme-prod-cert.yaml

#Deploy the app with secret and wait until it is ready
kubectl apply -f dep-after.yaml
echo "Waiting for paymetv-app' pod to be ready..."
until kubectl -n ${namespace} get pods --no-headers | grep paymetv-app-deployment | awk '{print $1}' | \
    xargs -I{} kubectl -n ${namespace} get pod {} -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' | grep -q True; do
  sleep 5
  echo "Still waiting..."
done
echo "paymetv-app 'deployment' pod is ready."