#!/bin/sh

set -e  # Exit on error

NAMESPACE="workflow"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
MANIFEST_FILE="${SCRIPT_DIR}/conf/workflow.yaml"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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
if ! kubectl -n "${NAMESPACE}" rollout status deployment/temporal --timeout=300s; then
echo -e "${YELLOW}Waiting for deployments to become ready...${NC}"
  kubectl -n "${NAMESPACE}" rollout status deployment/postgres --timeout=300s
  kubectl -n "${NAMESPACE}" rollout status deployment/temporal --timeout=300s
  kubectl -n "${NAMESPACE}" rollout status deployment/temporal-ui --timeout=300s
fi
echo -e "${GREEN}✓ Temporal stack is ready${NC}"
echo ""

# Port forward Temporal UI
kubectl port-forward -n "${NAMESPACE}" svc/temporal-ui 8088:8080 > /dev/null 2>&1 &
echo -e "${GREEN}✓ Temporal UI port-forward started at http://localhost:8088${NC}"
