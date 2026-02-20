#!/bin/sh

docker rm "$(docker ps -q --filter "publish=8081")" 2>/dev/null || true
docker build -t paymetv/streaming-app:latest . -f Dockerfile
docker run -d -p 8081:80 -it paymetv/streaming-app:latest
curl -I http://localhost:8081/ | grep HTTP/1.1