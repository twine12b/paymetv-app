#!/bin/bash

# Ingress NGINX Troubleshooting Script
# This script helps diagnose common ingress-nginx issues, especially admission webhook problems

namespace=default

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}Ingress NGINX Troubleshooting${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Check 1: Admission webhook job status
echo -e "${YELLOW}1. Checking admission webhook job status...${NC}"
if kubectl -n ${namespace} get job ingress-nginx-admission-create &>/dev/null; then
    JOB_STATUS=$(kubectl -n ${namespace} get job ingress-nginx-admission-create -o jsonpath='{.status.conditions[?(@.type=="Complete")].status}' 2>/dev/null)
    if [ "$JOB_STATUS" == "True" ]; then
        echo -e "${GREEN}✓ Admission webhook job completed successfully${NC}"
    else
        echo -e "${RED}✗ Admission webhook job has not completed${NC}"
        echo "Job details:"
        kubectl -n ${namespace} describe job ingress-nginx-admission-create
        echo ""
        echo "Job pod logs:"
        kubectl -n ${namespace} logs -l job-name=ingress-nginx-admission-create --tail=50
    fi
else
    echo -e "${RED}✗ Admission webhook job not found${NC}"
    echo "Available jobs:"
    kubectl -n ${namespace} get jobs
fi
echo ""

# Check 2: Admission webhook secret
echo -e "${YELLOW}2. Checking admission webhook secret...${NC}"
if kubectl -n ${namespace} get secret ingress-nginx-admission &>/dev/null; then
    echo -e "${GREEN}✓ Admission webhook secret exists${NC}"
    echo "Secret details:"
    kubectl -n ${namespace} get secret ingress-nginx-admission -o yaml | grep -A 5 "^data:"
else
    echo -e "${RED}✗ Admission webhook secret not found${NC}"
    echo "Available secrets:"
    kubectl -n ${namespace} get secrets
fi
echo ""

# Check 3: Controller deployment status
echo -e "${YELLOW}3. Checking ingress-nginx controller deployment...${NC}"
if kubectl -n ${namespace} get deployment ingress-nginx-controller &>/dev/null; then
    READY=$(kubectl -n ${namespace} get deployment ingress-nginx-controller -o jsonpath='{.status.readyReplicas}' 2>/dev/null)
    DESIRED=$(kubectl -n ${namespace} get deployment ingress-nginx-controller -o jsonpath='{.spec.replicas}' 2>/dev/null)
    
    if [ "$READY" == "$DESIRED" ] && [ ! -z "$READY" ]; then
        echo -e "${GREEN}✓ Controller deployment is ready ($READY/$DESIRED replicas)${NC}"
    else
        echo -e "${RED}✗ Controller deployment is not ready ($READY/$DESIRED replicas)${NC}"
        echo "Deployment details:"
        kubectl -n ${namespace} describe deployment ingress-nginx-controller
    fi
else
    echo -e "${RED}✗ Controller deployment not found${NC}"
fi
echo ""

# Check 4: Controller pod status
echo -e "${YELLOW}4. Checking controller pod status...${NC}"
CONTROLLER_POD=$(kubectl -n ${namespace} get pods -l app.kubernetes.io/component=controller -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
if [ ! -z "$CONTROLLER_POD" ]; then
    POD_STATUS=$(kubectl -n ${namespace} get pod $CONTROLLER_POD -o jsonpath='{.status.phase}' 2>/dev/null)
    POD_READY=$(kubectl -n ${namespace} get pod $CONTROLLER_POD -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null)
    
    if [ "$POD_STATUS" == "Running" ] && [ "$POD_READY" == "True" ]; then
        echo -e "${GREEN}✓ Controller pod is running and ready${NC}"
        echo "Pod: $CONTROLLER_POD"
    else
        echo -e "${RED}✗ Controller pod is not ready (Status: $POD_STATUS, Ready: $POD_READY)${NC}"
        echo "Pod: $CONTROLLER_POD"
        echo ""
        echo "Pod description:"
        kubectl -n ${namespace} describe pod $CONTROLLER_POD
        echo ""
        echo "Pod events:"
        kubectl -n ${namespace} get events --field-selector involvedObject.name=$CONTROLLER_POD --sort-by='.lastTimestamp'
        echo ""
        echo "Pod logs (last 50 lines):"
        kubectl -n ${namespace} logs $CONTROLLER_POD --tail=50
    fi
else
    echo -e "${RED}✗ Controller pod not found${NC}"
    echo "Available pods:"
    kubectl -n ${namespace} get pods
fi
echo ""

# Check 5: Admission webhook service
echo -e "${YELLOW}5. Checking admission webhook service...${NC}"
if kubectl -n ${namespace} get svc ingress-nginx-controller-admission &>/dev/null; then
    echo -e "${GREEN}✓ Admission webhook service exists${NC}"
    kubectl -n ${namespace} get svc ingress-nginx-controller-admission
else
    echo -e "${RED}✗ Admission webhook service not found${NC}"
fi
echo ""

# Check 6: ValidatingWebhookConfiguration
echo -e "${YELLOW}6. Checking ValidatingWebhookConfiguration...${NC}"
if kubectl get validatingwebhookconfiguration ingress-nginx-admission &>/dev/null; then
    echo -e "${GREEN}✓ ValidatingWebhookConfiguration exists${NC}"
    kubectl get validatingwebhookconfiguration ingress-nginx-admission -o yaml | grep -A 10 "webhooks:"
else
    echo -e "${RED}✗ ValidatingWebhookConfiguration not found${NC}"
fi
echo ""

# Summary and recommendations
echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}Summary and Recommendations${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Determine the main issue
if ! kubectl -n ${namespace} get secret ingress-nginx-admission &>/dev/null; then
    echo -e "${RED}Main Issue: Admission webhook secret is missing${NC}"
    echo ""
    echo "Recommended actions:"
    echo "1. Check if the admission webhook job completed:"
    echo "   kubectl -n ${namespace} get jobs"
    echo ""
    echo "2. Check job logs for errors:"
    echo "   kubectl -n ${namespace} logs -l job-name=ingress-nginx-admission-create"
    echo ""
    echo "3. Delete and recreate the job:"
    echo "   kubectl -n ${namespace} delete job ingress-nginx-admission-create"
    echo "   kubectl -n ${namespace} apply -f deploy.yaml"
    echo ""
elif [ ! -z "$CONTROLLER_POD" ]; then
    POD_STATUS=$(kubectl -n ${namespace} get pod $CONTROLLER_POD -o jsonpath='{.status.phase}' 2>/dev/null)
    if [ "$POD_STATUS" != "Running" ]; then
        echo -e "${RED}Main Issue: Controller pod is not running${NC}"
        echo ""
        echo "Recommended actions:"
        echo "1. Check pod events:"
        echo "   kubectl -n ${namespace} describe pod $CONTROLLER_POD"
        echo ""
        echo "2. Check pod logs:"
        echo "   kubectl -n ${namespace} logs $CONTROLLER_POD"
        echo ""
        echo "3. Restart the deployment:"
        echo "   kubectl -n ${namespace} rollout restart deployment ingress-nginx-controller"
    fi
else
    echo -e "${GREEN}All checks passed!${NC}"
    echo "Ingress NGINX appears to be running correctly."
fi
echo ""

