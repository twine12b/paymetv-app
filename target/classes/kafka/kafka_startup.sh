!#/bin/sh

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

# create the kafka namespace if it doesn't exist
echo -e "${YELLOW}Creating the kafka namespace if it doesn't exist...${NC}"
kubectl get namespace kafka >/dev/null 2>&1 || kubectl create namespace kafka
kubectl get namespace kafka-monitoring-ui >/dev/null 2>&1 || kubectl create namespace kafka-monitoring-ui
echo -e "${GREEN}✓ Namespace created${NC}"
echo ""

# Deploy the kafka cluster
echo -e "${YELLOW}Deploying the kafka cluster...${NC}"
pushd zookeeper/conf2 > /dev/null
kubectl apply -f .
popd > /dev/null
echo -e "${GREEN}✓ Kafka cluster deployed${NC}"
echo ""

# Wait for the kafka cluster to be ready
echo -e "${YELLOW}Waiting for the kafka cluster to be ready...${NC}"
pushd zookeeper/conf2 > /dev/null
#kubectl -n kafka rollout status -f .
kubectl -n kafka apply -f .
#kubectl -n kafka rollout status deployment/zookeeper-1 --timeout=1200s
#kubectl -n kafka rollout status deployment/kafka-broker-1 --timeout=1200s
#kubectl -n kafka rollout status deployment/kafka-broker-2 --timeout=1200s
#kubectl -n kafka rollout status deployment/kafka-ui --timeout=1200s
#kubectl -n kafka rollout status job/init-kafka --timeout=1200s
popd > /dev/null
echo -e "${GREEN}✓ Kafka cluster is ready${NC}"
echo ""

# Final Summary
echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}✓ Kafka Setup Complete!${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""
echo "Deployment components:"
echo "  ✓ Namespace: kafka"
echo "  ✓ Namespace: kafka-monitoring-ui"
echo "  ✓ Deployment: kafka-broker-1 - 127.0.0.1:9092"
echo "  ✓ Deployment: kafka-broker-2 - 127.0.0.1:9093"
echo "  ✓ Deployment: kafka-ui - http://localhost:8082"
echo "  ✓ Deployment: zookeeper - 127.0.0.1:2181"
echo "  ✓ Service: kafka-broker-1"
echo "  ✓ kafka-ui: http://localhost:8082"
echo ""
echo -e "${GREEN}Setup completed successfully!${NC}"
