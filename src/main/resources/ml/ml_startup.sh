#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd -P)"
KUBERNETES_DIR="${SCRIPT_DIR}/conf"
ML_NAMESPACE="ml"
IMAGE_FLAVOR="${ML_IMAGE_FLAVOR:-cpu}"

if ! command -v kubectl >/dev/null 2>&1; then
  echo "kubectl is required to deploy the ML service." >&2
  exit 1
fi
if ! command -v docker >/dev/null 2>&1; then
  echo "Docker is required to build the ML service image." >&2
  exit 1
fi

case "${IMAGE_FLAVOR}" in
  cpu) DEPLOYMENT_MANIFEST="${KUBERNETES_DIR}/deployment.yaml" ;;
  cuda) DEPLOYMENT_MANIFEST="${KUBERNETES_DIR}/deployment.cuda.yaml" ;;
  *) echo "ML_IMAGE_FLAVOR must be 'cpu' or 'cuda'." >&2; exit 1 ;;
esac

IMAGE="paymetv/ml-trainer:${IMAGE_FLAVOR}"
echo "Building the ML ${IMAGE_FLAVOR} image: ${IMAGE}"
docker build --target "${IMAGE_FLAVOR}" -t "${IMAGE}" "${SCRIPT_DIR}"

kubectl apply -f "${KUBERNETES_DIR}/namespace.yaml"
kubectl -n "${ML_NAMESPACE}" apply -f "${KUBERNETES_DIR}/pvc.yaml"

if ! kubectl -n "${ML_NAMESPACE}" get secret ml-api-auth >/dev/null 2>&1; then
  if [ -n "${ML_API_TOKEN:-}" ]; then
    API_TOKEN="${ML_API_TOKEN}"
  else
    if ! command -v openssl >/dev/null 2>&1; then
      echo "Install openssl or set ML_API_TOKEN before starting the ML service." >&2
      exit 1
    fi
    API_TOKEN="$(openssl rand -hex 32)"
  fi
  kubectl -n "${ML_NAMESPACE}" create secret generic ml-api-auth \
    --from-literal="token=${API_TOKEN}"
  unset API_TOKEN
fi

kubectl -n "${ML_NAMESPACE}" apply -f "${DEPLOYMENT_MANIFEST}"
kubectl -n "${ML_NAMESPACE}" apply -f "${KUBERNETES_DIR}/service.yaml"
kubectl -n "${ML_NAMESPACE}" apply -f "${KUBERNETES_DIR}/network-policy.yaml"

echo "Waiting for the ML trainer deployment..."
kubectl -n "${ML_NAMESPACE}" rollout status deployment/ml-trainer --timeout=10m

echo "ML trainer deployed with the ${IMAGE_FLAVOR} image."
echo "The KEDA ingress and scaling resources are deployed separately by resources/keda/keda_startup.sh."
echo "Endpoint: https://paymetv.co.uk/ml"
echo "Bearer token is stored in secret ml-api-auth in namespace ${ML_NAMESPACE}."
echo "Retrieve it with: kubectl -n ml get secret ml-api-auth -o jsonpath='{.data.token}'"
echo "The returned value is base64 encoded."
