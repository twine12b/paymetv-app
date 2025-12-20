#!/bin/bash

# Deploy PayMeTV application with HPA configuration
# Usage: ./deploy-hpa-setup.sh [namespace]

set -e

NAMESPACE="${1:-default}"
K8S_DIR="src/main/resources/k8s"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}PayMeTV HPA Deployment${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Step 1: Verify metrics-server
echo -e "${YELLOW}Step 1: Verifying metrics-server...${NC}"
./scripts/verify-metrics-server.sh
echo ""

# Step 2: Build and push Docker image
echo -e "${YELLOW}Step 2: Building Docker image...${NC}"
echo "Building frontend..."
cd src/main/resources/frontend
npm run build
cd ../../../..

echo "Building Spring Boot application..."
mvn clean package -DskipTests

echo "Building Docker image..."
docker build -t paymetv/paymetv-app:latest .

echo -e "${GREEN}✓ Docker image built${NC}"
echo ""

# Step 3: Create namespace if it doesn't exist
echo -e "${YELLOW}Step 3: Creating namespace (if needed)...${NC}"
kubectl create namespace "${NAMESPACE}" --dry-run=client -o yaml | kubectl apply -f -
echo -e "${GREEN}✓ Namespace ready${NC}"
echo ""

# Step 4: Deploy application
echo -e "${YELLOW}Step 4: Deploying application...${NC}"
kubectl apply -f "${K8S_DIR}/deployment-with-hpa.yaml" -n "${NAMESPACE}"
kubectl apply -f "${K8S_DIR}/service.yaml" -n "${NAMESPACE}"
echo -e "${GREEN}✓ Application deployed${NC}"
echo ""

# Step 5: Wait for deployment to be ready
echo -e "${YELLOW}Step 5: Waiting for deployment to be ready...${NC}"
kubectl wait --for=condition=available --timeout=300s deployment/paymetv-app-deployment -n "${NAMESPACE}"
echo -e "${GREEN}✓ Deployment ready${NC}"
echo ""

# Step 6: Deploy HPA
echo -e "${YELLOW}Step 6: Deploying HPA...${NC}"
kubectl apply -f "${K8S_DIR}/hpa.yaml" -n "${NAMESPACE}"
echo -e "${GREEN}✓ HPA deployed${NC}"
echo ""

# Step 7: Verify HPA
echo -e "${YELLOW}Step 7: Verifying HPA status...${NC}"
sleep 10  # Give HPA time to initialize
kubectl get hpa paymetv-app-hpa -n "${NAMESPACE}"
echo ""

# Step 8: Show deployment status
echo -e "${YELLOW}Step 8: Deployment Status${NC}"
echo ""
echo "Pods:"
kubectl get pods -n "${NAMESPACE}" -l app=paymetv-app
echo ""
echo "Service:"
kubectl get svc paymetv-app-service -n "${NAMESPACE}"
echo ""
echo "HPA:"
kubectl get hpa paymetv-app-hpa -n "${NAMESPACE}"
echo ""

# Get service URL
SERVICE_TYPE=$(kubectl get svc paymetv-app-service -n "${NAMESPACE}" -o jsonpath='{.spec.type}')
if [ "${SERVICE_TYPE}" = "LoadBalancer" ]; then
    echo "Waiting for LoadBalancer IP..."
    EXTERNAL_IP=""
    while [ -z "${EXTERNAL_IP}" ]; do
        EXTERNAL_IP=$(kubectl get svc paymetv-app-service -n "${NAMESPACE}" -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null)
        [ -z "${EXTERNAL_IP}" ] && sleep 5
    done
    echo -e "${GREEN}Application URL: http://${EXTERNAL_IP}${NC}"
elif [ "${SERVICE_TYPE}" = "NodePort" ]; then
    NODE_PORT=$(kubectl get svc paymetv-app-service -n "${NAMESPACE}" -o jsonpath='{.spec.ports[0].nodePort}')
    NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}')
    echo -e "${GREEN}Application URL: http://${NODE_IP}:${NODE_PORT}${NC}"
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}Deployment Complete!${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo "Next steps:"
echo "  1. Monitor HPA: ./scripts/monitor-hpa.sh ${NAMESPACE}"
echo "  2. Run load tests: ./scripts/run-gatling-tests.sh scale-up http://<service-url>"
echo ""

