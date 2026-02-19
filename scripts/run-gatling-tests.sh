#!/bin/bash

# Run Gatling load tests for HPA verification
# Usage: ./run-gatling-tests.sh [test-type] [base-url]

set -e

TEST_TYPE="${1:-scale-up}"
BASE_URL="${2:-http://localhost}"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Gatling HPA Load Test Runner${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Determine which simulation to run
case "${TEST_TYPE}" in
    scale-up)
        SIMULATION="com.paymetv.app.gatling.HPAScaleUpSimulation"
        echo -e "Test Type: ${GREEN}Scale Up${NC}"
        echo "This test will gradually increase load to trigger HPA scale-up"
        ;;
    scale-down)
        SIMULATION="com.paymetv.app.gatling.HPAScaleDownSimulation"
        echo -e "Test Type: ${GREEN}Scale Down${NC}"
        echo "This test will decrease load to trigger HPA scale-down"
        ;;
    sustained)
        SIMULATION="com.paymetv.app.gatling.HPASustainedLoadSimulation"
        echo -e "Test Type: ${GREEN}Sustained Load${NC}"
        echo "This test will maintain constant load to verify HPA stability"
        ;;
    spike)
        SIMULATION="com.paymetv.app.gatling.HPASpikeTestSimulation"
        echo -e "Test Type: ${GREEN}Spike Test${NC}"
        echo "This test will create sudden load spikes to test rapid scaling"
        ;;
    *)
        echo -e "${YELLOW}Unknown test type: ${TEST_TYPE}${NC}"
        echo "Available test types:"
        echo "  scale-up    - Test HPA scale-up behavior"
        echo "  scale-down  - Test HPA scale-down behavior"
        echo "  sustained   - Test HPA stability under sustained load"
        echo "  spike       - Test HPA response to sudden load spikes"
        exit 1
        ;;
esac

echo -e "Base URL: ${GREEN}${BASE_URL}${NC}"
echo ""

# Check if application is accessible
echo "Checking if application is accessible..."
if curl -s -f "${BASE_URL}/api/health" > /dev/null; then
    echo -e "${GREEN}✓ Application is accessible${NC}"
else
    echo -e "${YELLOW}⚠ Warning: Application may not be accessible at ${BASE_URL}${NC}"
    echo "Continuing anyway..."
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo "Starting Gatling test..."
echo -e "${BLUE}========================================${NC}"
echo ""

# Run Maven Gatling test
mvn gatling:test \
    -Dgatling.simulationClass="${SIMULATION}" \
    -DbaseUrl="${BASE_URL}" \
    -q

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}Test Complete!${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo "Gatling report generated in: target/gatling/"
echo ""
echo "To view the report:"
echo "  1. Find the latest report directory in target/gatling/"
echo "  2. Open index.html in a web browser"
echo ""
echo "Example:"
echo "  open target/gatling/\$(ls -t target/gatling/ | head -1)/index.html"

