#!/bin/sh

set -e  # Exit on error

FRONTEND_DIR="./conf"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}PayMeTV Frontend Setup Script${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Build the frontend
echo -e "${YELLOW}Step 1: Building frontend...${NC}"
npm install
npm run build
echo -e "${GREEN}✓ Frontend built${NC}"
echo ""