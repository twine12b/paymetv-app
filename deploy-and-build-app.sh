#!/bin/bash
set -euo pipefail

# set the frontend directory
frontend_dir="src/main/resources/frontend"

# get repository root
ROOT_DIR="$(git rev-parse --show-toplevel 2>/dev/null || true)"

if [ -z "$ROOT_DIR" ]; then
  echo "Repository root not found" >&2
  exit 1
fi

# set the frontend full path
frontend_full="$ROOT_DIR/${frontend_dir}"

#Build the frontend
cd "$frontend_full"
npm run build

# build the backend
cd "$ROOT_DIR"
mvn clean install

# build the docker image
docker build -t paymetv/paymetv-app:latest .
docker run -dp 8080:80 paymetv/paymetv-app:latest

