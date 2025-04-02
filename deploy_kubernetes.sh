#!/bin/bash

namespace="default"
dir=conf

mvn package && java -jar target/paymetv-0.0.1-SNAPSHOT.jar
mvn package
docker build -t paymetv-app:latest .
kubectl create ns $namespace

echo "waiting for pods to run"
kubectl wait --namespace "$namespace" --for=condition=Ready deployment/"paymetv-app-deployment" --timeout=10s
echo "pods created"

echo "waiting for service to run"
kubectl wait --namespace "$namespace" --for=condition=Ready service/"paymetv-app-service" --timeout=60s
echo "service created"

echo "waiting for ingress to run"
kubectl wait --namespace "$namespace" --for=condition=Ready ingress/"paymetv-app-ingress" --timeout=60s
echo "ingress created"

#
#kubectl apply -f "src/main/resources/${dir}/dep-before.yaml" -n $namespace
#
#
#
#kubectl apply -f ./src/main/resources/${dir}/deployment.yaml -n $namespace
#kubectl apply -f ./src/main/resources/${dir}/service.yaml -n $namespace
#kubectl apply -f ./src/main/resources/${dir}/ingress.yaml  -n $namespace
#
#
#sleep 10
#kubectl get deployments -n $namespace
#kubectl get svc paymetv-app-service -n $namespace
#kubectl get ingress paymetv-app-ingress -n $namespace
#
## URL to request
#http="http://"
#https="https://"
#url="paymetv.co.uk"
#
## Perform the curl request and capture the status code
#status_code=$(curl -o /dev/null -s -w "%{http_code}\n" "${http}$url")
#
## Print the status code
#echo "Status code: $status_code"
#
## Perform the curl request and capture the status code
#status_code=$(curl -o /dev/null -s -w "%{http_code}\n" "${https}$url")
#
## Print the status code
#echo "Status code: $status_code"