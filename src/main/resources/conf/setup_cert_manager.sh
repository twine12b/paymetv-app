#!/bin/bash

#wget https://raw.githubusercontent.com/gurlal-1/devops-avenue/refs/heads/main/yt-videos/kind-cert-manager/script/set_cluster.sh

set -e  # Exit on error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd -P)"
root_dir="$(cd "$SCRIPT_DIR/../../../../" && pwd -P)"
STREAMING_DIR="$root_dir/src/main/resources/streaming/conf"

echo "Script directory: $SCRIPT_DIR"

namespace=default

SECRET_NAME=$1
ISSUER_NAME=""


if [[ -z "$SECRET_NAME" ]]; then
  echo "======================================="
  echo "Error: secret_name argument is required"
  echo "======================================="
  echo "Usage: $0 <SECRET_NAME>"
  echo "Example: $0 dev"
  exit 1
fi

# Handle shortcuts or use literal secret name
if [ "$SECRET_NAME" == "prod" ]; then
  SECRET_NAME="pmtv-acme-http-prod-sec"
  ISSUER_NAME="pmtv-acme-http-prod"
  echo "Using production secret name: $SECRET_NAME" # not safe for production
elif [ "$SECRET_NAME" = "dev" ]; then
  SECRET_NAME="pmtv-acme-http-stage-sec"
  ISSUER_NAME="pmtv-acme-http-stage"
  echo "Using development/staging secret name: $SECRET_NAME"  # not safe for production
elif [ "$SECRET_NAME" = "local" ]; then
  SECRET_NAME="pmtv-ss-sec"
  ISSUER_NAME="pmtv-selfsigned-issuer"
  echo "Using self signed cert name: $SECRET_NAME"  # not safe for production
else
    echo "======================================================"
    echo "Error: secret_name argument must be either prod or dev"
    echo "======================================================"
    exit 1
fi

if [[ "$SECRET_NAME" == "prod" || "$SECRET_NAME" == "dev" || "$SECRET_NAME" == "local" ]]; then
  echo "======================================="
  echo "secret name not resolved to a valid secret name"
  echo "======================================="
  exit 1
fi

if [[  "$ISSUER_NAME" == "" ]]; then
  echo "======================================="
  echo "Error: issuer_name not resolved to a valid issuer name"
  echo "======================================="
  echo "Issuer name: $ISSUER_NAME"
  exit 1
fi

export SECRET_NAME
export ISSUER_NAME

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}PayMeTV Kubernetes Setup Script${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Create ingress-nginx namespace and deploy
echo -e "${YELLOW}Setting up Ingress NGINX...${NC}"
kubectl get namespace ingress-nginx >/dev/null 2>&1 || kubectl create namespace ingress-nginx
#envsubst < deploy.yaml | kubectl -n ${namespace} apply -f -
kubectl -n ${namespace} apply -f deploy.yaml
echo -e "${GREEN}✓ Ingress NGINX deployed${NC}"
echo ""

# Deploy lighttpd
kubectl get namespace ingress-nginx >/dev/null 2>&1 || kubectl create namespace ingress-nginx
# Deploy Lighttpd
echo -e "${YELLOW}Setting up Ingress NGINX...${NC}"
#kubectl create namespace streaming --dry-run=client -o yaml | kubectl apply -f -
kubectl get namespace streaming >/dev/null 2>&1 || kubectl create namespace streaming
kubectl -n streaming apply -f ${STREAMING_DIR}/deployment.yaml
echo -e "${GREEN}✓ deployment LIGHTTPD deployed${NC}"
kubectl -n streaming apply -f ${STREAMING_DIR}/service.yaml
echo -e "${GREEN}✓ Service LIGHTTPD deployed${NC}"
envsubst < ${STREAMING_DIR}/ingress.yaml | kubectl -n streaming apply -f -
echo -e "${GREEN}✓ Ingress Lighttpd deployed${NC}"
echo ""


# Wait for ingress-nginx pod with 'controller' in its name to be ready
echo -e "${YELLOW}Waiting for ingress-nginx controller pod to be ready...${NC}"
until kubectl -n ${namespace} get pods --no-headers | grep controller | awk '{print $1}' | \
  xargs -I{} kubectl -n ${namespace} get pod {} -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null | grep -q True; do
  sleep 5
  echo "Still waiting for ingress-nginx controller..."
done
echo -e "${GREEN}✓ Ingress-nginx controller pod is ready${NC}"
echo ""

#Deploy the app and wait until it is ready
envsubst < dep-before.yaml | kubectl apply -f -
echo -e  ${YELLOW}"Waiting for paymetv-app' pod to be ready...${NC}"
until kubectl -n ${namespace} get pods --no-headers | grep paymetv-app-deployment | awk '{print $1}' | \
    xargs -I{} kubectl -n ${namespace} get pod {} -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null | grep -q True; do
  sleep 5
  echo "Still waiting for PayMeTV app..."
done
echo -e "${GREEN}✓ PayMeTV app deployment pod is ready${NC}"
echo ""

#Install cert-manager
echo -e "${YELLOW}Step 9: Installing cert-manager...${NC}"
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.16.3/cert-manager.yaml
echo -e "${GREEN}✓ Cert-manager manifests applied${NC}"
echo ""

echo -e "${YELLOW}Waiting for cert-manager webhook pod to be ready...${NC}"
until kubectl -n cert-manager get pods --no-headers | grep cert-manager-webhook | awk '{print $1}' | \
    xargs -I{} kubectl -n cert-manager get pod {} -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null | grep -q True; do
  sleep 5
  echo "Still waiting for cert-manager webhook..."
done
echo -e "${GREEN}✓ Cert-manager webhook pod is ready${NC}"
echo ""

#install issuer
echo -e "${YELLOW}Installing $1 certificate issuers...${NC}"
kubectl apply -f self-signed-issuer.yaml
kubectl apply -f acme-staging-issuer.yaml
kubectl apply -f acme-prod-issuer.yaml
echo -e "${GREEN}✓ ($1) - Certificate issuers installed${NC}"
echo ""

#Create certificate
echo -e "${YELLOW}Step 12: Creating certificates...${NC}"
kubectl apply -f self-signed-cert.yaml
kubectl apply -f acme-staging-cert.yaml
kubectl apply -f acme-prod-cert.yaml
echo -e "${GREEN}✓ Certificates created${NC}"
echo ""

#Deploy the app with secret and wait until it is ready
echo -e "${YELLOW}Step 13: Deploying PayMeTV application (with TLS)...${NC}"
envsubst < dep-after.yaml | kubectl apply -f -
echo "Waiting for paymetv-app' pod to be ready..."
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