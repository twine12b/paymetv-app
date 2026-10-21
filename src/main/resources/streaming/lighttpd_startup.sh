#!/bin/sh

set -e  # Exit on error

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P)"
STREAMING_DIR="${SCRIPT_DIR}/conf"

ROOT_DIR="$(CDPATH= cd -- "${SCRIPT_DIR}/../../../.." && pwd -P)"
source "${ROOT_DIR}/scripts/commons/util.sh"

#docker rm "$(docker ps -q --filter "publish=8081")" 2>/dev/null || true
#docker build -t paymetv/streaming-app:latest . -f Dockerfile
#docker run -d -p 8081:80 -it paymetv/streaming-app:latest
#curl -I http://localhost:8081/ | grep HTTP/1.1


echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}PayMeTV Streaming Server Setup Script${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

sh "${ROOT_DIR}/scripts/commons/checksum.sh" \
    "$SCRIPT_DIR" \
    "${SCRIPT_DIR}/Dockerfile" \
    sh -c 'docker build -t paymetv/streaming-app:latest "$1" -f "$1/Dockerfile" && (kubectl get namespace streaming >/dev/null 2>&1 || kubectl create namespace streaming) && kubectl -n streaming apply -f "$1/conf/deployment.yaml" && kubectl -n streaming rollout restart deployment/lighttpd' \
    sh "$SCRIPT_DIR"

kubectl get namespace streaming >/dev/null 2>&1 || kubectl create namespace streaming

# Set the namespace for the current context
echo -e "${YELLOW}Setting namespace for current context...${NC}"
kubectl config set-context --current --namespace=streaming
echo -e "${GREEN}✓ Namespace set${NC}"
echo ""

# Deploy lighttpd
kubectl get namespace ingress-nginx >/dev/null 2>&1 || kubectl create namespace ingress-nginx
# Deploy Lighttpd
echo -e "${YELLOW}Setting up Ingress NGINX...${NC}"
#kubectl create namespace streaming --dry-run=client -o yaml | kubectl apply -f -
echo -e "${GREEN}✓ LIGHTTPD deployment checksum check complete${NC}"
kubectl -n streaming apply -f ${STREAMING_DIR}/service.yaml
echo -e "${GREEN}✓ Service LIGHTTPD deployed${NC}"
envsubst < ${STREAMING_DIR}/ingress.yaml | kubectl -n streaming apply -f -
echo -e "${GREEN}✓ Ingress Lighttpd deployed${NC}"
echo ""

# port forward PayMeTV services
kubectl port-forward -n streaming svc/lighttpd 3000:80 > /dev/null 2>&1 &
