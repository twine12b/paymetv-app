#!/bin/bash

# Monitor HPA status during load tests
# Usage: ./monitor-hpa.sh [namespace] [interval_seconds]

NAMESPACE="${1:-default}"
INTERVAL="${2:-5}"
HPA_NAME="paymetv-app-hpa"
DEPLOYMENT_NAME="paymetv-app-deployment"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}HPA Monitoring for PayMeTV Application${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "Namespace: ${GREEN}${NAMESPACE}${NC}"
echo -e "HPA Name: ${GREEN}${HPA_NAME}${NC}"
echo -e "Deployment: ${GREEN}${DEPLOYMENT_NAME}${NC}"
echo -e "Refresh Interval: ${GREEN}${INTERVAL}s${NC}"
echo -e "${BLUE}========================================${NC}\n"

# Create log file with timestamp
LOG_FILE="hpa-monitor-$(date +%Y%m%d-%H%M%S).log"
echo "Logging to: ${LOG_FILE}"
echo ""

# Header for log file
echo "Timestamp,Replicas,Desired,CPU_Current,CPU_Target,Min,Max" > "${LOG_FILE}"

# Function to get HPA metrics
get_hpa_metrics() {
    kubectl get hpa "${HPA_NAME}" -n "${NAMESPACE}" -o json 2>/dev/null
}

# Function to get pod metrics
get_pod_metrics() {
    # Try to find pods with the correct label
    local pods=$(kubectl top pods -n "${NAMESPACE}" -l app=paymetv-app --no-headers 2>/dev/null)

    # If no pods found with paymetv-app label, try pmtv-app label
    if [ -z "$pods" ]; then
        pods=$(kubectl top pods -n "${NAMESPACE}" -l app=pmtv-app --no-headers 2>/dev/null)
    fi

    # If still no pods, try getting all pods from the deployment
    if [ -z "$pods" ]; then
        pods=$(kubectl top pods -n "${NAMESPACE}" --selector="app in (paymetv-app,pmtv-app)" --no-headers 2>/dev/null)
    fi

    echo "$pods"
}

# Function to detect the correct app label
detect_app_label() {
    # Check if pods exist with paymetv-app label
    if kubectl get pods -n "${NAMESPACE}" -l app=paymetv-app --no-headers 2>/dev/null | grep -q .; then
        echo "paymetv-app"
        return 0
    fi

    # Check if pods exist with pmtv-app label
    if kubectl get pods -n "${NAMESPACE}" -l app=pmtv-app --no-headers 2>/dev/null | grep -q .; then
        echo "pmtv-app"
        return 0
    fi

    # No pods found
    return 1
}

# Check if HPA exists
if ! kubectl get hpa "${HPA_NAME}" -n "${NAMESPACE}" &>/dev/null; then
    echo -e "${RED}ERROR: HPA '${HPA_NAME}' not found in namespace '${NAMESPACE}'${NC}"
    echo "Available HPAs:"
    kubectl get hpa -n "${NAMESPACE}"
    exit 1
fi

# Detect the app label being used
APP_LABEL=$(detect_app_label)
if [ $? -ne 0 ]; then
    echo -e "${RED}ERROR: No pods found with labels 'app=paymetv-app' or 'app=pmtv-app' in namespace '${NAMESPACE}'${NC}"
    echo ""
    echo "Available pods in namespace '${NAMESPACE}':"
    kubectl get pods -n "${NAMESPACE}"
    echo ""
    echo "Available deployments:"
    kubectl get deployments -n "${NAMESPACE}"
    echo ""
    echo -e "${YELLOW}TIP: Run './scripts/diagnose-pods.sh ${NAMESPACE}' for detailed diagnostics${NC}"
    exit 1
fi

echo -e "Detected app label: ${GREEN}app=${APP_LABEL}${NC}"
echo ""

# Monitoring loop
while true; do
    TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')
    
    # Get HPA status
    HPA_JSON=$(get_hpa_metrics)
    
    if [ -n "${HPA_JSON}" ]; then
        CURRENT_REPLICAS=$(echo "${HPA_JSON}" | jq -r '.status.currentReplicas // 0')
        DESIRED_REPLICAS=$(echo "${HPA_JSON}" | jq -r '.status.desiredReplicas // 0')
        MIN_REPLICAS=$(echo "${HPA_JSON}" | jq -r '.spec.minReplicas')
        MAX_REPLICAS=$(echo "${HPA_JSON}" | jq -r '.spec.maxReplicas')
        
        # Get CPU metrics
        CPU_CURRENT=$(echo "${HPA_JSON}" | jq -r '.status.currentMetrics[0].resource.current.averageUtilization // "N/A"')
        CPU_TARGET=$(echo "${HPA_JSON}" | jq -r '.spec.metrics[0].resource.target.averageUtilization')
        
        # Display current status
        clear
        echo -e "${BLUE}========================================${NC}"
        echo -e "${BLUE}HPA Status - ${TIMESTAMP}${NC}"
        echo -e "${BLUE}========================================${NC}"
        echo ""
        
        echo -e "${YELLOW}Replica Status:${NC}"
        echo -e "  Current Replicas: ${GREEN}${CURRENT_REPLICAS}${NC}"
        echo -e "  Desired Replicas: ${GREEN}${DESIRED_REPLICAS}${NC}"
        echo -e "  Min Replicas:     ${GREEN}${MIN_REPLICAS}${NC}"
        echo -e "  Max Replicas:     ${GREEN}${MAX_REPLICAS}${NC}"
        echo ""
        
        echo -e "${YELLOW}CPU Metrics:${NC}"
        echo -e "  Current CPU:      ${GREEN}${CPU_CURRENT}%${NC}"
        echo -e "  Target CPU:       ${GREEN}${CPU_TARGET}%${NC}"
        echo ""
        
        # Show scaling status
        if [ "${CURRENT_REPLICAS}" -lt "${DESIRED_REPLICAS}" ]; then
            echo -e "${YELLOW}⬆ SCALING UP: ${CURRENT_REPLICAS} → ${DESIRED_REPLICAS}${NC}"
        elif [ "${CURRENT_REPLICAS}" -gt "${DESIRED_REPLICAS}" ]; then
            echo -e "${YELLOW}⬇ SCALING DOWN: ${CURRENT_REPLICAS} → ${DESIRED_REPLICAS}${NC}"
        else
            echo -e "${GREEN}✓ STABLE: ${CURRENT_REPLICAS} replicas${NC}"
        fi
        echo ""
        
        # Show pod metrics
        echo -e "${YELLOW}Pod Resource Usage:${NC}"
        echo -e "${BLUE}NAME                                    CPU(cores)   MEMORY(bytes)${NC}"
        get_pod_metrics | while read -r line; do
            echo -e "  ${line}"
        done
        echo ""
        
        # Log to file
        echo "${TIMESTAMP},${CURRENT_REPLICAS},${DESIRED_REPLICAS},${CPU_CURRENT},${CPU_TARGET},${MIN_REPLICAS},${MAX_REPLICAS}" >> "${LOG_FILE}"
        
        echo -e "${BLUE}========================================${NC}"
        echo -e "Logging to: ${LOG_FILE}"
        echo -e "Press Ctrl+C to stop monitoring"
        echo -e "${BLUE}========================================${NC}"
    else
        echo -e "${RED}Failed to get HPA metrics${NC}"
    fi
    
    sleep "${INTERVAL}"
done

