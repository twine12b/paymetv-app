#!/bin/sh

set -e  # Exit on error

NAMESPACE="database"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}PayMeTV Kubernetes Setup Script${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# teardown the database namespace and all its resources - TODO delete docker volume and kill port-forward
echo -e "${YELLOW}Step 0: tearing down the database namespace and all its resources...${NC}"
kubectl delete namespace database --ignore-not-found=true
kubectl delete pods --all -n database --ignore-not-found=true
kubectl delete deployments --all -n database --ignore-not-found=true
kubectl delete services --all -n database --ignore-not-found=true
kubectl delete secrets --all -n database --ignore-not-found=true
kubectl delete configmaps --all -n database --ignore-not-found=true
kubectl delete pvc --all -n database --ignore-not-found=true
kubectl delete pv --all -n database --ignore-not-found=true
kubectl delete ingress --all -n database --ignore-not-found=true
echo -e "${GREEN}✓ database namespace and all its resources deleted${NC}"
echo ""

# Build the Docker image - this is only here because I need to amend the push and build script
echo -e "${YELLOW}Step 1: building the mysql docker image...${NC}"
docker build -t paymetv/paymetv_db:latest .
echo -e "${GREEN}✓ mysql docker image built${NC}"
echo ""

# Create the database namespace if it doesn't exist
echo -e "${YELLOW}Step 2: Creating namespace if it doesn't exist...${NC}"
kubectl get namespace database >/dev/null 2>&1 || kubectl create namespace database
echo -e "${GREEN}✓ Namespace created${NC}"
echo ""

# Set the namespace for the current context
echo -e "${YELLOW}Setting namespace for current context...${NC}"
kubectl config set-context --current --namespace=database
echo -e "${GREEN}✓ Namespace set${NC}"
echo ""

# Create the secrets - TODO add this to a configMap
echo -e "${YELLOW}Step 3: Creating secret for mysql root password...${NC}"
kubectl create secret generic mysql-root-pass --from-literal=password=Ch1ldren -n database
kubectl create secret generic mysql-user-pass --from-literal=username=todd --from-literal=password=password -n database
kubectl create secret generic mysql-db-url --from-literal=database=paymetv_db1 -n database
echo -e "${GREEN}✓ Secrets created${NC}"
echo ""

# Deploy the database
echo -e "${YELLOW}Step 4: Deploying the database...${NC}"
kubectl apply -f conf/mysql.yaml
echo "Waiting for mysql deployment to become ready in namespace '${NAMESPACE}'..."
if ! kubectl -n "${NAMESPACE}" rollout status deployment/mysql-deployment --timeout=300s; then
  echo "Timed out or deployment failed. Showing pod status and recent logs for debugging:"
  kubectl -n "${NAMESPACE}" get pods -l app=mysql -o wide
  kubectl -n "${NAMESPACE}" logs -l app=mysql --tail=200 || true
  exit 1
fi
echo -e "${GREEN}✓ Database deployed${NC}"
echo ""

# Port forward to the database - this is only required for local development
echo -e "${YELLOW}Step 5: Port-forwarding to the database...${NC}"
kubectl port-forward -n database svc/mysql 3306:3306 > /dev/null 2>&1 &
PORT_FORWARD_PID=$!
echo $PORT_FORWARD_PID > .portforward.pid
echo -e "${GREEN}✓ Database port-forwarded${NC}"
echo ""

# Final Summary
echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}✓ MySQL Database Setup Complete!${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""
echo "Deployment components:"
echo "  ✓ Namespace: database"
echo "  ✓ Secrets: mysql-root-pass, mysql-user-pass, mysql-db-url"
echo "  ✓ Deployment: mysql-deployment"
echo "  ✓ Service: mysql"
echo "  ✓ Port-forward: 3306:3306"
echo ""
echo -e "${GREEN}Setup completed successfully!${NC}"

## Test the database connection
#echo -e "${YELLOW}Info: Testing the database connection...${NC}"
#./conf/test-mysql-connection.sh
#echo -e "${GREEN}✓ Database connection test passed${NC}"
#echo ""


