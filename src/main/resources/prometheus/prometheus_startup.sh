#!/bin/sh

conf_dir="./conf"

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts/
helm repo update

#helm search repo prometheus-community/kube-prometheus-stack --versions
helm install prometheus  prometheus-community/kube-prometheus-stack --version 45.7.1 --namespace monitoring --create-namespace

kubectl expose service prometheus-server --type=LoadBalancer --name=prometheus-server-lb
#kubectl get svc prometheus-server-lb

pushd ${conf_dir} > /dev/null
kubectl apply -f .
popd > /dev/null

# port forward Prometheus services
kubectl port-forward -n monitoring svc/prometheus-operated 9090:9090 > /dev/null 2>&1 &
# port forward Grafana services
kubectl port-forward -n monitoring svc/prometheus-grafana 3000:80 > /dev/null 2>&1 &
# port forward Alertmanager services
kubectl port-forward -n monitoring svc/alertmanager-operated 9093:9093 > /dev/null 2>&1 &
# port forward FastAPI services
kubectl port-forward -n monitoring svc/fastapi-app 8000:8000 > /dev/null 2>&1 &
# port forward PayMeTV services
kubectl port-forward -n default svc/paymetv-app-service 8080:80 > /dev/null 2>&1 &



#kubectl port-forward -n monitoring svc/prometheus-operated 9090:9090
#kubectl get secrets -n monitoring prometheus-grafana
#kubectl get secret -n monitoring prometheus-grafana -o jsonpath="{.data.admin-password }" | base64 --decode; echo


