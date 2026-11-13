#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"

# The cert in conf-k3s.yaml creates this secret. Override if using another cert.
SECRET_NAME="${SECRET_NAME:-pmtv-ss-sec}"
ML_IMAGE="${ML_IMAGE:-paymetv/ml-trainer:cpu-latest}"
CUDA_IMAGE="${CUDA_IMAGE:-paymetv/ml-trainer:cuda-latest}"
ISSUER_NAME="${ISSUER_NAME:-pmtv-selfsigned-issuer}"
CERT_MANAGER_VERSION="${CERT_MANAGER_VERSION:-v1.16.3}"
KEDA_NAMESPACE="${KEDA_NAMESPACE:-keda}"

if [[ ! "${SECRET_NAME}" =~ ^[a-z0-9]([-a-z0-9]*[a-z0-9])?$ ]]; then
  echo "SECRET_NAME must be a valid Kubernetes secret name." >&2
  exit 1
fi

if ! command -v kubectl >/dev/null 2>&1; then
  echo "kubectl is required to deploy to k3s." >&2
  exit 1
fi

ensure_namespace() {
  kubectl create namespace "$1" --dry-run=client -o yaml | kubectl apply -f -
}

ensure_mysql_secrets() {
  kubectl -n database get secret mysql-root-pass >/dev/null 2>&1 || \
    kubectl -n database create secret generic mysql-root-pass \
      --from-literal=password=Ch1ldren
  kubectl -n database get secret mysql-user-pass >/dev/null 2>&1 || \
    kubectl -n database create secret generic mysql-user-pass \
      --from-literal=username=todd --from-literal=password=password
  kubectl -n database get secret mysql-db-url >/dev/null 2>&1 || \
    kubectl -n database create secret generic mysql-db-url \
      --from-literal=database=paymetv_db1
}

ensure_cert_manager() {
  if ! kubectl get crd certificates.cert-manager.io >/dev/null 2>&1 || \
     ! kubectl get crd clusterissuers.cert-manager.io >/dev/null 2>&1; then
    echo "Installing cert-manager ${CERT_MANAGER_VERSION}..."
    kubectl apply -f "https://github.com/cert-manager/cert-manager/releases/download/${CERT_MANAGER_VERSION}/cert-manager.yaml"
  fi

  echo "Waiting for cert-manager CRDs to become available..."
  kubectl wait --for=condition=Established --timeout=180s \
    crd/certificates.cert-manager.io crd/clusterissuers.cert-manager.io
  kubectl -n cert-manager rollout status deployment/cert-manager --timeout=180s
  kubectl -n cert-manager rollout status deployment/cert-manager-webhook --timeout=180s
}

ensure_keda() {
  if ! command -v helm >/dev/null 2>&1; then
    echo "helm is required to install KEDA and its HTTP add-on." >&2
    exit 1
  fi

  helm repo add kedacore https://kedacore.github.io/charts --force-update
  helm repo update kedacore
  helm upgrade --install keda kedacore/keda \
    --namespace "${KEDA_NAMESPACE}" --create-namespace \
    --wait --timeout 10m
  helm upgrade --install http-add-on kedacore/keda-add-ons-http \
    --namespace "${KEDA_NAMESPACE}" \
    --wait --timeout 10m

  echo "Waiting for KEDA resource types to become available..."
  kubectl wait --for=condition=Established --timeout=180s \
    crd/scaledobjects.keda.sh crd/interceptorroutes.http.keda.sh
}

echo "Deploying Kafka..."
kubectl get namespace kafka >/dev/null 2>&1 || kubectl create namespace kafka
ensure_namespace kafka
kubectl apply -f "${SCRIPT_DIR}/kafka/zookeeper/conf2/kafka-conf2-k3s.yaml"
bash "${SCRIPT_DIR}/port_forward.sh" start kafka svc/kafka-service :8082 \
  "${SCRIPT_DIR}/kafka/zookeeper/conf2/.portforward.pid" kafka

