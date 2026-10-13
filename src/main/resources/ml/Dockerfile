# Build from src/main/resources/ml:
#   docker buildx build --platform linux/amd64,linux/arm64 --target cpu -t paymetv/ml-trainer:cpu .
#   docker buildx build --platform linux/amd64 --target cuda -t paymetv/ml-trainer:cuda .

FROM python:3.12-slim AS cpu
WORKDIR /opt/ml
ENV PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1
COPY requirements1.txt requirements2.txt ./

COPY init.sh ./
RUN chmod +x init.sh
CMD ["./init.sh"]

COPY service.py ./service.py
COPY code/ ./code/
EXPOSE 8080
CMD ["uvicorn", "service:app", "--host", "0.0.0.0", "--port", "8080"]

FROM nvidia/cuda:12.6.3-cudnn-runtime-ubuntu22.04 AS cuda
ENV DEBIAN_FRONTEND=noninteractive PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1
RUN apt-get update && apt-get install -y --no-install-recommends \
    python3 python3-pip ca-certificates && \
    rm -rf /var/lib/apt/lists/* && \
    ln -sf /usr/bin/python3 /usr/local/bin/python
WORKDIR /opt/ml
COPY requirements1.txt requirements2.txt ./
RUN pip3 install --no-cache-dir -r requirements2.txt
COPY service.py ./service.py
COPY code/ ./code/

EXPOSE 8080
CMD ["python3", "-m", "uvicorn", "service:app", "--host", "0.0.0.0", "--port", "8080"]
