#!/bin/sh

conf_dir="./conf"

#teardown port forwards
#kill $(lsof -t -i:9090) 2>/dev/null || true
#kill $(lsof -t -i:3000) 2>/dev/null || true
#kill $(lsof -t -i:9093) 2>/dev/null || true
#kill $(lsof -t -i:8000) 2>/dev/null || true

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts/
helm repo update

#helm search repo prometheus-community/kube-prometheus-stack --versions
helm install prometheus  prometheus-community/kube-prometheus-stack --version 45.7.1 --namespace monitoring --create-namespace

kubectl expose service prometheus-server --type=LoadBalancer --name=prometheus-server-lb
#kubectl get svc prometheus-server-lb

kubectl get namespace monitoring >/dev/null 2>&1 || kubectl create namespace monitoring


pushd ${conf_dir} > /dev/null
kubectl apply -f .
popd > /dev/null

echo -e "${YELLOW}Waiting for Prometheus to be ready...${NS}"
if ! kubectl -n monitoring rollout status deployment/fastapi-app --timeout=1200s; then
  echo "Timed out or deployment failed. Showing pod status and recent logs for debugging:"
  kubectl -n monitoring get pods -l app=prometheus-server -o wide
  kubectl -n monitoring logs -l app=prometheus-server --tail=200 || true
  exit 1
fi
echo -e "${GREEN}Prometheus is ready${NS}"
echo ""

# port forward Prometheus services
kubectl port-forward -n monitoring svc/prometheus-operated 9090:9090 > /dev/null 2>&1 &
# port forward Grafana services
kubectl port-forward -n monitoring svc/prometheus-grafana 3010:80 > /dev/null 2>&1 &
# port forward Alertmanager services
kubectl port-forward -n monitoring svc/alertmanager-operated 9093:9093 > /dev/null 2>&1 &
# port forward FastAPI services
kubectl port-forward -n monitoring svc/fastapi-app 8000:8000 > /dev/null 2>&1 &
# port forward PayMeTV services
kubectl port-forward -n default svc/paymetv-app-service 8080:80 > /dev/null 2>&1 &



#kubectl port-forward -n monitoring svc/prometheus-operated 9090:9090
#kubectl get secrets -n monitoring prometheus-grafana
#kubectl get secret -n monitoring prometheus-grafana -o jsonpath="{.data.admin-password }" | base64 --decode; echo


