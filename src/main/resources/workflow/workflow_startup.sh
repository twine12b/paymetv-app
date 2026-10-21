#!/bin/sh

set -e  # Exit on error

ROOT_DIR="../../../.."
source "${ROOT_DIR}/scripts/commons/util.sh"

NAMESPACE="workflow"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
MANIFEST_FILE="${SCRIPT_DIR}/conf/workflow.yaml"

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}Temporal Workflow Kubernetes Startup${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

if [ ! -f "${MANIFEST_FILE}" ]; then
  echo -e "${YELLOW}Manifest not found: ${MANIFEST_FILE}${NC}"
  exit 1
fi

kubectl get namespace "${NAMESPACE}" >/dev/null 2>&1 || kubectl create namespace "${NAMESPACE}"

# Set the namespace for the current context
echo -e "${YELLOW}Setting namespace for current context...${NC}"
kubectl config set-context --current --namespace="${NAMESPACE}"
echo -e "${GREEN}✓ Namespace set${NC}"
echo ""

# Deploy Temporal workflow stack
echo -e "${YELLOW}Deploying Temporal workflow stack...${NC}"
kubectl apply -f "${MANIFEST_FILE}"
echo -e "${GREEN}✓ Manifests applied${NC}"
echo -e "${YELLOW}Waiting for deployments to become ready...${NC}"
kubectl -n "${NAMESPACE}" rollout status deployment/postgres --timeout=300s
kubectl -n "${NAMESPACE}" rollout status deployment/temporal --timeout=300s
kubectl -n "${NAMESPACE}" rollout status deployment/temporal-ui --timeout=300s
echo -e "${GREEN}✓ Temporal stack is ready${NC}"
echo ""

# Port forward Temporal UI
pkill -f "kubectl port-forward.*8088" > /dev/null 2>&1 || true
kubectl port-forward -n "${NAMESPACE}" deployment/temporal-ui 8088:8080 > /dev/null 2>&1 &
echo -e "${GREEN}✓ Temporal UI port-forward started at http://localhost:8088${NC}"
