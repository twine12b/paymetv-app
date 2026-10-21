#!/bin/bash

set -e

ROOT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"

RESOURCES_DIR="${ROOT_DIR}/src/main/resources"
source "${ROOT_DIR}/scripts/commons/util.sh"

POD_DIRS=(conf database ml streaming)
if (($# > 0)); then
  POD_DIRS=("$@")
fi


echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}✓ PayMeTV New Project initialisation${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

echo -e "${YELLOW}Removing checksums from resource directories...${NC}"
for POD_DIR in "${POD_DIRS[@]}"; do
  RESOURCE_DIR="${RESOURCES_DIR}/${POD_DIR}"
  if [[ ! -d "$RESOURCE_DIR" ]]; then
    echo "Skipping missing resource directory: ${RESOURCE_DIR}" >&2
    continue
  fi

  pushd "$RESOURCE_DIR" > /dev/null
  if [[ -f checksum ]]; then
    rm checksum
    echo -e "${GREEN}Removed checksum from ${POD_DIR}.${NC}"
    echo ""
  fi
  popd > /dev/null
done

# Install ML Python dependencies using the ML initializer, which selects the
# CPU or CUDA requirements based on whether an NVIDIA GPU is available.
ML_DIR="${RESOURCES_DIR}/ml"
echo -e "${YELLOW}Installing Python dependencies in ${ML_DIR}...${NC}"
pushd "$ML_DIR" >/dev/null
bash ./init.sh
popd >/dev/null

echo -e "${GREEN}✓ Python dependencies installed successfully.${NC}"
echo ""


