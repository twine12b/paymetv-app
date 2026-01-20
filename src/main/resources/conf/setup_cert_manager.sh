#!/bin/bash

set -e  # Exit on error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd -P)"
root_dir="$(cd "$SCRIPT_DIR/../../../../" && pwd -P)"

echo "Script directory: $SCRIPT_DIR"

namespace=default

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}PayMeTV Kubernetes Setup Script${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Step 1: Build MySQL Docker Image
echo -e "${YELLOW}Step 1: Building MySQL Docker Image...${NC}"
# Use absolute path to dockerfile at project root for reliability
if [ -f "$root_dir/dockerfile_db" ]; then
    docker build -f "$root_dir/dockerfile_db" -t paymetv/mysql-db:latest "$root_dir"
    echo -e "${GREEN}✓ MySQL Docker image built successfully${NC}"
else
    echo -e "${YELLOW}⚠ dockerfile_db not found, skipping MySQL image build${NC}"
fi
echo ""

# Step 2: Deploy MySQL Database
echo -e "${YELLOW}Step 2: Deploying MySQL Database...${NC}"
kubectl -n ${namespace} apply -f mysql-deployment.yaml
echo -e "${GREEN}✓ MySQL deployment manifests applied${NC}"
echo ""

# Wait for MySQL pod to be ready
echo -e "${YELLOW}Step 3: Waiting for MySQL pod to be ready...${NC}"
echo "This may take 1-2 minutes for the first time..."
until kubectl -n ${namespace} get pods --no-headers | grep mysql-deployment | awk '{print $1}' | \
  xargs -I{} kubectl -n ${namespace} get pod {} -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null | grep -q True; do
  sleep 5
  echo "Still waiting for MySQL..."
done
echo -e "${GREEN}✓ MySQL pod is ready${NC}"
echo ""

# Verify MySQL service is available
echo -e "${YELLOW}Step 4: Verifying MySQL service...${NC}"
kubectl -n ${namespace} get svc mysql-service
echo -e "${GREEN}✓ MySQL service is available${NC}"
echo ""

# Step 5: Create ingress-nginx namespace and deploy
echo -e "${YELLOW}Step 5: Setting up Ingress NGINX...${NC}"
kubectl create namespace ingress-nginx --dry-run=client -o yaml | kubectl apply -f -
kubectl -n ${namespace} apply -f deploy.yaml
echo -e "${GREEN}✓ Ingress NGINX deployed${NC}"
echo ""

# Step 6: Wait for ingress-nginx controller pod to be ready
echo -e "${YELLOW}Step 6: Waiting for ingress-nginx controller pod to be ready...${NC}"
until kubectl -n ${namespace} get pods --no-headers | grep controller | awk '{print $1}' | \
  xargs -I{} kubectl -n ${namespace} get pod {} -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null | grep -q True; do
  sleep 5
  echo "Still waiting for ingress-nginx controller..."
done
echo -e "${GREEN}✓ Ingress-nginx controller pod is ready${NC}"
echo ""

# Step 7: Deploy the PayMeTV app (before cert-manager)
echo -e "${YELLOW}Step 7: Deploying PayMeTV application (initial)...${NC}"
kubectl apply -f dep-before.yaml
echo -e "${GREEN}✓ PayMeTV app deployment manifests applied${NC}"
echo ""

echo -e "${YELLOW}Step 8: Waiting for PayMeTV app pod to be ready...${NC}"
until kubectl -n ${namespace} get pods --no-headers | grep paymetv-app-deployment | awk '{print $1}' | \
    xargs -I{} kubectl -n ${namespace} get pod {} -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null | grep -q True; do
  sleep 5
  echo "Still waiting for PayMeTV app..."
done
echo -e "${GREEN}✓ PayMeTV app deployment pod is ready${NC}"
echo ""

# Step 9: Install cert-manager
echo -e "${YELLOW}Step 9: Installing cert-manager...${NC}"
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.16.3/cert-manager.yaml
echo -e "${GREEN}✓ Cert-manager manifests applied${NC}"
echo ""

echo -e "${YELLOW}Step 10: Waiting for cert-manager webhook pod to be ready...${NC}"
until kubectl -n cert-manager get pods --no-headers | grep cert-manager-webhook | awk '{print $1}' | \
    xargs -I{} kubectl -n cert-manager get pod {} -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null | grep -q True; do
  sleep 5
  echo "Still waiting for cert-manager webhook..."
done
echo -e "${GREEN}✓ Cert-manager webhook pod is ready${NC}"
echo ""

# Step 11: Install certificate issuers
echo -e "${YELLOW}Step 11: Installing certificate issuers...${NC}"
kubectl apply -f self-signed-issuer.yaml
kubectl apply -f acme-staging-issuer.yaml
kubectl apply -f acme-prod-issuer.yaml
echo -e "${GREEN}✓ Certificate issuers installed${NC}"
echo ""

# Step 12: Create certificates
echo -e "${YELLOW}Step 12: Creating certificates...${NC}"
kubectl apply -f self-signed-cert.yaml
kubectl apply -f acme-staging-cert.yaml
kubectl apply -f acme-prod-cert.yaml
echo -e "${GREEN}✓ Certificates created${NC}"
echo ""

# Step 13: Deploy the app with TLS certificates
echo -e "${YELLOW}Step 13: Deploying PayMeTV application (with TLS)...${NC}"
kubectl apply -f dep-after.yaml
echo -e "${GREEN}✓ PayMeTV app deployment updated with TLS${NC}"
echo ""

echo -e "${YELLOW}Step 14: Waiting for PayMeTV app pod to be ready (final)...${NC}"
until kubectl -n ${namespace} get pods --no-headers | grep paymetv-app-deployment | awk '{print $1}' | \
    xargs -I{} kubectl -n ${namespace} get pod {} -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null | grep -q True; do
  sleep 5
  echo "Still waiting for PayMeTV app (final)..."
done
echo -e "${GREEN}✓ PayMeTV app deployment pod is ready (final)${NC}"
echo ""

# Final summary
echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}✓ Setup Complete!${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""
echo "Deployed components:"
echo "  ✓ MySQL Database"
echo "  ✓ Ingress NGINX Controller"
echo "  ✓ PayMeTV Application"
echo "  ✓ Cert-Manager"
echo "  ✓ TLS Certificates"
echo ""
echo "Checking deployment status..."
echo ""
echo "MySQL:"
kubectl -n ${namespace} get pods -l app=mysql
echo ""
echo "PayMeTV App:"
kubectl -n ${namespace} get pods -l app=paymetv-app
echo ""
echo "Services:"
kubectl -n ${namespace} get svc
echo ""
echo -e "${GREEN}Setup completed successfully!${NC}"