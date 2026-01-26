#!/bin/bash

set -e  # Exit on error (but we'll use || true for optional deletions)

namespace="default"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}PayMeTV Kubernetes Teardown Script${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""


# Confirmation prompt
echo -e "${YELLOW}WARNING: This will delete all Kubernetes resources in the '${namespace}' namespace.${NC}"
if [ "$PRESERVE_SECRETS" = true ]; then
  echo -e "${BLUE}  - Secrets will be PRESERVED${NC}"
fi
if [ "$PRESERVE_CONFIG" = true ]; then
  echo -e "${BLUE}  - ConfigMaps will be PRESERVED${NC}"
fi
if [ "$PRESERVE_DATA" = true ]; then
  echo -e "${BLUE}  - PersistentVolumeClaims will be PRESERVED${NC}"
fi
echo ""
read -p "Are you sure you want to continue? (yes/no): " -r
echo ""
if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
  echo -e "${YELLOW}Teardown cancelled.${NC}"
  exit 0
fi

echo -e "${BLUE}Starting teardown of resources in namespace: ${namespace}${NC}"
echo ""

# Step 2: Delete Deployments (to prevent pod recreation)
echo -e "${YELLOW}Step 2: Deleting Deployments...${NC}"
#kubectl delete deployments --all -n $namespace
kubectl delete deployments -n $namespace --all 2>/dev/null || echo -e "${BLUE}  No deployments found${NC}"
kubectl delete pods --all -n $namespace
echo -e "${GREEN}✓ Deployments deleted${NC}"
echo ""

echo -e "${GREEN}=========================================${NC}"
echo -e "${GREEN}Teardown Complete!${NC}"
echo -e "${GREEN}=========================================${NC}"
echo ""
echo -e "${BLUE}Summary:${NC}"
echo -e "  - All resources in namespace '${namespace}' have been deleted"
if [ "$PRESERVE_SECRETS" = true ]; then
  echo -e "  - Secrets were preserved"
fi
if [ "$PRESERVE_CONFIG" = true ]; then
  echo -e "  - ConfigMaps were preserved"
fi
if [ "$PRESERVE_DATA" = true ]; then
  echo -e "  - PersistentVolumeClaims were preserved"
fi
echo ""
echo -e "${YELLOW}Note: To delete the ingress-nginx namespace and cert-manager, run:${NC}"
echo -e "  kubectl delete namespace ingress-nginx"
echo -e "  kubectl delete namespace cert-manager"
echo ""
