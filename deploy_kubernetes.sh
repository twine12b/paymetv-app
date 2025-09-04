#!/bin/bash

namespace="default"
dir=conf

mvn package && java -jar target/paymetv-0.0.1-SNAPSHOT.jar
mvn package
docker build -t paymetv-app:latest .
kubectl create ns $namespace

kubectl apply -f ./src/main/resources/${dir}/deployment.yaml -n $namespace
kubectl apply -f ./src/main/resources/${dir}/service.yaml -n $namespace
kubectl apply -f ./src/main/resources/${dir}/ingress.yaml  -n $namespace

kubectl apply -f ./src/main/resources/conf/service.yaml
#kubectl apply -f ./src/main/resources/conf/service2.yaml
kubectl expose deployment pmtv-app --type=LoadBalancer --name=paymetv-service

sleep 20
kubectl get pods --output=wide
curl http://192.168.0.2:80
sleep 10
kubectl get deployments -n $namespace
kubectl get svc paymetv-app-service -n $namespace
kubectl get ingress paymetv-app-ingress -n $namespace

#kubectl delete services paymetv-service && kubectl delete deployment pmtv-app
# URL to request
http="http://"
https="https://"
url="paymetv.co.uk"

# Perform the curl request and capture the status code
status_code=$(curl -o /dev/null -s -w "%{http_code}\n" "${http}$url")

# Print the status code
echo "Status code: $status_code"

# Perform the curl request and capture the status code
status_code=$(curl -o /dev/null -s -w "%{http_code}\n" "${https}$url")

# Print the status code
echo "Status code: $status_code"
