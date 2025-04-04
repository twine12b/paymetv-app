#!/bin/bash

namespace="default"
dir=conf8

mvn package && java -jar target/paymetv-0.0.1-SNAPSHOT.jar
mvn package
docker build -t paymetv-app:latest .

# Log in to Docker Hub
docker login

# Tag the Docker image
docker tag paymetv-app:latest paymetv/paymetv-app:latest

# Push the Docker image to Docker Hub
docker push your-dockerhub-username/paymetv-app:latest

kubectl create ns $namespace
kubectl apply -f ./src/main/resources/${dir}/deployment.yaml -n $namespace
kubectl apply -f ./src/main/resources/${dir}/service.yaml -n $namespace
kubectl apply -f ./src/main/resources/${dir}/ingress.yaml -n $namespace

sleep 10
kubectl get deployments -n $namespace
kubectl get svc paymetv-app-service -n $namespace
kubectl get ingress paymetv-app-ingress -n $namespace

# URL to request
http="http://"
https="https://"
url="paymetv.co.uk"

# Perform the curl request and capture the status code
status_code=$(curl -o /dev/null -s -w "%{http_code}\n" "${http}$url")
echo "Status code: $status_code"

status_code=$(curl -o /dev/null -s -w "%{http_code}\n" "${https}$url")

echo "Status code: $status_code"
