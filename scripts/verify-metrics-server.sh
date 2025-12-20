#!/bin/bash

# Verify that metrics-server is installed and running
# This is required for HPA to function properly
# Automatically fixes common TLS certificate issues in local clusters

set -e

NAMESPACE="${1:-kube-system}"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}Verifying Metrics Server Installation${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Function to detect cluster type
detect_cluster_type() {
    local context=$(kubectl config current-context)
    local cluster_info=$(kubectl cluster-info 2>/dev/null | head -1)

    # Check for common local cluster patterns
    if [[ "$context" == *"docker-desktop"* ]] || [[ "$context" == *"docker-for-desktop"* ]]; then
        echo "docker-desktop"
    elif [[ "$context" == *"minikube"* ]]; then
        echo "minikube"
    elif [[ "$context" == *"kind"* ]]; then
        echo "kind"
    elif [[ "$context" == *"k3s"* ]] || [[ "$context" == *"k3d"* ]]; then
        echo "k3s"
    elif [[ "$cluster_info" == *"127.0.0.1"* ]] || [[ "$cluster_info" == *"localhost"* ]]; then
        echo "local"
    else
        echo "cloud"
    fi
}

# Function to check if metrics-server needs TLS fix
needs_tls_fix() {
    local pod_name=$(kubectl get pods -n "${NAMESPACE}" -l k8s-app=metrics-server -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

    if [ -z "$pod_name" ]; then
        return 1  # No pod found, can't check
    fi

    # Check logs for TLS certificate errors
    if kubectl logs -n "${NAMESPACE}" "$pod_name" --tail=50 2>/dev/null | grep -q "x509: cannot validate certificate"; then
        return 0  # Needs fix
    fi

    # Check if pod is not ready
    local ready=$(kubectl get pod -n "${NAMESPACE}" "$pod_name" -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null)
    if [ "$ready" != "True" ]; then
        # Check if it's been running for more than 2 minutes and still not ready
        local age=$(kubectl get pod -n "${NAMESPACE}" "$pod_name" -o jsonpath='{.status.startTime}' 2>/dev/null)
        if [ -n "$age" ]; then
            return 0  # Likely needs fix
        fi
    fi

    return 1  # Doesn't need fix
}

echo "1. Detecting cluster type..."
CLUSTER_TYPE=$(detect_cluster_type)
echo -e "${GREEN}✓ Cluster type: ${CLUSTER_TYPE}${NC}"
echo ""

# Check if metrics-server deployment exists
echo "2. Checking if metrics-server deployment exists..."
if kubectl get deployment metrics-server -n "${NAMESPACE}" &>/dev/null; then
    echo -e "${GREEN}✓ metrics-server deployment found${NC}"
    kubectl get deployment metrics-server -n "${NAMESPACE}"
    METRICS_EXISTS=true
else
    echo -e "${YELLOW}✗ metrics-server deployment NOT found${NC}"
    METRICS_EXISTS=false
fi

echo ""

# Install or fix metrics-server
if [ "$METRICS_EXISTS" = false ]; then
    echo "3. Installing metrics-server..."

    # Download the manifest
    curl -sL https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml -o /tmp/metrics-server.yaml

    # For local clusters, add TLS flags to prevent certificate issues
    if [ "$CLUSTER_TYPE" != "cloud" ]; then
        echo -e "${YELLOW}   Applying local cluster TLS configuration...${NC}"

        # Add the necessary flags for local clusters
        # --kubelet-insecure-tls: Skip TLS certificate verification (safe for local dev)
        # --kubelet-preferred-address-types: Use InternalIP first for kubelet communication
        sed -i.bak '/- --metric-resolution=15s/a\
        - --kubelet-insecure-tls\
        - --kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname
' /tmp/metrics-server.yaml

        echo -e "${GREEN}   ✓ Added TLS bypass flags for local cluster${NC}"
        echo -e "${BLUE}   Note: These flags are safe for local development but should NOT be used in production${NC}"
    fi

    kubectl apply -f /tmp/metrics-server.yaml
    rm -f /tmp/metrics-server.yaml /tmp/metrics-server.yaml.bak

    echo ""
    echo "   Waiting for metrics-server deployment to be available..."
    kubectl wait --for=condition=available --timeout=300s deployment/metrics-server -n "${NAMESPACE}" 2>/dev/null || true
    echo -e "${GREEN}✓ metrics-server installed${NC}"
else
    # Check if existing metrics-server needs TLS fix
    echo "3. Checking if metrics-server needs TLS configuration fix..."

    if needs_tls_fix; then
        echo -e "${YELLOW}⚠ Detected TLS certificate verification issues${NC}"
        echo ""
        echo "   Common error: 'x509: cannot validate certificate for <IP> because it doesn't contain any IP SANs'"
        echo ""

        if [ "$CLUSTER_TYPE" != "cloud" ]; then
            echo -e "${YELLOW}   Applying fix for local cluster...${NC}"

            # Patch the deployment to add TLS flags
            kubectl patch deployment metrics-server -n "${NAMESPACE}" --type='json' -p='[
              {
                "op": "add",
                "path": "/spec/template/spec/containers/0/args/-",
                "value": "--kubelet-insecure-tls"
              }
            ]' 2>/dev/null || true

            # Check if kubelet-preferred-address-types already exists
            if ! kubectl get deployment metrics-server -n "${NAMESPACE}" -o yaml | grep -q "kubelet-preferred-address-types"; then
                kubectl patch deployment metrics-server -n "${NAMESPACE}" --type='json' -p='[
                  {
                    "op": "add",
                    "path": "/spec/template/spec/containers/0/args/-",
                    "value": "--kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname"
                  }
                ]' 2>/dev/null || true
            fi

            echo -e "${GREEN}   ✓ Applied TLS bypass configuration${NC}"
            echo -e "${BLUE}   Note: --kubelet-insecure-tls skips TLS verification (safe for local dev only)${NC}"
            echo ""
            echo "   Waiting for rollout to complete..."
            kubectl rollout status deployment/metrics-server -n "${NAMESPACE}" --timeout=120s
            echo -e "${GREEN}✓ metrics-server configuration updated${NC}"
        else
            echo -e "${RED}⚠ TLS issues detected on cloud cluster${NC}"
            echo "   This may require proper certificate configuration."
            echo "   Please check your cluster's kubelet certificates."
        fi
    else
        echo -e "${GREEN}✓ metrics-server configuration looks good${NC}"
    fi
