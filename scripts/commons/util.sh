#!/bin/bash

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# define working directory
SCRIPTS_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
ROOT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)"
COMMONS_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

get_timestamp_version() {
  date -u '+%Y%m%d%H%M%S'
}


