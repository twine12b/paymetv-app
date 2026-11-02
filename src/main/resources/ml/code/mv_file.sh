#!/usr/bin/env bash
set -euo pipefail

ACTIVE_USER=$1
CATEGORY=$2
SUB_CAT=$3

if [[ $# -ne 3 ]]; then
    echo "Usage: $0 <active_user> <category> <sub_cat>" >&2
    exit 2
fi

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_STRUCTURE="${SCRIPT_DIR}/../default_ml_structure"
DATASETS_DIR="/data/${ACTIVE_USER}/datasets"

if [[ ! -d "$DEFAULT_STRUCTURE" ]]; then
    echo "Default structure not found: $DEFAULT_STRUCTURE" >&2
    exit 1
fi

mkdir -p "$DATASETS_DIR"
cp -R "$DEFAULT_STRUCTURE"/. "$DATASETS_DIR"/
mkdir -p "$DATASETS_DIR/input/foregrounds/${CATEGORY}/${SUB_CAT}"

cp -R "/data/${ACTIVE_USER}/result"/. "${DATASETS_DIR}/input/foregrounds/${CATEGORY}/${SUB_CAT}"/

echo "File structure created successfully."