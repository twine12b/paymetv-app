#!/usr/bin/env bash
set -euo pipefail

python3 -m venv .venv
source .venv/bin/activate
python -m pip install --upgrade pip

if command -v nvidia-smi >/dev/null 2>&1 && nvidia-smi >/dev/null 2>&1; then
  echo "**** NVIDIA GPU detected; installing requirements2.txt"
  pip install -r requirements2.txt
else
  echo "**** No NVIDIA GPU detected; installing requirements1.txt"
  pip install -r requirements1.txt
fi