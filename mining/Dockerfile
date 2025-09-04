FROM ubuntu:latest
LABEL authors="twine"

# Update package list and install required packages
RUN apt-get update && apt-get install -y \
    curl \
    nginx \
    nvidia-driver-535 \
    nvidia-cuda-toolkit \
    nvidia-utils-535 \
    nvidia-settings \
    nvidia-modprobe \
    wget \
    pciutils \
    netcat-openbsd \
    dnsutils \
    telnet \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Set NVIDIA environment variables for Docker
ENV NVIDIA_VISIBLE_DEVICES=all
ENV NVIDIA_DRIVER_CAPABILITIES=compute,utility

# Create GPU initialization script with NVAPI fix
RUN echo '#!/bin/bash' > /usr/local/bin/init-gpu.sh && \
    echo 'echo "Initializing GPU with NVAPI support..."' >> /usr/local/bin/init-gpu.sh && \
    echo '# Load NVIDIA kernel modules' >> /usr/local/bin/init-gpu.sh && \
    echo 'nvidia-modprobe -u -c=0 || echo "nvidia-modprobe failed"' >> /usr/local/bin/init-gpu.sh && \
    echo '# Initialize NVIDIA Management Library' >> /usr/local/bin/init-gpu.sh && \
    echo 'nvidia-smi -pm 1 || echo "Failed to set persistence mode"' >> /usr/local/bin/init-gpu.sh && \
    echo '# Test GPU access' >> /usr/local/bin/init-gpu.sh && \
    echo 'nvidia-smi || echo "nvidia-smi failed - GPU may not be available"' >> /usr/local/bin/init-gpu.sh && \
    echo '# Set GPU performance mode' >> /usr/local/bin/init-gpu.sh && \
    echo 'nvidia-smi -ac 877,1911 || echo "Failed to set application clocks"' >> /usr/local/bin/init-gpu.sh && \
    echo '# Create device files if missing' >> /usr/local/bin/init-gpu.sh && \
    echo 'ls -la /dev/nvidia* || echo "NVIDIA device files not found"' >> /usr/local/bin/init-gpu.sh && \
    echo 'echo "GPU initialization complete"' >> /usr/local/bin/init-gpu.sh && \
    chmod +x /usr/local/bin/init-gpu.sh

# Create kawpow mining script with NVAPI fixes
RUN echo '#!/bin/bash' > /usr/local/bin/mine-kawpow.sh && \
    echo 'echo "Starting kawpow mining with NVAPI support..."' >> /usr/local/bin/mine-kawpow.sh && \
    echo '/usr/local/bin/init-gpu.sh' >> /usr/local/bin/mine-kawpow.sh && \
    echo 'cd /nicehash/NBMiner_Linux' >> /usr/local/bin/mine-kawpow.sh && \
    echo 'echo "Checking NBMiner version and supported algorithms..."' >> /usr/local/bin/mine-kawpow.sh && \
    echo './nbminer --help | grep -i kawpow || echo "kawpow not supported, trying alternatives..."' >> /usr/local/bin/mine-kawpow.sh && \
    echo 'echo "Mining with NVAPI disabled flag..."' >> /usr/local/bin/mine-kawpow.sh && \
    echo './nbminer -a kawpow -o stratum+tcp://kawpow.auto.nicehash.com:9200 -u $WALLET_ADDRESS.$WORKER_NAME --no-nvml --no-health || echo "kawpow failed, trying alternative pools..."' >> /usr/local/bin/mine-kawpow.sh && \
    echo 'echo "Trying alternative kawpow pool..."' >> /usr/local/bin/mine-kawpow.sh && \
    echo './nbminer -a kawpow -o stratum+tcp://kawpow.eu.nicehash.com:9200 -u $WALLET_ADDRESS.$WORKER_NAME --no-nvml --no-health || echo "EU pool failed"' >> /usr/local/bin/mine-kawpow.sh && \
    echo './nbminer -a kawpow -o stratum+tcp://kawpow.usa.nicehash.com:9200 -u $WALLET_ADDRESS.$WORKER_NAME --no-nvml --no-health || echo "USA pool failed"' >> /usr/local/bin/mine-kawpow.sh && \
    chmod +x /usr/local/bin/mine-kawpow.sh

# Create algorithm checker script
RUN echo '#!/bin/bash' > /usr/local/bin/check-algos.sh && \
    echo 'echo "Checking supported algorithms in NBMiner..."' >> /usr/local/bin/check-algos.sh && \
    echo 'cd /nicehash/NBMiner_Linux' >> /usr/local/bin/check-algos.sh && \
    echo './nbminer --help' >> /usr/local/bin/check-algos.sh && \
    echo 'echo "Available algorithms:"' >> /usr/local/bin/check-algos.sh && \
    echo './nbminer --help | grep -A 20 "algorithm"' >> /usr/local/bin/check-algos.sh && \
    chmod +x /usr/local/bin/check-algos.sh

