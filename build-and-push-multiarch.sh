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
DOCKER_USERNAME="paymetv"
DOCKER_PASSWORD="Ch1ldren1553"

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

echo -e "${YELLOW}Step 4: Building multi-architecture image locally (OCI tarball)...${NC}"
echo "Image: ${IMAGE_NAME}:${IMAGE_TAG}"
echo "Platforms: ${PLATFORMS}"
echo ""

# Export the multi-arch image to a local OCI tarball — no registry push.
# type=oci writes the same manifest structure that gets pushed to a registry,
# so sha256(index.json) == the manifest-list digest Docker Hub will report.
LOCAL_OCI_TAR="/tmp/paymetv-app-local.tar"

docker buildx build \
  --platform "${PLATFORMS}" \
  --tag "${IMAGE_NAME}:${IMAGE_TAG}" \
  --output "type=oci,dest=${LOCAL_OCI_TAR},oci-mediatypes=true" \
  .

# Compute the manifest-list digest from the OCI tarball's index.json.
LOCAL_DIGEST="sha256:$(tar -xOf "${LOCAL_OCI_TAR}" index.json | sha256sum | awk '{print $1}')"

echo -e "${GREEN}✓ Multi-architecture image built locally (OCI tarball)${NC}"
echo ""

###############################################
# SHA COMPARISON + CONDITIONAL PUSH
###############################################

echo -e "${YELLOW}Step 5: Comparing local image digest with Docker Hub...${NC}"
# Use --password-stdin to avoid exposing credentials in the process list.
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin > /dev/null

# Get the top-level manifest-list digest currently on Docker Hub.
# grep "^Digest:" matches only the unindented top-level line, not the
# per-platform "Digest:" lines that appear inside the Manifests block.
REMOTE_DIGEST=$(docker buildx imagetools inspect "${IMAGE_NAME}:${IMAGE_TAG}" \
  2>/dev/null | grep "^Digest:" | awk '{print $2}' || echo "none")

echo "Local digest:  ${LOCAL_DIGEST}"
echo "Remote digest: ${REMOTE_DIGEST}"
echo ""

if [ "${LOCAL_DIGEST}" != "${REMOTE_DIGEST}" ]; then
  echo -e "${YELLOW}⚠️  Digests differ — pushing updated image to Docker Hub...${NC}"

  docker buildx build \
    --platform "${PLATFORMS}" \
    --tag "${IMAGE_NAME}:${IMAGE_TAG}" \
    --push \
    .

  echo -e "${GREEN}✓ Image pushed to Docker Hub as ${IMAGE_NAME}:${IMAGE_TAG}${NC}"
else
  echo -e "${GREEN}✓ Local and remote digests match — no push required.${NC}"
fi

###############################################

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
