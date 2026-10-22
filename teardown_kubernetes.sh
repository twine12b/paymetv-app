#!/bin/bash

set -e  # Exit on error (but we'll use || true for optional deletions)

namespaces=("default" "streaming")
 teardown_namespaces=("database" "monitoring" "kafka" "kafka-monitoring-ui" "streaming" "workflow" "ml" "keda")

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
echo -e "${YELLOW}WARNING: This will delete all Kubernetes resources in the default and streaming namespace.${NC}"
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

for namespace in "${namespaces[@]}"; do
echo -e "${BLUE}Starting teardown of resources in namespace: ${namespace}${NC}"
echo ""

# Step 2: Delete Deployments (to prevent pod recreation)
kubectl delete deployments --all -n "$namespace" --ignore-not-found=true || true
kubectl delete pods --all -n "$namespace" --ignore-not-found=true || true
echo -e "${GREEN}✓ Deployments deleted${NC}"
echo ""
done

echo -e "${GREEN}=========================================${NC}"
echo -e "${GREEN}Teardown Complete!${NC}"
echo -e "${GREEN}=========================================${NC}"
echo ""
echo -e "${BLUE}Summary:${NC}"
echo -e "  - All resources in namespace default & streaming have been deleted"
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

# KEDA's ScaledObjects have a finalizer managed by the KEDA operator. Delete
# the HTTP add-on first, while the KEDA operator is still available to finalize
# its chart-created ScaledObject, then remove all application KEDA resources.
echo -e "${BLUE}Cleaning up KEDA resources and Helm releases...${NC}"
if command -v helm >/dev/null 2>&1; then
  helm uninstall http-add-on --namespace keda --ignore-not-found --wait --timeout 5m || true
fi

all_namespaces=()
while IFS= read -r namespace; do
  all_namespaces+=("$namespace")
done < <(kubectl get namespaces -o jsonpath='{range .items[*]}{.metadata.name}{"\n"}{end}' 2>/dev/null || true)
for namespace in "${all_namespaces[@]}"; do
  kubectl -n "$namespace" delete scaledobjects.keda.sh --all \
    --ignore-not-found=true --wait=true --timeout=30s >/dev/null 2>&1 || true
  kubectl -n "$namespace" delete interceptorroutes.http.keda.sh --all \
    --ignore-not-found=true --wait=true --timeout=30s >/dev/null 2>&1 || true
done

# If the KEDA operator is already gone, its ScaledObject finalizers cannot run.
# Remove only those orphaned finalizers so deletion can complete.
for namespace in "${all_namespaces[@]}"; do
  scaled_objects=$(kubectl -n "$namespace" get scaledobjects.keda.sh -o name 2>/dev/null || true)
  for scaled_object in $scaled_objects; do
    kubectl -n "$namespace" patch "$scaled_object" --type=merge \
      -p '{"metadata":{"finalizers":[]}}' >/dev/null 2>&1 || true
  done
done

if command -v helm >/dev/null 2>&1; then
  helm uninstall keda --namespace keda --ignore-not-found --wait --timeout 5m || true
fi
# Remove a stale aggregated API registration if Helm/operator deletion was
# incomplete; it otherwise makes namespace discovery fail during teardown.
kubectl delete apiservice v1beta1.external.metrics.k8s.io \
  --ignore-not-found=true --wait=false >/dev/null 2>&1 || true
echo -e "${GREEN}✓ KEDA resources cleaned up${NC}"
echo ""

# Step 3: Delete namespaces (optional, but will delete all resources within)
#kubectl delete namespace default
for namespace in "${teardown_namespaces[@]}"; do
  echo -e "${BLUE}Deleting namespace: ${namespace}${NC}"

  # Try graceful deletion first and wait for completion.
  kubectl delete namespace "$namespace" --ignore-not-found=true --wait=true --timeout=60s || true

  # KEDA custom resources are handled above. Do not strip namespace finalizers
  # while namespaced content still remains; that only hides the controller issue.
  if kubectl get namespace "$namespace" >/dev/null 2>&1; then
    echo -e "${YELLOW}  Namespace ${namespace} is still terminating; remaining resources:${NC}"
    kubectl get namespace "$namespace" -o jsonpath='{.status.conditions[?(@.type=="NamespaceContentRemaining")].message}{"\n"}' 2>/dev/null || true
  fi
done
echo -e "${GREEN}✓ Namespaces deleted${NC}"
echo ""

# Step 4: Clean up docker images (optional)
docker rmi paymetv/streaming-app:latest 2>/dev/null || echo -e "${BLUE}  No local Docker images to delete${NC}"
docker rmi paymetv/paymetv-app:latest 2>/dev/null || echo -e "${BLUE}  No local Docker images to delete${NC}"
docker rmi paymetv/mysql:latest 2>/dev/null || echo -e "${BLUE}  No local Docker images to delete${NC}"
docker rmi paymetv/kafka:latest 2>/dev/null || echo -e "${BLUE}  No local Docker images to delete${NC}"
echo -e "${GREEN}✓ Local Docker images cleaned up${NC}"
echo ""
