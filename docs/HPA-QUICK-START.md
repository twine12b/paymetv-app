# HPA Load Testing - Quick Start Guide

## 5-Minute Setup

### Prerequisites Check

```bash
# Verify you have all required tools
kubectl version --client
docker --version
mvn --version
java --version
jq --version
```

### Step 1: Deploy Application with HPA (2 minutes)

```bash
# From project root
./scripts/deploy-hpa-setup.sh default
```

This will:
- ✅ Install/verify metrics-server
- ✅ Build frontend and backend
- ✅ Create Docker image
- ✅ Deploy to Kubernetes
- ✅ Configure HPA

### Step 2: Get Service URL (30 seconds)

```bash
# For LoadBalancer
export SERVICE_URL=http://$(kubectl get svc paymetv-app-service -n default -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

# For NodePort (if LoadBalancer not available)
export NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}')
export NODE_PORT=$(kubectl get svc paymetv-app-service -n default -o jsonpath='{.spec.ports[0].nodePort}')
export SERVICE_URL=http://${NODE_IP}:${NODE_PORT}

# Test connectivity
curl ${SERVICE_URL}/api/health
```

### Step 3: Start Monitoring (30 seconds)

Open a new terminal and run:

```bash
./scripts/monitor-hpa.sh default 5
```

Keep this running to watch HPA scale in real-time.

### Step 4: Run Load Test (2 minutes)

In your original terminal:

```bash
# Scale-up test
./scripts/run-gatling-tests.sh scale-up ${SERVICE_URL}
```

Watch the monitoring terminal to see:
- CPU utilization increase
- Pods scale from 2 → 6-8 replicas
- CPU stabilize around 70%

### Step 5: View Results

```bash
# Open Gatling report
open target/gatling/$(ls -t target/gatling/ | head -1)/index.html
```

## Quick Test Commands

```bash
# Scale-up test (5 min ramp, 3 min sustain)
./scripts/run-gatling-tests.sh scale-up ${SERVICE_URL}

# Scale-down test (5 min ramp-down, 3 min cool-down)
./scripts/run-gatling-tests.sh scale-down ${SERVICE_URL}

# Sustained load test (10 min constant load)
./scripts/run-gatling-tests.sh sustained ${SERVICE_URL}

# Spike test (sudden load bursts)
./scripts/run-gatling-tests.sh spike ${SERVICE_URL}
```

## Quick Verification Commands

```bash
# Check HPA status
kubectl get hpa paymetv-app-hpa -n default

# Check pod count
kubectl get pods -n default -l app=paymetv-app

# Check CPU usage
kubectl top pods -n default -l app=paymetv-app

# View HPA events
kubectl describe hpa paymetv-app-hpa -n default | tail -20
```

## Expected Behavior

### Scale-Up Test
- **Start**: 2 pods, ~10% CPU
- **During**: CPU rises to 80-90%
- **Result**: Scales to 6-8 pods, CPU stabilizes at ~70%
- **Time**: 30-60 seconds to scale

### Scale-Down Test
- **Start**: 6-8 pods, ~70% CPU
- **During**: CPU drops to 20-30%
- **Result**: Scales down to 2-3 pods
- **Time**: 60-90 seconds to scale (includes stabilization window)

## Troubleshooting

### HPA shows "unknown" CPU or metrics-server not ready
```bash
# This script automatically fixes TLS certificate issues
./scripts/verify-metrics-server.sh

# Wait for metrics collection (1-2 minutes)
sleep 120
kubectl get hpa -n default
```

**Common fix applied**: The script adds `--kubelet-insecure-tls` flag for local clusters to bypass TLS certificate verification issues.

See [Metrics Server Troubleshooting Guide](METRICS-SERVER-TROUBLESHOOTING.md) for details.

### Pods not scaling
```bash
# Check HPA events
kubectl describe hpa paymetv-app-hpa -n default

# Check pod CPU
kubectl top pods -n default -l app=paymetv-app
```

### Load test connection failed
```bash
# Verify service
kubectl get svc paymetv-app-service -n default

# Test endpoint
curl ${SERVICE_URL}/api/health
```

## Cleanup

```bash
kubectl delete hpa paymetv-app-hpa -n default
kubectl delete deployment paymetv-app-deployment -n default
kubectl delete svc paymetv-app-service -n default
```

## Next Steps

For detailed information, see [HPA-LOAD-TESTING-GUIDE.md](./HPA-LOAD-TESTING-GUIDE.md)

