#!/bin/bash

set -e  # Exit on error

namespace="default"
dir=conf8
frontend_dir="src/main/resources/frontend"
streaming_dir="src/main/resources/streaming"
startup_dir="src/main/resources/conf"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE} PayMeTV Release New Version ${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

echo -e ${YELLOW}${streaming_dir}${NC}
echo -e ${YELLOW}${frontend_dir}${NC}
echo ""

# Step 1: Build frontend
echo -e "${YELLOW}Step 1: Build Frontend...${NC}"
pushd $frontend_dir > /dev/null
npm install
npm run build
echo -e "${GREEN}✓ Frontend build complete${NC}"
popd > /dev/null
echo ""

# Step 2: Build backend
echo -e "${YELLOW}Step 1: Build Backend...${NC}"
[ -d target ] && rm -r ./target/
#mvn package && java -jar ./target/paymetv-app-0.0.1-SNAPSHOT.jar  # Not needed during build process
mvn clean package
echo -e "${GREEN}✓ Backend build complete${NC}"
echo ""

# Step 3: Build and push Docker images
echo -e "${YELLOW}Step 1: Build Images...${NC}"
docker build -t paymetv-app:latest .
echo $streaming_dir
pushd $streaming_dir > /dev/null
docker build -t streaming-app:latest  .
popd > /dev/null

# Log in to Docker Hub
docker login

# Tag the Docker image
docker tag paymetv-app:latest paymetv/paymetv-app:latest
docker tag streaming-app:latest paymetv/streaming-app:latest

# Push the Docker image to Docker Hub
docker push paymetv/paymetv-app:latest
docker push paymetv/streaming-app:latest
#echo -e "${GREEN}✓ Backend build complete${NC}"

# Step 4: Deploy to Kubernetes
echo -e "${YELLOW}Step 1: Deploying to Kubernetes...${NC}"
pushd $startup_dir > /dev/null
./setup_cert_manager.sh prod
popd > /dev/null
echo ""
echo -e "${GREEN}✓ Deployment complete${NC}"
echo ""

# Step 5: Verify deployment
echo -e "${YELLOW}Step 1: Verifying deployment...${NC}"
kubectl get pods -n $namespace
kubectl get svc paymetv-app-service -n $namespace
kubectl get ingress paymetv-app-ingress -n $namespace
echo -e "${GREEN}✓ Verification complete${NC}"
echo ""

# Step 6: Final summary
echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}✓ Setup Complete!${NC}"
echo -e "${BLUE}=========================================${NC}"
echo "Built and deployed components:"
echo "  ✓ Frontend"
echo "  ✓ Backend"
echo "  ✓ Streaming"
echo "  ✓ Kubernetes"
echo "  ✓ Docker"
echo "  ✓ Docker Hub"
echo "  ✓ Cert-Manager"
echo "  ✓ Ingress NGINX"
echo "  ✓ TLS Certificates"
echo -e "${GREEN}PayMeTV is now available at https://paymetv.co.uk${NC}"
echo -e "${YELLOW}Done!${NC}"
echo ""

## URL to request
#http="http://"
#https="https://"
#url="paymetv.co.uk"
#
## Perform the curl request and capture the status code
#status_code=$(curl -o /dev/null -s -w "%{http_code}\n" "${http}$url")
#echo "Status code: $status_code"
#
#status_code=$(curl -o /dev/null -s -w "%{http_code}\n" "${https}$url")
#
#echo "Status code: $status_code"
