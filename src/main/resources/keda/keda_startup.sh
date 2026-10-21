#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd -P)"
MANIFEST_DIR="${SCRIPT_DIR}/conf"
ENVIRONMENT="${1:-local}"

ROOT_DIR="$(CDPATH= cd -- "${SCRIPT_DIR}/../../../.." && pwd -P)"
source "${ROOT_DIR}/scripts/commons/util.sh"
KEDA_NAMESPACE="keda"

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}PayMeTV Keda Setup Script${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

for command_name in kubectl helm envsubst; do
  if ! command -v "${command_name}" >/dev/null 2>&1; then
    echo "${command_name} is required to install KEDA and apply its manifests." >&2
    exit 1
  fi
done

case "${ENVIRONMENT}" in
  prod) ISSUER_NAME="pmtv-acme-http-prod" ;;
  dev) ISSUER_NAME="pmtv-acme-http-stage" ;;
  local) ISSUER_NAME="pmtv-selfsigned-issuer" ;;
  *) echo "Environment must be 'prod', 'dev', or 'local'." >&2; exit 1 ;;
esac
export ISSUER_NAME

echo -e "${YELLOW}Installing or updating KEDA and the KEDA HTTP Add-on...${NC}"
helm repo add kedacore https://kedacore.github.io/charts --force-update
helm repo update kedacore
helm upgrade --install keda kedacore/keda \
  --namespace "${KEDA_NAMESPACE}" --create-namespace \
  --wait --timeout 10m
helm upgrade --install http-add-on kedacore/keda-add-ons-http \
  --namespace "${KEDA_NAMESPACE}" \
  --wait --timeout 10m
echo -e "${GREEN}✓ Keda install complete.${NC}"
echo ""

kubectl wait --for=condition=Established --timeout=120s \
  crd/scaledobjects.keda.sh
kubectl wait --for=condition=Established --timeout=120s \
  crd/interceptorroutes.http.keda.sh

# Keep the namespaces available even when their application startup is run later.
kubectl get namespace ml >/dev/null 2>&1 || kubectl create namespace ml
kubectl get namespace streaming >/dev/null 2>&1 || kubectl create namespace streaming

echo -e "${YELLOW}Apply Keda manifests...${NC}"
kubectl apply -f "${MANIFEST_DIR}/ml-interceptorroute.yaml"
kubectl apply -f "${MANIFEST_DIR}/ml-scaledobject.yaml"
kubectl apply -f "${MANIFEST_DIR}/streaming-scaledobject.yaml"
envsubst < "${MANIFEST_DIR}/ml-certificate.yaml" | kubectl apply -f -
kubectl -n "${KEDA_NAMESPACE}" wait --for=condition=Ready \
  certificate/ml-trainer-ingress-cert --timeout=10m
kubectl apply -f "${MANIFEST_DIR}/ml-ingress.yaml"
echo -e "${GREEN}✓ Keda manifests deployed${NC}"
echo ""

echo -e "${YELLOW}KEDA and its HTTP Add-on are ready. ${NC}"
echo -e "${YELLOW}KEDA manifests applied from ${MANIFEST_DIR}. ${NC}"
