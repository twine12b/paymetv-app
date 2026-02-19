#!/bin/bash
set -euo pipefail

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}PayMeTV Multi-Architecture Build Script${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Configuration
IMAGE_NAME="paymetv/paymetv-app"
IMAGE_TAG="latest"
PLATFORMS="linux/amd64,linux/arm64"

# set the frontend directory
frontend_dir="src/main/resources/frontend"

# get repository root
ROOT_DIR="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"

if [ -z "$ROOT_DIR" ]; then
  echo -e "${RED}Error: Repository root not found${NC}" >&2
  exit 1
fi

echo -e "${YELLOW}Step 1: Building frontend...${NC}"
pushd $frontend_dir > /dev/null
npm install
npm run build
popd > /dev/null
echo -e "${GREEN}✓ Frontend built successfully${NC}"
echo ""

echo -e "${YELLOW}Step 2: Building backend with Maven...${NC}"
[ -d target ] && rm -r ./target/
mvn clean package
echo -e "${GREEN}✓ Backend built successfully${NC}"
echo ""

echo -e "${YELLOW}Step 3: Setting up Docker buildx for multi-architecture builds...${NC}"
# Create a new builder instance if it doesn't exist
if ! docker buildx inspect paymetv-builder >/dev/null 2>&1; then
  echo "Creating new buildx builder instance..."
  docker buildx create --name paymetv-builder --use
else
  echo "Using existing buildx builder instance..."
  docker buildx use paymetv-builder
fi

# Bootstrap the builder
docker buildx inspect --bootstrap
echo -e "${GREEN}✓ Docker buildx configured${NC}"
echo ""

echo -e "${YELLOW}Step 4: Building and pushing multi-architecture image...${NC}"
echo "Image: ${IMAGE_NAME}:${IMAGE_TAG}"
echo "Platforms: ${PLATFORMS}"
echo ""

# Build and push the multi-architecture image
docker buildx build \
  --platform "${PLATFORMS}" \
  --tag "${IMAGE_NAME}:${IMAGE_TAG}" \
  --push \
  .

echo -e "${GREEN}✓ Multi-architecture image built and pushed successfully${NC}"
echo ""

echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}Build Complete!${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""
echo "Image: ${IMAGE_NAME}:${IMAGE_TAG}"
echo "Platforms: ${PLATFORMS}"
echo ""
echo -e "${YELLOW}Verifying image manifest...${NC}"
docker buildx imagetools inspect "${IMAGE_NAME}:${IMAGE_TAG}"
echo ""
echo -e "${GREEN}You can now deploy this image to your Kubernetes cluster!${NC}"

