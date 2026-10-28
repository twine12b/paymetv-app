#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd -P)"
KUBERNETES_DIR="${SCRIPT_DIR}/conf"

ROOT_DIR="$(cd -- "${SCRIPT_DIR}/../../../.." && pwd -P)"
source "${ROOT_DIR}/scripts/commons/util.sh"

NAMESPACE="ml"
IMAGE_FLAVOR="${ML_IMAGE_FLAVOR:-cpu}"
PORT_FORWARD_PID_FILE="${SCRIPT_DIR}/.portforward.pid"

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}PayMeTV Machine Learning Setup Script${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

echo -e "${YELLOW}Deciding on which flavour [CPU/CUDA]...${NC}"
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
echo -e "${GREEN}Using ML machine with ${IMAGE_FLAVOR} flavor...${NC}"
echo ""

BUILD_VERSION="$(get_timestamp_version)"
IMAGE="paymetv/ml-trainer:${IMAGE_FLAVOR}-${BUILD_VERSION}"
echo -e "${YELLOW}Checking the ML ${IMAGE_FLAVOR} image checksum for ${IMAGE}...${NC}"

kubectl apply -f "${KUBERNETES_DIR}/namespace.yaml"
kubectl -n "${NAMESPACE}" apply -f "${KUBERNETES_DIR}/pvc.yaml"

if ! kubectl -n "${NAMESPACE}" get secret ml-api-auth >/dev/null 2>&1; then
  if [ -n "${ML_API_TOKEN:-}" ]; then
    API_TOKEN="${ML_API_TOKEN}"
  else
    if ! command -v openssl >/dev/null 2>&1; then
      echo "Install openssl or set ML_API_TOKEN before starting the ML service." >&2
      exit 1
    fi
    API_TOKEN="$(openssl rand -hex 32)"
  fi
  kubectl -n "${NAMESPACE}" create secret generic ml-api-auth \
    --from-literal="token=${API_TOKEN}"
  unset API_TOKEN
fi

CHECKSUM_KEY="${IMAGE_FLAVOR}" sh "${ROOT_DIR}/scripts/commons/checksum.sh" \
  "${SCRIPT_DIR}" \
  "${SCRIPT_DIR}/Dockerfile" \
  bash -c 'set -euo pipefail
           docker build --target "$1" -t "paymetv/ml-trainer:$1-$2" "$3"
           IMAGE="paymetv/ml-trainer:$1-$2" envsubst < "$4" | kubectl -n "$5" apply -f -
           kubectl -n "$5" rollout restart deployment/ml-trainer' \
  bash "${IMAGE_FLAVOR}" "${BUILD_VERSION}" "${SCRIPT_DIR}" "${DEPLOYMENT_MANIFEST}" "${NAMESPACE}"

kubectl -n "${NAMESPACE}" apply -f "${KUBERNETES_DIR}/service.yaml"
kubectl -n "${NAMESPACE}" apply -f "${KUBERNETES_DIR}/network-policy.yaml"

echo "Waiting for the ML trainer deployment..."
kubectl -n "${NAMESPACE}" rollout status deployment/ml-trainer --timeout=1m

# Cleanup: Stop existing port-forward process - TODO move to common script keep (DRY)
echo -e "${YELLOW}Cleanup: Stopping existing port-forward process...${NC}"
if [ -f "${PORT_FORWARD_PID_FILE}" ]; then
  PID=$(cat "${PORT_FORWARD_PID_FILE}" 2>/dev/null || true)
  if [ -n "${PID}" ] && ps -p "${PID}" > /dev/null 2>&1; then
    kill "${PID}" 2>/dev/null || true
    echo -e "${GREEN}✓ Stopped port-forward process (PID: $PID)${NC}"
  else
    echo -e "${YELLOW}  No running port-forward process found${NC}"
  fi
  rm -f "${PORT_FORWARD_PID_FILE}" 2>/dev/null || true
else
  echo -e "${YELLOW}  No previous port-forward PID file found${NC}"
fi
echo ""

# Port forward to the Machine Learning - this is only required for local development
echo -e "${YELLOW} Port-forwarding to the Machine Learning Server ...${NC}"
kubectl port-forward -n "${NAMESPACE}" svc/ml-trainer 18080:8080 > /dev/null 2>&1 &
PORT_FORWARD_PID=$!
echo "${PORT_FORWARD_PID}" > "${PORT_FORWARD_PID_FILE}"

# Give kubectl time to bind the local port and fail clearly if the forward cannot start.
PORT_FORWARD_READY=false
for _ in {1..10}; do
  if ! kill -0 "${PORT_FORWARD_PID}" 2>/dev/null; then
    break
  fi
  if (echo > /dev/tcp/127.0.0.1/18080) >/dev/null 2>&1; then
    PORT_FORWARD_READY=true
    break
  fi
  sleep 1
done

if [ "${PORT_FORWARD_READY}" != true ]; then
  kill "${PORT_FORWARD_PID}" 2>/dev/null || true
  rm -f "${PORT_FORWARD_PID_FILE}"
  echo "Failed to start the ML port-forward on localhost:18080." >&2
  exit 1
fi

echo -e "${GREEN}✓ Machine Learning port-forwarded at http://localhost:18080${NC}"
echo ""

echo -e "${YELLOW}ML trainer deployed with the ${IMAGE_FLAVOR} image.${NC}"
echo -e "${YELLOW}The KEDA ingress and scaling resources are deployed separately by resources/keda/keda_startup.sh.${NC}"
echo -e "${YELLOW}Endpoint: https://paymetv.co.uk/ml${NC}"
echo -e "${YELLOW}Bearer token is stored in secret ml-api-auth in namespace ${NAMESPACE}.${NC}"
echo -e "${YELLOW}Retrieve it with: kubectl -n ml get secret ml-api-auth -o jsonpath='{.data.token}'${NC}"
echo -e "${YELLOW}The returned value is base64 encoded.${NC}"
echo ""
