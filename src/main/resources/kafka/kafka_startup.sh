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

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}Error: Docker is not running. Please start Docker and try again.${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Docker is running${NC}"
echo ""

# Build the Docker image
echo -e "${YELLOW}Step 1: building the kafka docker image...${NC}"
docker-compose up -d --build
echo -e "${GREEN}✓ kafka docker image built${NC}"
echo ""

sleep 10

# Run the Kafka example
echo -e "${YELLOW}Step 2: running the kafka example...${NC}"
node kafka_connection_test.js
node index.js
echo -e "${GREEN}✓ kafka example run${NC}"
echo ""

# Stop the Kafka example
echo -e "${YELLOW}Step 3: stopping the kafka example...${NC}"
docker-compose down
echo -e "${GREEN}✓ kafka example stopped${NC}"
echo ""

echo -e "${GREEN}✓ Kafka Setup Complete!${NC}"
echo ""