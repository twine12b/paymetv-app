#!/bin/bash

set -e

NAMESPACE="${1:default 2:-streaming 3:-database 4:-monitoring}"
K8S_DIR="src/main/resources/k8s"
app_dir="../src/main/resources/conf"
streaming_dir="../src/main/resources/streaming"
database_dir="../src/main/resources/database"
monitoring_dir="../src/main/resources/prometheus"
ENVIRONMENT="local"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}✓ PayMeTV Starting All Deployments${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Step1: Starting Paymetv app
echo -e "${YELLOW}Step 1: Starting Paymetv app...${NC}"
echo -e "${YELLOW}Setting namespace to ${NAMESPACE-default}...${NC}"
kubectl config set-context --current --namespace=${NAMESPACE-default}
echo -e "${GREEN}✓ Namespace set...${NC}"

echo -e "${YELLOW}Deploying Paymetv app...${NC}"
kubectl config set-context --current --namespace=${NAMESPACE-default}
pushd $app_dir > /dev/null
./setup_cert_manager.sh ${ENVIRONMENT}
popd > /dev/null
echo -e "${GREEN}✓ Paymetv app started${NC}"
echo ""

# Step2: Starting Streaming app
echo -e "${YELLOW}Step 2: Starting Streaming app...${NC}"
kubectl config set-context --current --namespace=${NAMESPACE-streaming}
pushd $streaming_dir > /dev/null
./lighttpd_startup.sh
popd > /dev/null
echo -e "${GREEN}✓ Streaming app started${NC}"
echo ""

# Step3: Starting Database app
kubectl config set-context --current --namespace=${NAMESPACE-database}
echo -e "${YELLOW}Step 3: Starting Database app...${NC}"
pushd $database_dir > /dev/null
./mysql_startup.sh
popd > /dev/null
echo -e "${GREEN}✓ Database app started${NC}"
echo ""

# Step4: Starting Monitoring app
kubectl config set-context --current --namespace=${NAMESPACE-monitoring}
echo -e "${YELLOW}Step 4: Starting Monitoring app...${NC}"
pushd $monitoring_dir > /dev/null
./prometheus_startup.sh
sleep 10
# TODO: fix why prometheus is not starting first time
./prometheus_startup.sh
popd > /dev/null
echo -e "${GREEN}✓ Monitoring app started${NC}"
echo ""

# Final Summary
echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}✓ All Deployments Started!${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""
echo "Deployment components:"
echo "  ✓ PayMeTV Application - http://localhost:8080"
echo "  ✓ Streaming Application - http://localhost:3000"
echo "  ✓ Database Application - http://localhost:3306"
echo "  ✓ Monitoring Application Grafana - http://localhost:3010"
echo "  ✓ Monitoring Application Prometheus - http://localhost:9090"
echo "  ✓ Monitoring Application Alertmanager - http://localhost:9093"
echo ""
echo -e "$RED Note: It may take a few minutes for all applications to be ready.${NC}"
echo ""
echo -e "${GREEN}Setup completed successfully!${NC}"