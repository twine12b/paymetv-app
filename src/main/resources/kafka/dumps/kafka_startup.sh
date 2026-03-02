#!/bin/sh

set -e  # Exit on error

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}PayMeTV Kafka Setup Script${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Cleanup: Stop existing port-forward process - TODO move to common script keep (DRY)
echo -e "${YELLOW}Cleanup: Stopping existing port-forward process...${NC}"
if [ -f ".portforward.pid" ]; then
  PID=$(cat .portforward.pid 2>/dev/null || true)
  if [ -n "$PID" ] && ps -p $PID > /dev/null 2>&1; then
    kill $PID 2>/dev/null || true
    echo -e "${GREEN}✓ Stopped port-forward process (PID: $PID)${NC}"
  else
    echo -e "${YELLOW}  No running port-forward process found${NC}"
  fi
  rm -f .portforward.pid 2>/dev/null || true
else
  echo -e "${YELLOW}  No previous port-forward PID file found${NC}"
fi
echo ""

# create the kafka namespace if it doesn't exist
echo -e "${YELLOW}Creating the kafka namespace if it doesn't exist...${NC}"
kubectl get namespace kafka >/dev/null 2>&1 || kubectl create namespace kafka
echo -e "${GREEN}✓ Namespace created${NC}"
echo ""

# Run the Kafka setup
echo -e "${YELLOW}Step 1: Running Kafka setup...${NC}"
pushd zookeeper/conf2 > /dev/null
kubectl apply -f .
popd > /dev/null
echo -e "${GREEN}✓ Kafka setup applied${NC}"
echo ""

# Run the Kafka setup and wait for it to be ready
echo -e "${YELLOW}Waiting for Kafka to be ready...${NC}"
if ! kubectl -n kafka rollout status deployment/kafka-broker-1 --timeout=1200s; then
  echo "Timed out or deployment failed. Showing pod status and recent logs for debugging:"
  kubectl -n kafka get pods -l app=kafka -o wide
  kubectl -n kafka logs -l app=kafka --tail=200 || true
  exit 1
fi
echo -e "${GREEN}✓ Kafka is ready${NC}"
echo ""

## Port forward to the kafka broker 1 - this is only required for local development
#echo -e "${YELLOW}Step 2: Port-forwarding to the kafka broker 1...${NC}"
#kubectl port-forward -n kafka svc/kafka-broker-1 9092:9092 > /dev/null 2>&1 &
#PORT_FORWARD_PID=$!
#echo $PORT_FORWARD_PID > .portforward.pid
#echo -e "${GREEN}✓ Kafka port-forwarded${NC}"
#echo ""

# Final Summary
echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}✓ Kafka Setup Complete!${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""
echo "Deployment components:"
echo "  ✓ Namespace: kafka"
echo "  ✓ Deployment: kafka-broker-1"
echo "  ✓ Service: kafka-broker-1"
echo "  ✓ Port-forward: 9092:9092"
echo "  ✓ kafka-ui: http://localhost:8082"
echo ""
echo -e "${GREEN}Setup completed successfully!${NC}"
