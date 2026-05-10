#!/bin/bash

# Test health endpoints for PayMeTV application
# This script verifies that Spring Boot Actuator health endpoints are working

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}Testing PayMeTV Health Endpoints${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Default host and port
HOST="${1:-localhost}"
PORT="${2:-8090}"
BASE_URL="http://${HOST}:${PORT}"

echo -e "${YELLOW}Testing against: ${BASE_URL}${NC}"
echo ""

# Test 1: Main health endpoint
echo -e "${YELLOW}Test 1: Main health endpoint${NC}"
if curl -s "${BASE_URL}/actuator/health" > /dev/null; then
    echo -e "${GREEN}✓ /actuator/health is accessible${NC}"
    curl -s "${BASE_URL}/actuator/health" | jq '.'
else
    echo -e "${RED}✗ /actuator/health is NOT accessible${NC}"
fi
echo ""

# Test 2: Liveness probe
echo -e "${YELLOW}Test 2: Liveness probe${NC}"
if curl -f -s "${BASE_URL}/actuator/health/liveness" > /dev/null; then
    echo -e "${GREEN}✓ /actuator/health/liveness is accessible${NC}"
    curl -s "${BASE_URL}/actuator/health/liveness" | jq '.'
else
    echo -e "${RED}✗ /actuator/health/liveness is NOT accessible${NC}"
fi
echo ""

# Test 3: Readiness probe
echo -e "${YELLOW}Test 3: Readiness probe${NC}"
if curl -f -s "${BASE_URL}/actuator/health/readiness" > /dev/null; then
    echo -e "${GREEN}✓ /actuator/health/readiness is accessible${NC}"
    curl -s "${BASE_URL}/actuator/health/readiness" | jq '.'
else
    echo -e "${RED}✗ /actuator/health/readiness is NOT accessible${NC}"
fi
echo ""

# Test 4: Metrics endpoint
echo -e "${YELLOW}Test 4: Metrics endpoint${NC}"
if curl -f -s "${BASE_URL}/actuator/metrics" > /dev/null; then
    echo -e "${GREEN}✓ /actuator/metrics is accessible${NC}"
else
    echo -e "${RED}✗ /actuator/metrics is NOT accessible${NC}"
fi
echo ""

# Test 5: Prometheus endpoint
echo -e "${YELLOW}Test 5: Prometheus endpoint${NC}"
if curl -f -s "${BASE_URL}/actuator/prometheus" > /dev/null; then
    echo -e "${GREEN}✓ /actuator/prometheus is accessible${NC}"
else
    echo -e "${RED}✗ /actuator/prometheus is NOT accessible${NC}"
fi
echo ""

echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}Health Endpoint Testing Complete${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""
echo -e "${YELLOW}Usage:${NC}"
echo -e "  Test localhost:     ./test_health_endpoints.sh"
echo -e "  Test custom host:   ./test_health_endpoints.sh myhost.com 8080"
echo -e "  Test K8s pod:       ./test_health_endpoints.sh 10.244.0.20 80"

