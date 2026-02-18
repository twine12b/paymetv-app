#!/bin/sh

# Create the database namespace if it doesn't exist
kubectl get namespace database >/dev/null 2>&1 || kubectl create namespace database

# Set the namespace for the current context
kubectl config set-context --current --namespace=database

# Create secret Mysql root password (delete if exists, then create)
kubectl delete secret mysql-root-pass -n database --ignore-not-found=true
kubectl create secret generic mysql-root-pass --from-literal=password=Ch1ldren -n database

# Create MySql user password (delete if exists, then create)
kubectl delete secret mysql-user-pass -n database --ignore-not-found=true
kubectl create secret generic mysql-user-pass --from-literal=username=todd --from-literal=password=password -n database

# Create secret MySql url (delete if exists, then create)
kubectl delete secret mysql-db-url -n database --ignore-not-found=true
kubectl create secret generic mysql-db-url --from-literal=database=paymetv_db1 -n database

kubectl apply -f conf/mysql.yaml

# Wait for MySQL to be ready
#kubectl get pods -l app=mysql -n database --watch

