#!/bin/bash

#mvn package && java -jar target/paymetv-0.0.1-SNAPSHOT.jar
mvn package
docker build -t paymetv-app:latest .

kubectl apply -f ./src/main/resources/conf2/service.yaml
kubectl expose deployment pmtv-app --type=LoadBalancer --name=paymetv-service

sleep 10
kubectl get pods --output=wide
curl http://192.168.0.2:80

#kubectl delete services paymetv-service && kubectl delete deployment pmtv-app