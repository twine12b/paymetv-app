# Build from src/main/resources/ml:
#   docker buildx build --platform linux/amd64,linux/arm64 --target cpu -t paymetv/ml-trainer:cpu .
#   docker buildx build --platform linux/amd64 --target cuda -t paymetv/ml-trainer:cuda .

FROM python:3.12-slim AS cpu
WORKDIR /opt/ml
ENV PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    DATA_DIR=/data \
    VIRTUAL_ENV=/opt/venv \
    PATH="/opt/venv/bin:$PATH"

COPY requirements1.txt ./
RUN pip3 install --no-cache-dir -r requirements1.txt
COPY service.py ./service.py
COPY code/ ./code/
COPY default_ml_structure /opt/ml/default_ml_structure

COPY image1.jpeg /opt/ml/image1.jpeg

EXPOSE 8080
CMD ["sh", "-c", "mkdir -p /data/testUser12345 \
     && cp -n /opt/ml/image1.jpeg /data/testUser12345/image1.jpeg \
     && exec python3 -m uvicorn service:app --host 0.0.0.0 --port 8080"]

# -------------

FROM nvidia/cuda:12.6.3-cudnn-runtime-ubuntu22.04 AS cuda
WORKDIR /opt/ml
ENV PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    DATA_DIR=/data \
    VIRTUAL_ENV=/opt/venv \
    PATH="/opt/venv/bin:$PATH"

COPY requirements2.txt ./
RUN pip3 install --no-cache-dir -r requirements2.txt
COPY service.py ./service.py
COPY code/ ./code/
COPY default_ml_structure /opt/ml/default_ml_structure

COPY image1.jpeg /opt/ml/image1.jpeg

EXPOSE 8080
CMD ["sh", "-c", "mkdir -p /data/testUser12345 \
     && cp -n /opt/ml/image1.jpeg /data/testUser12345/image1.jpeg \
     && exec python3 -m uvicorn service:app --host 0.0.0.0 --port 8080"]