echo "Deploying database..."
kubectl get namespace database >/dev/null 2>&1 || kubectl create namespace database
ensure_namespace database
ensure_mysql_secrets
kubectl apply -f "${SCRIPT_DIR}/database/conf/database-k3s.yaml"
if ! kubectl -n database rollout status deployment/mysql-deployment --timeout=300s; then
  echo "MySQL did not become ready. Showing pod status and recent logs:" >&2
  kubectl -n database get pods -l app=mysql -o wide || true
  kubectl -n database describe pods -l app=mysql || true
  kubectl -n database logs -l app=mysql --all-containers=true --tail=200 || true
  exit 1
fi
bash "${SCRIPT_DIR}/port_forward.sh" start database svc/mysql-service :3306 \
  "${SCRIPT_DIR}/database/.portforward.pid" database

echo "Deploying streaming..."
kubectl get namespace streaming >/dev/null 2>&1 || kubectl create namespace streaming
ensure_namespace streaming
bash "${SCRIPT_DIR}/port_forward.sh" start streaming svc/streaming-service :3000 \
  "${SCRIPT_DIR}/streaming/.portforward.pid" streaming

echo "Deploying Prometheus resources..."
kubectl get namespace monitoring >/dev/null 2>&1 || kubectl create namespace monitoring
ensure_namespace monitoring
if ! command -v helm >/dev/null 2>&1; then
  echo "helm is required to install Prometheus and its ServiceMonitor CRD." >&2
  exit 1
fi
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts/ --force-update
helm repo update prometheus-community
helm upgrade --install prometheus prometheus-community/kube-prometheus-stack \
  --version 45.7.1 --namespace monitoring --create-namespace \
  --wait --timeout 10m
kubectl wait --for=condition=Established --timeout=180s \
  crd/servicemonitors.monitoring.coreos.com
kubectl apply -f "${SCRIPT_DIR}/prometheus/conf/prometheus-k3s.yaml"
bash "${SCRIPT_DIR}/port_forward.sh" start monitoring svc/prometheus-service :9090 \
  "${SCRIPT_DIR}/prometheus/.portforward.pid" monitoring

echo "Deploying workflow..."
kubectl get namespace workflow >/dev/null 2>&1 || kubectl create namespace workflow
ensure_namespace workflow
kubectl apply -f "${SCRIPT_DIR}/workflow/conf/workflow-k3s.yaml"
bash "${SCRIPT_DIR}/port_forward.sh" start workflow svc/workflow-service :8088 \
  "${SCRIPT_DIR}/workflow/.portforward.pid" workflow

ensure_cert_manager

echo "Deploying PayMeTV application and certificate..."
sed "s|\${SECRET_NAME}|${SECRET_NAME}|g" "${SCRIPT_DIR}/conf/conf-k3s.yaml" | kubectl apply -f -
bash "${SCRIPT_DIR}/port_forward.sh" start conf svc/paymetv-app-service :8080 \
  "${SCRIPT_DIR}/conf/.portforward.pid" conf

echo "Deploying ML resources..."
kubectl get namespace ml >/dev/null 2>&1 || kubectl create namespace ml
sed \
  -e "s|\${IMAGE}|${ML_IMAGE}|g" \
  -e "s|\${CUDA_IMAGE}|${CUDA_IMAGE}|g" \
  "${SCRIPT_DIR}/ml/conf/ml-k3s.yaml" | kubectl apply -f -

echo "Deploying KEDA resources..."
kubectl get namespace keda >/dev/null 2>&1 || kubectl create namespace keda
ensure_namespace "${KEDA_NAMESPACE}"
ensure_namespace ml
ensure_keda
sed "s|\${ISSUER_NAME}|${ISSUER_NAME}|g" \
  "${SCRIPT_DIR}/keda/conf/keda-k3s.yaml" | kubectl apply -f -
#bash "${SCRIPT_DIR}/port_forward.sh" start keda svc/paymetv-app-service :8080 \
#  "${SCRIPT_DIR}/database/.portforward.pid" conf

echo "All requested k3s manifests have been applied."
