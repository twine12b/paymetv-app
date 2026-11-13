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

# set the frontend directory
frontend_dir="src/main/resources/frontend"

# get repository root
ROOT_DIR="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"

if [ -z "$ROOT_DIR" ]; then
  echo -e "${RED}Error: Repository root not found${NC}" >&2
  exit 1
fi

cd "${ROOT_DIR}"

echo -e "${YELLOW}Step 1: Building frontend...${NC}"
pushd $frontend_dir > /dev/null
npm install --force
npm run build
popd > /dev/null
echo -e "${GREEN}✓ Frontend built successfully${NC}"
echo ""

echo -e "${YELLOW}Step 2: Building backend with Maven...${NC}"
[ -d target ] && rm -r ./target/
mvn clean package -DskipTests
APP_JAR="${ROOT_DIR}/target/paymetv-app-0.0.1-SNAPSHOT.jar"
if [ ! -f "${APP_JAR}" ]; then
  echo -e "${RED}Error: Maven did not create ${APP_JAR}${NC}" >&2
  exit 1
fi
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

echo -e "${YELLOW}Step 4: Logging in to Docker Hub...${NC}"
docker login -u "${DOCKER_USERNAME}"

build_and_push_image() {
  local image_name="$1"
  local image_tag="$2"
  local platforms="$3"
  local context="$4"
  local dockerfile="$5"
  local target="$6"
  shift 6
  local input_paths=("$@")
  local context_dir
  context_dir="$(cd "${context}" && pwd -P)"
  local image="${image_name}:${image_tag}"
  local first_platform="${platforms%%,*}"
  local checksum_input
  local checksum
  local remote_checksum=""
  local build_args=(--platform "${platforms}" --tag "${image}" --file "${context_dir}/${dockerfile}")

  # Hash only files that can affect this image. Hashing these inputs before
  # Buildx runs avoids rebuilding unchanged images; a label on the pushed image
  # lets later runs compare against the checksum stored in the registry.
  checksum_input="$(
    cd "${context_dir}"
    for path in "${input_paths[@]}"; do
      if [ -d "${path}" ]; then
        find "${path}" \
          \( -name .venv -o -name __pycache__ -o -name .idea \
             -o -name image_in -o -name image_out \
             -o -path "${path}/code/cocosynth/datasets" \
             -path "${path}/code/cocosynth/models*" \) -prune -o \
          \( -name '*.pyc' -o -name '*.pyo' -o -name .DS_Store \) -prune -o \
          -type f -print0
      elif [ -f "${path}" ]; then
        printf '%s\0' "${path}"
      else
        echo "Missing build input: ${context}/${path}" >&2
        exit 1
      fi
    done | sort -z | while IFS= read -r -d '' path; do
      shasum -a 256 "${path}"
    done | shasum -a 256 | awk '{print $1}'
  )"
  checksum="${checksum_input}"

  echo -e "${YELLOW}Checking ${image} build-input checksum...${NC}"
  if docker pull --platform "${first_platform}" "${image}" >/dev/null 2>&1; then
    remote_checksum="$(docker image inspect --format '{{ index .Config.Labels "com.paymetv.build-input-sha256" }}' "${image}" 2>/dev/null || true)"
  fi

  if [ "${checksum}" = "${remote_checksum}" ]; then
    echo -e "${GREEN}✓ ${image} already exists with matching checksum; skipping build and push${NC}"
    echo ""
    return
  fi

  echo "Build-input checksum: ${checksum}"
  echo "Registry checksum:    ${remote_checksum:-none}"
  build_args+=(--label "com.paymetv.build-input-sha256=${checksum}")

  if [ -n "${target}" ]; then
    build_args+=(--target "${target}")
  fi

  echo -e "${YELLOW}Building ${image} for ${platforms}...${NC}"
  docker buildx build "${build_args[@]}" --push "${context_dir}"
  echo -e "${GREEN}✓ Built and pushed ${image}${NC}"
  echo ""
}

echo -e "${YELLOW}Step 5: Building and publishing all deployed PayMeTV images...${NC}"
build_and_push_image "paymetv/paymetv-app" "${IMAGE_TAG}" "${PLATFORMS}" "." "Dockerfile" "" \
  "Dockerfile" "pom.xml" "mvnw" "src"
build_and_push_image "paymetv/streaming-app" "latest" "${PLATFORMS}" \
  "src/main/resources/streaming" "Dockerfile" "" \
  "Dockerfile" "conf/lighttpd.conf" "html"
build_and_push_image "paymetv/paymetv_db" "latest" "${PLATFORMS}" \
  "src/main/resources/database" "Dockerfile" "" \
  "Dockerfile" "dumps/database.sql" "initialise_db.sql"
build_and_push_image "paymetv/ml-trainer" "cpu-latest" "${PLATFORMS}" \
  "src/main/resources/ml" "Dockerfile" "cpu" \
  "Dockerfile" "requirements1.txt" "service.py" "code" "default_ml_structure" "image1.jpeg"
# NVIDIA CUDA images are currently AMD64-only; publish that flavor for GPU clusters.
build_and_push_image "paymetv/ml-trainer" "cuda-latest" "linux/amd64" \
  "src/main/resources/ml" "Dockerfile" "cuda" \
  "Dockerfile" "requirements2.txt" "service.py" "code" "default_ml_structure" "image1.jpeg"

echo ""
echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}Build Complete!${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""
echo "Published images:"
echo "  paymetv/paymetv-app:${IMAGE_TAG} (${PLATFORMS})"
echo "  paymetv/streaming-app:latest (${PLATFORMS})"
echo "  paymetv/paymetv_db:latest (${PLATFORMS})"
echo "  paymetv/ml-trainer:cpu-latest (${PLATFORMS})"
echo "  paymetv/ml-trainer:cuda-latest (linux/amd64 only)"
echo ""
echo -e "${GREEN}You can now deploy this image to your Kubernetes cluster!${NC}"
