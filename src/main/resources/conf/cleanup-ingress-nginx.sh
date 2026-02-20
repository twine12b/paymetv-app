#!/bin/bash

# Ingress NGINX Cleanup and Reset Script
# Use this script to clean up a failed ingress-nginx deployment and start fresh

namespace=default

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}Ingress NGINX Cleanup Script${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

echo -e "${YELLOW}This script will remove all ingress-nginx components.${NC}"
echo -e "${YELLOW}Press Ctrl+C to cancel, or Enter to continue...${NC}"
read

echo ""
echo -e "${YELLOW}Step 1: Deleting ingress-nginx controller deployment...${NC}"
if kubectl -n ${namespace} get deployment ingress-nginx-controller &>/dev/null; then
    kubectl -n ${namespace} delete deployment ingress-nginx-controller
    echo -e "${GREEN}✓ Controller deployment deleted${NC}"
else
    echo -e "${YELLOW}⚠ Controller deployment not found${NC}"
fi
echo ""

echo -e "${YELLOW}Step 2: Deleting admission webhook jobs...${NC}"
if kubectl -n ${namespace} get job ingress-nginx-admission-create &>/dev/null; then
    kubectl -n ${namespace} delete job ingress-nginx-admission-create
    echo -e "${GREEN}✓ Admission create job deleted${NC}"
else
    echo -e "${YELLOW}⚠ Admission create job not found${NC}"
fi

if kubectl -n ${namespace} get job ingress-nginx-admission-patch &>/dev/null; then
    kubectl -n ${namespace} delete job ingress-nginx-admission-patch
    echo -e "${GREEN}✓ Admission patch job deleted${NC}"
else
    echo -e "${YELLOW}⚠ Admission patch job not found${NC}"
fi
echo ""

echo -e "${YELLOW}Step 3: Deleting admission webhook secret...${NC}"
if kubectl -n ${namespace} get secret ingress-nginx-admission &>/dev/null; then
    kubectl -n ${namespace} delete secret ingress-nginx-admission
    echo -e "${GREEN}✓ Admission webhook secret deleted${NC}"
else
    echo -e "${YELLOW}⚠ Admission webhook secret not found${NC}"
fi
echo ""

echo -e "${YELLOW}Step 4: Deleting services...${NC}"
if kubectl -n ${namespace} get svc ingress-nginx-controller &>/dev/null; then
    kubectl -n ${namespace} delete svc ingress-nginx-controller
    echo -e "${GREEN}✓ Controller service deleted${NC}"
else
    echo -e "${YELLOW}⚠ Controller service not found${NC}"
fi

if kubectl -n ${namespace} get svc ingress-nginx-controller-admission &>/dev/null; then
    kubectl -n ${namespace} delete svc ingress-nginx-controller-admission
    echo -e "${GREEN}✓ Admission webhook service deleted${NC}"
else
    echo -e "${YELLOW}⚠ Admission webhook service not found${NC}"
fi
echo ""

echo -e "${YELLOW}Step 5: Deleting ValidatingWebhookConfiguration...${NC}"
if kubectl get validatingwebhookconfiguration ingress-nginx-admission &>/dev/null; then
    kubectl delete validatingwebhookconfiguration ingress-nginx-admission
    echo -e "${GREEN}✓ ValidatingWebhookConfiguration deleted${NC}"
else
    echo -e "${YELLOW}⚠ ValidatingWebhookConfiguration not found${NC}"
fi
echo ""

echo -e "${YELLOW}Step 6: Deleting IngressClass...${NC}"
if kubectl get ingressclass nginx &>/dev/null; then
    kubectl delete ingressclass nginx
    echo -e "${GREEN}✓ IngressClass deleted${NC}"
else
    echo -e "${YELLOW}⚠ IngressClass not found${NC}"
fi
echo ""

echo -e "${YELLOW}Step 7: Deleting RBAC resources...${NC}"
kubectl -n ${namespace} delete serviceaccount ingress-nginx 2>/dev/null && echo -e "${GREEN}✓ ServiceAccount deleted${NC}"
kubectl -n ${namespace} delete serviceaccount ingress-nginx-admission 2>/dev/null && echo -e "${GREEN}✓ Admission ServiceAccount deleted${NC}"
kubectl -n ${namespace} delete role ingress-nginx 2>/dev/null && echo -e "${GREEN}✓ Role deleted${NC}"
kubectl -n ${namespace} delete role ingress-nginx-admission 2>/dev/null && echo -e "${GREEN}✓ Admission Role deleted${NC}"
kubectl -n ${namespace} delete rolebinding ingress-nginx 2>/dev/null && echo -e "${GREEN}✓ RoleBinding deleted${NC}"
kubectl -n ${namespace} delete rolebinding ingress-nginx-admission 2>/dev/null && echo -e "${GREEN}✓ Admission RoleBinding deleted${NC}"
kubectl delete clusterrole ingress-nginx 2>/dev/null && echo -e "${GREEN}✓ ClusterRole deleted${NC}"
kubectl delete clusterrole ingress-nginx-admission 2>/dev/null && echo -e "${GREEN}✓ Admission ClusterRole deleted${NC}"
kubectl delete clusterrolebinding ingress-nginx 2>/dev/null && echo -e "${GREEN}✓ ClusterRoleBinding deleted${NC}"
kubectl delete clusterrolebinding ingress-nginx-admission 2>/dev/null && echo -e "${GREEN}✓ Admission ClusterRoleBinding deleted${NC}"
echo ""

echo -e "${YELLOW}Step 8: Waiting for resources to be fully deleted...${NC}"
sleep 5
echo -e "${GREEN}✓ Cleanup complete${NC}"
echo ""

echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}Cleanup Complete!${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""
echo "Remaining ingress-nginx resources (should be empty):"
echo ""
echo "Deployments:"
kubectl -n ${namespace} get deployments -l app.kubernetes.io/name=ingress-nginx
echo ""
echo "Jobs:"
kubectl -n ${namespace} get jobs -l app.kubernetes.io/name=ingress-nginx
echo ""
echo "Secrets:"
kubectl -n ${namespace} get secrets | grep ingress-nginx
echo ""
echo "Services:"
kubectl -n ${namespace} get svc -l app.kubernetes.io/name=ingress-nginx
echo ""
echo -e "${GREEN}You can now re-run the setup script to deploy ingress-nginx fresh.${NC}"
echo ""
