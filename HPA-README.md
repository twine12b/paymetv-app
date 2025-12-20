# PayMeTV HPA Load Testing Implementation

## 🎯 Overview

Complete implementation of Horizontal Pod Autoscaling (HPA) for the PayMeTV Spring Boot application with comprehensive Gatling load tests for verification.

## ✨ Features

- ✅ **Production-Ready HPA** - CPU-based autoscaling (70% target, 2-10 replicas)
- ✅ **4 Gatling Test Scenarios** - Scale-up, scale-down, sustained load, and spike tests
- ✅ **Real-Time Monitoring** - Beautiful terminal UI with CSV logging
- ✅ **CPU Load Endpoints** - Multiple endpoints for realistic load generation
- ✅ **Automated Deployment** - One-command setup and deployment
- ✅ **Comprehensive Docs** - Quick start and detailed guides
- ✅ **Metrics Verification** - Automated metrics-server setup

## 🚀 Quick Start (5 Minutes)

### 1. Deploy Application with HPA

```bash
./scripts/deploy-hpa-setup.sh default
```

### 2. Start Monitoring (in new terminal)

```bash
./scripts/monitor-hpa.sh default 5
```

### 3. Get Service URL

```bash
# For LoadBalancer
export SERVICE_URL=http://$(kubectl get svc paymetv-app-service -n default -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

# For NodePort
export NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}')
export NODE_PORT=$(kubectl get svc paymetv-app-service -n default -o jsonpath='{.spec.ports[0].nodePort}')
export SERVICE_URL=http://${NODE_IP}:${NODE_PORT}
```

### 4. Run Load Test

```bash
./scripts/run-gatling-tests.sh scale-up ${SERVICE_URL}
```

### 5. View Results

```bash
open target/gatling/$(ls -t target/gatling/ | head -1)/index.html
```

## 📁 What's Included

### Kubernetes Manifests

- **`src/main/resources/k8s/deployment-with-hpa.yaml`** - Deployment with resource limits
- **`src/main/resources/k8s/hpa.yaml`** - HPA configuration (70% CPU, 2-10 replicas)
- **`src/main/resources/k8s/service.yaml`** - LoadBalancer service

### Spring Boot Endpoints

**`src/main/java/com/paymetv/app/controller/LoadTestController.java`**

- `GET /api/load/cpu` - Configurable CPU load
- `GET /api/load/fibonacci` - Recursive Fibonacci
- `GET /api/load/prime` - Prime number calculation
- `GET /api/load/stats` - Load test statistics

### Gatling Simulations

**`src/test/scala/com/paymetv/app/gatling/`**

1. **HPAScaleUpSimulation** - Test scale-up behavior
2. **HPAScaleDownSimulation** - Test scale-down behavior
3. **HPASustainedLoadSimulation** - Test stability
4. **HPASpikeTestSimulation** - Test spike response

### Scripts

- **`scripts/deploy-hpa-setup.sh`** - Complete deployment automation
- **`scripts/monitor-hpa.sh`** - Real-time HPA monitoring
- **`scripts/verify-metrics-server.sh`** - Metrics server verification
- **`scripts/run-gatling-tests.sh`** - Load test runner

### Documentation

- **`docs/HPA-QUICK-START.md`** - 5-minute quick start guide
- **`docs/HPA-LOAD-TESTING-GUIDE.md`** - Comprehensive guide
- **`docs/HPA-IMPLEMENTATION-SUMMARY.md`** - Implementation details
- **`src/test/scala/com/paymetv/app/gatling/README.md`** - Gatling docs

## 📊 Test Scenarios

### Scale-Up Test
```bash
./scripts/run-gatling-tests.sh scale-up ${SERVICE_URL}
```
- Gradually increases load
- Triggers HPA scale-up
- Verifies CPU stabilizes at ~70%

### Scale-Down Test
```bash
./scripts/run-gatling-tests.sh scale-down ${SERVICE_URL}
```
- Decreases load gradually
- Triggers HPA scale-down
- Verifies minimum replicas maintained

### Sustained Load Test
```bash
./scripts/run-gatling-tests.sh sustained ${SERVICE_URL}
```
- Maintains constant load
- Verifies stable replica count
- Tests long-term stability

### Spike Test
```bash
./scripts/run-gatling-tests.sh spike ${SERVICE_URL}
```
- Creates sudden load spikes
- Tests rapid scale-up
- Verifies graceful scale-down

## 🔍 Monitoring

### Real-Time HPA Monitor

```bash
./scripts/monitor-hpa.sh default 5
```

Displays:
- Current vs desired replicas
- CPU utilization (current vs target)
- Scaling status (up/down/stable)
- Individual pod resource usage
- Logs to timestamped CSV file

### Manual Commands

```bash
# Watch HPA status
kubectl get hpa paymetv-app-hpa -n default --watch

# View pod CPU usage
kubectl top pods -n default -l app=paymetv-app

# Check HPA events
kubectl describe hpa paymetv-app-hpa -n default
```

## 📋 Prerequisites

- Kubernetes cluster (v1.23+)
- kubectl (v1.23+)
- Docker (v20.10+)
- Maven (v3.8+)
- Java 17+
- Node.js 18+ and npm
- jq (for monitoring scripts)

## 📚 Documentation

- **Quick Start**: [docs/HPA-QUICK-START.md](docs/HPA-QUICK-START.md)
- **Full Guide**: [docs/HPA-LOAD-TESTING-GUIDE.md](docs/HPA-LOAD-TESTING-GUIDE.md)
- **Implementation**: [docs/HPA-IMPLEMENTATION-SUMMARY.md](docs/HPA-IMPLEMENTATION-SUMMARY.md)
- **Gatling Tests**: [src/test/scala/com/paymetv/app/gatling/README.md](src/test/scala/com/paymetv/app/gatling/README.md)

## 🛠️ Troubleshooting

### Metrics Server Issues

If you encounter "readiness probe failed" or TLS certificate errors with metrics-server:

```bash
# Our script automatically fixes this
./scripts/verify-metrics-server.sh
```

See the detailed [Metrics Server Troubleshooting Guide](docs/METRICS-SERVER-TROUBLESHOOTING.md) for more information.

### Other Issues

See the [Troubleshooting section](docs/HPA-LOAD-TESTING-GUIDE.md#troubleshooting) in the full guide.

## 🧹 Cleanup

```bash
kubectl delete hpa paymetv-app-hpa -n default
kubectl delete deployment paymetv-app-deployment -n default
kubectl delete svc paymetv-app-service -n default
```

## 📖 Learn More

- [Kubernetes HPA Documentation](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)
- [Gatling Documentation](https://gatling.io/docs/gatling/)
- [Metrics Server](https://github.com/kubernetes-sigs/metrics-server)

