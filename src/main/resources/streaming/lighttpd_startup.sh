#!/bin/sh

set -e  # Exit on error


STREAMING_DIR="./conf"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

#docker rm "$(docker ps -q --filter "publish=8081")" 2>/dev/null || true
#docker build -t paymetv/streaming-app:latest . -f Dockerfile
#docker run -d -p 8081:80 -it paymetv/streaming-app:latest
#curl -I http://localhost:8081/ | grep HTTP/1.1

docker build -t paymetv/streaming-app:latest . -f Dockerfile

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
kubectl -n streaming apply -f ${STREAMING_DIR}/deployment.yaml
echo -e "${GREEN}✓ deployment LIGHTTPD deployed${NC}"
kubectl -n streaming apply -f ${STREAMING_DIR}/service.yaml
echo -e "${GREEN}✓ Service LIGHTTPD deployed${NC}"
envsubst < ${STREAMING_DIR}/ingress.yaml | kubectl -n streaming apply -f -
echo -e "${GREEN}✓ Ingress Lighttpd deployed${NC}"
echo ""

# port forward PayMeTV services
#kubectl port-forward -n default svc/paymetv-app-service 8080:80 > /dev/null 2>&1 &