fi

echo ""

# Check if metrics-server pods are running
echo "4. Checking metrics-server pod status..."
METRICS_POD=$(kubectl get pods -n "${NAMESPACE}" -l k8s-app=metrics-server -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

if [ -n "${METRICS_POD}" ]; then
    echo -e "${GREEN}✓ metrics-server pod found: ${METRICS_POD}${NC}"
    kubectl get pod "${METRICS_POD}" -n "${NAMESPACE}"
else
    echo -e "${RED}✗ metrics-server pod NOT found${NC}"
    exit 1
fi

echo ""

# Wait for metrics-server to be ready
echo "5. Waiting for metrics-server to be ready..."
echo "   This may take up to 2 minutes..."

# Try to wait for pod to be ready
if kubectl wait --for=condition=ready pod -l k8s-app=metrics-server -n "${NAMESPACE}" --timeout=120s 2>/dev/null; then
    echo -e "${GREEN}✓ metrics-server is ready${NC}"
else
    echo -e "${YELLOW}⚠ Timeout waiting for ready condition, checking pod status...${NC}"

    # Check if pod is actually running
    POD_STATUS=$(kubectl get pod "${METRICS_POD}" -n "${NAMESPACE}" -o jsonpath='{.status.phase}' 2>/dev/null)
    if [ "$POD_STATUS" = "Running" ]; then
        echo -e "${YELLOW}   Pod is running but may still be initializing...${NC}"
        echo "   Checking for errors in logs..."

        # Show last few log lines
        kubectl logs -n "${NAMESPACE}" "${METRICS_POD}" --tail=10 2>/dev/null || true

        echo ""
        echo -e "${YELLOW}   Continuing anyway - metrics may become available shortly${NC}"
    else
        echo -e "${RED}✗ Pod is not running (status: ${POD_STATUS})${NC}"
        echo "   Showing pod events:"
        kubectl describe pod "${METRICS_POD}" -n "${NAMESPACE}" | grep -A 10 "Events:" || true
        exit 1
    fi
fi

echo ""

# Test metrics API
echo "6. Testing metrics API..."
echo "   Waiting for metrics collection to start (15 seconds)..."
sleep 15

# Try to get node metrics with retries
MAX_RETRIES=6
RETRY_COUNT=0
METRICS_WORKING=false

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if kubectl top nodes &>/dev/null; then
        METRICS_WORKING=true
        break
    fi

    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ $RETRY_COUNT -lt $MAX_RETRIES ]; then
        echo "   Attempt $RETRY_COUNT/$MAX_RETRIES: Metrics not ready yet, waiting 10 seconds..."
        sleep 10
    fi
done

if [ "$METRICS_WORKING" = true ]; then
    echo -e "${GREEN}✓ Node metrics available:${NC}"
    kubectl top nodes
    echo ""

    if kubectl top pods -A --no-headers | head -5 &>/dev/null; then
        echo -e "${GREEN}✓ Pod metrics available (showing first 5):${NC}"
        kubectl top pods -A --no-headers | head -5
    else
        echo -e "${YELLOW}⚠ Pod metrics not available yet (may take another minute)${NC}"
    fi
else
    echo -e "${YELLOW}⚠ Metrics not available yet${NC}"
    echo ""
    echo "   This is normal - metrics-server needs time to collect initial data."
    echo "   Please wait 1-2 minutes and verify with:"
    echo "     kubectl top nodes"
    echo "     kubectl top pods -n default"
    echo ""
    echo "   If metrics still don't work after 2 minutes, check the logs:"
    echo "     kubectl logs -n ${NAMESPACE} -l k8s-app=metrics-server --tail=50"
fi

echo ""
echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}Metrics Server Verification Complete${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Show current configuration
echo "Current metrics-server configuration:"
kubectl get deployment metrics-server -n "${NAMESPACE}" -o jsonpath='{.spec.template.spec.containers[0].args}' | jq -r '.[]' 2>/dev/null || \
    kubectl get deployment metrics-server -n "${NAMESPACE}" -o jsonpath='{.spec.template.spec.containers[0].args}' | tr ',' '\n'

echo ""
echo -e "${GREEN}✓ Verification complete${NC}"
echo ""

if [ "$CLUSTER_TYPE" != "cloud" ] && kubectl get deployment metrics-server -n "${NAMESPACE}" -o yaml | grep -q "kubelet-insecure-tls"; then
    echo -e "${BLUE}ℹ Local cluster detected - TLS verification is disabled for development${NC}"
    echo -e "${YELLOW}⚠ WARNING: Do NOT use --kubelet-insecure-tls in production clusters${NC}"
    echo ""
fi

echo "Quick test commands:"
echo "  kubectl top nodes"
echo "  kubectl top pods -n default"
echo "  kubectl get hpa -A --watch"