# Create comprehensive network connectivity test script
RUN echo '#!/bin/bash' > /usr/local/bin/test-network.sh && \
    echo 'echo "=== Docker Container Network Diagnostics ==="' >> /usr/local/bin/test-network.sh && \
    echo 'echo "1. Basic connectivity test:"' >> /usr/local/bin/test-network.sh && \
    echo 'ping -c 3 8.8.8.8 || echo "FAILED: Cannot reach 8.8.8.8"' >> /usr/local/bin/test-network.sh && \
    echo 'ping -c 3 google.com || echo "FAILED: Cannot reach google.com"' >> /usr/local/bin/test-network.sh && \
    echo 'echo "2. DNS resolution test:"' >> /usr/local/bin/test-network.sh && \
    echo 'nslookup kawpow.auto.nicehash.com || echo "FAILED: DNS resolution failed"' >> /usr/local/bin/test-network.sh && \
    echo 'echo "3. HTTP connectivity test:"' >> /usr/local/bin/test-network.sh && \
    echo 'curl -I --connect-timeout 10 https://www.google.com || echo "FAILED: HTTP connectivity failed"' >> /usr/local/bin/test-network.sh && \
    echo 'echo "4. Mining pool connectivity test:"' >> /usr/local/bin/test-network.sh && \
    echo 'nc -zv kawpow.auto.nicehash.com 9200 || echo "FAILED: Auto pool unreachable"' >> /usr/local/bin/test-network.sh && \
    echo 'nc -zv kawpow.eu.nicehash.com 9200 || echo "FAILED: EU pool unreachable"' >> /usr/local/bin/test-network.sh && \
    echo 'echo "5. Network configuration:"' >> /usr/local/bin/test-network.sh && \
    echo 'ip addr show || ifconfig' >> /usr/local/bin/test-network.sh && \
    echo 'cat /etc/resolv.conf' >> /usr/local/bin/test-network.sh && \
    echo 'echo "=== Network test complete ==="' >> /usr/local/bin/test-network.sh && \
    chmod +x /usr/local/bin/test-network.sh

# Create stratum connection test script
RUN echo '#!/bin/bash' > /usr/local/bin/test-stratum.sh && \
    echo 'echo "=== Testing Stratum Pool Connections ==="' >> /usr/local/bin/test-stratum.sh && \
    echo 'POOL_HOST="daggerhashimoto.auto.nicehash.com"' >> /usr/local/bin/test-stratum.sh && \
    echo 'POOL_PORT="9200"' >> /usr/local/bin/test-stratum.sh && \
    echo 'echo "1. DNS Resolution Test:"' >> /usr/local/bin/test-stratum.sh && \
    echo 'nslookup $POOL_HOST || echo "FAILED: DNS resolution failed for $POOL_HOST"' >> /usr/local/bin/test-stratum.sh && \
    echo 'echo "2. Ping Test:"' >> /usr/local/bin/test-stratum.sh && \
    echo 'ping -c 3 $POOL_HOST || echo "FAILED: Cannot ping $POOL_HOST"' >> /usr/local/bin/test-stratum.sh && \
    echo 'echo "3. Port Connectivity Test:"' >> /usr/local/bin/test-stratum.sh && \
    echo 'nc -zv $POOL_HOST $POOL_PORT || echo "FAILED: Port $POOL_PORT is not reachable"' >> /usr/local/bin/test-stratum.sh && \
    echo 'echo "4. Telnet Test (5 second timeout):"' >> /usr/local/bin/test-stratum.sh && \
    echo 'timeout 5 telnet $POOL_HOST $POOL_PORT || echo "FAILED: Telnet connection failed"' >> /usr/local/bin/test-stratum.sh && \
    echo 'echo "5. Advanced Stratum Protocol Test:"' >> /usr/local/bin/test-stratum.sh && \
    echo 'echo "Testing stratum protocol handshake..."' >> /usr/local/bin/test-stratum.sh && \
    echo '(echo "{\"id\":1,\"method\":\"mining.subscribe\",\"params\":[]}"; sleep 2) | nc $POOL_HOST $POOL_PORT || echo "FAILED: Stratum protocol test failed"' >> /usr/local/bin/test-stratum.sh && \
    echo 'echo "=== Stratum test complete ==="' >> /usr/local/bin/test-stratum.sh && \
    chmod +x /usr/local/bin/test-stratum.sh

# Create alternative mining script for different algorithms
RUN echo '#!/bin/bash' > /usr/local/bin/mine-eth.sh && \
    echo 'echo "Starting Ethereum mining..."' >> /usr/local/bin/mine-eth.sh && \
    echo '/usr/local/bin/init-gpu.sh' >> /usr/local/bin/mine-eth.sh && \
    echo 'cd /nicehash/NBMiner_Linux' >> /usr/local/bin/mine-eth.sh && \
    echo './nbminer -a ethash -o stratum+tcp://daggerhashimoto.auto.nicehash.com:9200 -u $WALLET_ADDRESS.$WORKER_NAME' >> /usr/local/bin/mine-eth.sh && \
    chmod +x /usr/local/bin/mine-eth.sh

COPY --chown=1000:1000 address-cyphered.yml /address-cyphered.yml
COPY --chown=1000:1000 start-mining.sh /start-mining.sh


# Create startup script that initializes GPU
RUN echo '#!/bin/bash' > /usr/local/bin/startup.sh && \
    echo 'echo "Starting container with GPU support..."' >> /usr/local/bin/startup.sh && \
    echo '/usr/local/bin/init-gpu.sh' >> /usr/local/bin/startup.sh && \
    echo 'exec "$@"' >> /usr/local/bin/startup.sh && \
    chmod +x /usr/local/bin/startup.sh

EXPOSE 8084

# Use startup script as entrypoint
RUN chmod +x /start-mining.sh
ENTRYPOINT ["/start-mining.sh"]

# Default command - you can override this when running
CMD ["bash"]