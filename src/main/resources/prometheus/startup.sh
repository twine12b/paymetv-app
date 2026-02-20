#!/bin/sh

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts/
helm repo update
#helm search repo prometheus-community/kube-prometheus-stack --versions
helm install prometheus  prometheus-community/kube-prometheus-stack --version 45.7.1 --namespace monitoring --create-namespace
#kubectl expose service prometheus-server --type=LoadBalancer --name=prometheus-server-lb
#kubectl get svc prometheus-server-lb

kubectl port-forward -n monitoring svc/prometheus-operated 9090:9090
#kubectl get secrets -n monitoring prometheus-grafana
#kubectl get secret -n monitoring prometheus-grafana -o jsonpath="{.data.admin-password }" | base64 --decode; echo


