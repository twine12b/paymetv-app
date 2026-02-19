# Horizontal Pod Autoscaler (HPA) Load Testing Guide

## Overview

This guide explains how to implement, deploy, and test Horizontal Pod Autoscaling (HPA) for the PayMeTV Spring Boot application using Gatling load tests.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Architecture](#architecture)
3. [Setup Instructions](#setup-instructions)
4. [Running Load Tests](#running-load-tests)
5. [Monitoring HPA](#monitoring-hpa)
6. [Test Scenarios](#test-scenarios)
7. [Troubleshooting](#troubleshooting)

## Prerequisites

### Required Tools

- **Kubernetes Cluster** (v1.23+)
  - Minikube, Kind, or cloud provider (GKE, EKS, AKS)
- **kubectl** (v1.23+)
- **Docker** (v20.10+)
- **Maven** (v3.8+)
- **Java** (v17+)
- **Node.js** (v18+) and npm
- **jq** (for monitoring scripts)

### Kubernetes Requirements

1. **Metrics Server** - Required for HPA to function
   ```bash
   kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
   ```

2. **Sufficient Cluster Resources**
   - Minimum: 4 CPU cores, 8GB RAM
   - Recommended: 8 CPU cores, 16GB RAM

## Architecture

### HPA Configuration

- **Target Metric**: CPU Utilization at 70%
- **Min Replicas**: 2
- **Max Replicas**: 10
- **Scale Up Policy**: Aggressive (double pods every 15s, max 4 pods)
- **Scale Down Policy**: Conservative (50% reduction every 60s, max 2 pods)

### Resource Limits

Each pod is configured with:
- **CPU Request**: 200m (0.2 cores)
- **CPU Limit**: 500m (0.5 cores)
- **Memory Request**: 256Mi
- **Memory Limit**: 512Mi

### Load Test Endpoints

The application provides CPU-intensive endpoints for testing:

1. `/api/load/cpu` - Configurable CPU load generation
2. `/api/load/fibonacci` - Recursive Fibonacci calculation
3. `/api/load/prime` - Prime number calculation
4. `/api/health` - Health check endpoint

## Setup Instructions

### Step 1: Verify Metrics Server

```bash
./scripts/verify-metrics-server.sh
```

This script will:
- Check if metrics-server is installed
- Install it if missing
- **Automatically detect and fix TLS certificate issues** (common in local clusters)
- Verify metrics are being collected

**What it fixes automatically:**
- TLS certificate verification errors (`x509: cannot validate certificate`)
- Readiness probe failures in local clusters (Docker Desktop, Minikube, Kind)
- Adds `--kubelet-insecure-tls` flag for local development clusters
- Configures proper kubelet address types

**Note**: The script detects your cluster type and only applies TLS bypass for local development clusters. Production clusters are not modified.

### Step 2: Build and Deploy Application

```bash
./scripts/deploy-hpa-setup.sh [namespace]
```

This script will:
1. Verify metrics-server
2. Build the React frontend
3. Build the Spring Boot application
4. Create Docker image
5. Deploy to Kubernetes
6. Configure HPA
7. Display deployment status

**Example:**
```bash
./scripts/deploy-hpa-setup.sh default
```

### Step 3: Verify Deployment

```bash
kubectl get all -n default -l app=paymetv-app
kubectl get hpa paymetv-app-hpa -n default
```

Expected output:
```
NAME                                        READY   STATUS    RESTARTS   AGE
pod/paymetv-app-deployment-xxxxxxxxx-xxxxx  1/1     Running   0          2m

NAME                          TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)        AGE
service/paymetv-app-service   LoadBalancer   10.96.xxx.xxx   <pending>     80:xxxxx/TCP   2m

NAME                                     READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/paymetv-app-deployment   2/2     2            2           2m

NAME                                                REFERENCE                           TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
horizontalpodautoscaler.autoscaling/paymetv-app-hpa deployment.apps/paymetv-app-deployment   0%/70%    2         10        2          2m
```

## Running Load Tests

### Test Types

1. **Scale-Up Test** - Gradually increases load to trigger scale-up
2. **Scale-Down Test** - Decreases load to trigger scale-down
3. **Sustained Load Test** - Maintains constant load for stability testing
4. **Spike Test** - Creates sudden load spikes

### Running Tests

#### 1. Scale-Up Test

```bash
./scripts/run-gatling-tests.sh scale-up http://<service-url>
```

**Parameters** (via system properties):
- `targetRps`: Target requests per second (default: 100)
- `rampDuration`: Ramp-up duration in minutes (default: 5)
- `sustainDuration`: Sustained load duration in minutes (default: 3)

**Example with custom parameters:**
```bash
mvn gatling:test \
  -Dgatling.simulationClass=com.paymetv.app.gatling.HPAScaleUpSimulation \
  -DbaseUrl=http://paymetv.example.com \
  -DtargetRps=150 \

**Parameters:**
- `initialRps`: Initial requests per second (default: 100)
- `rampDownDuration`: Ramp-down duration in minutes (default: 5)
- `coolDownDuration`: Cool-down duration in minutes (default: 3)

#### 3. Sustained Load Test

```bash
./scripts/run-gatling-tests.sh sustained http://<service-url>
```

**Parameters:**
- `targetRps`: Target requests per second (default: 80)
- `testDuration`: Test duration in minutes (default: 10)

#### 4. Spike Test

```bash
./scripts/run-gatling-tests.sh spike http://<service-url>
```

**Parameters:**
- `baselineRps`: Baseline requests per second (default: 20)
- `spikeRps`: Spike requests per second (default: 150)
- `spikeDuration`: Spike duration in minutes (default: 2)

## Monitoring HPA

### Real-Time Monitoring

Start the HPA monitor in a separate terminal before running load tests:

```bash
./scripts/monitor-hpa.sh [namespace] [interval_seconds]
```

**Example:**
```bash
./scripts/monitor-hpa.sh default 5
```

This will display:
- Current and desired replica counts
- CPU utilization (current vs target)
- Scaling status (up/down/stable)
- Individual pod resource usage
- Logs data to a timestamped CSV file

### Manual Monitoring Commands

```bash
# Watch HPA status
kubectl get hpa paymetv-app-hpa -n default --watch

# Watch pod count
kubectl get pods -n default -l app=paymetv-app --watch

# View pod CPU/memory usage
kubectl top pods -n default -l app=paymetv-app

# View HPA events
kubectl describe hpa paymetv-app-hpa -n default

# View HPA details in JSON
kubectl get hpa paymetv-app-hpa -n default -o json
```

## Test Scenarios

### Scenario 1: Scale-Up Verification

**Objective**: Verify HPA scales up when CPU exceeds 70%

**Steps**:
1. Start with 2 replicas (minimum)
2. Begin monitoring: `./scripts/monitor-hpa.sh default 5`
3. Run scale-up test: `./scripts/run-gatling-tests.sh scale-up http://<url>`
4. Observe:
   - CPU utilization increases above 70%
   - Desired replicas increases
   - New pods are created
   - Load is distributed across pods
   - CPU utilization stabilizes around 70%

**Expected Results**:
- Pods scale from 2 to 6-8 replicas
- Scaling occurs within 30-60 seconds of CPU threshold breach
- CPU stabilizes at ~70% after scaling

### Scenario 2: Scale-Down Verification

**Objective**: Verify HPA scales down when CPU drops below 70%

**Steps**:
1. Start with scaled-up state (6-8 replicas)
2. Begin monitoring: `./scripts/monitor-hpa.sh default 5`
3. Run scale-down test: `./scripts/run-gatling-tests.sh scale-down http://<url>`
4. Observe:
   - CPU utilization decreases below 70%
   - Desired replicas decreases
   - Pods are terminated gracefully
   - Minimum of 2 replicas maintained

**Expected Results**:
- Pods scale down to 2-3 replicas
- Scale-down occurs after 60s stabilization window
- No more than 50% of pods removed at once

### Scenario 3: Sustained Load Stability

**Objective**: Verify HPA maintains stable replica count under constant load

**Steps**:
1. Begin monitoring: `./scripts/monitor-hpa.sh default 5`
2. Run sustained test: `./scripts/run-gatling-tests.sh sustained http://<url>`
3. Observe:
   - Replica count stabilizes
   - CPU hovers around 70%
   - No unnecessary scaling events

**Expected Results**:
- Replica count remains stable (±1 pod)
- CPU utilization stays within 60-80% range
- No thrashing (rapid scale up/down)

### Scenario 4: Spike Response

**Objective**: Verify HPA responds quickly to sudden load increases

**Steps**:
1. Begin monitoring: `./scripts/monitor-hpa.sh default 5`
2. Run spike test: `./scripts/run-gatling-tests.sh spike http://<url>`
3. Observe:
   - Rapid scale-up during spike
   - Scale-down after spike ends
   - Multiple spike handling

**Expected Results**:
- Pods scale up within 15-30 seconds of spike
- Maximum scale-up rate: 4 pods per 15 seconds
- Graceful scale-down after spike ends

## Viewing Test Reports

After each Gatling test, a detailed HTML report is generated:

```bash
# Open the latest report
open target/gatling/$(ls -t target/gatling/ | head -1)/index.html

# Or on Linux
xdg-open target/gatling/$(ls -t target/gatling/ | head -1)/index.html
```

The report includes:
- Request/response time statistics
- Throughput metrics
- Success/failure rates
- Response time distribution
- Active users over time

## Troubleshooting

### HPA Shows "unknown" for CPU Metrics

**Cause**: Metrics server not installed or not collecting metrics

**Solution**:
```bash
./scripts/verify-metrics-server.sh
# Wait 1-2 minutes for metrics collection
kubectl top pods -n default
```

**If the issue persists**, check for TLS certificate errors:
```bash
# Check metrics-server logs
kubectl logs -n kube-system -l k8s-app=metrics-server --tail=50

# Look for errors like:
# "x509: cannot validate certificate for <IP> because it doesn't contain any IP SANs"

# The verify-metrics-server.sh script should fix this automatically
# If not, manually patch the deployment:
kubectl patch deployment metrics-server -n kube-system --type='json' -p='[
  {
    "op": "add",
    "path": "/spec/template/spec/containers/0/args/-",
    "value": "--kubelet-insecure-tls"
  }
]'

# Wait for rollout
kubectl rollout status deployment/metrics-server -n kube-system
```

### HPA Not Scaling

**Possible Causes**:
1. Resource requests not defined
2. CPU not reaching threshold
3. Metrics server not working

**Solutions**:
```bash
# Check resource requests are defined
kubectl get deployment paymetv-app-deployment -n default -o yaml | grep -A 5 resources

# Check current CPU usage
kubectl top pods -n default -l app=paymetv-app

# Check HPA events
kubectl describe hpa paymetv-app-hpa -n default
```

### Pods Stuck in Pending State

**Cause**: Insufficient cluster resources

**Solution**:
```bash
# Check node resources
kubectl top nodes

# Check pod events
kubectl describe pod <pod-name> -n default

# Scale down max replicas if needed
kubectl patch hpa paymetv-app-hpa -n default -p '{"spec":{"maxReplicas":5}}'
```

### Load Test Fails to Connect

**Cause**: Service not accessible or wrong URL

**Solution**:
```bash
# Get service details
kubectl get svc paymetv-app-service -n default

# For LoadBalancer
kubectl get svc paymetv-app-service -n default -o jsonpath='{.status.loadBalancer.ingress[0].ip}'

# For NodePort
kubectl get svc paymetv-app-service -n default -o jsonpath='{.spec.ports[0].nodePort}'

# Test connectivity
curl http://<service-url>/api/health
```

## Best Practices

1. **Always monitor HPA during tests** - Use the monitoring script to observe scaling behavior
2. **Start with small load** - Gradually increase to understand scaling patterns
3. **Allow stabilization time** - Wait for metrics to stabilize between tests
4. **Review Gatling reports** - Analyze performance metrics after each test
5. **Check pod logs** - Monitor application logs for errors during high load
6. **Adjust thresholds** - Fine-tune CPU target based on application behavior
7. **Test scale-down** - Verify graceful scale-down to avoid service disruption

## Advanced Configuration

### Adjusting HPA Behavior

Edit `src/main/resources/k8s/hpa.yaml`:

```yaml
# More aggressive scale-up
behavior:
  scaleUp:
    stabilizationWindowSeconds: 0
    policies:
      - type: Percent
        value: 200  # Triple pods
        periodSeconds: 15
```

### Custom Metrics

HPA can also scale based on custom metrics (requires Prometheus):

```yaml
metrics:
  - type: Pods
    pods:
      metric:
        name: http_requests_per_second
      target:
        type: AverageValue
        averageValue: "1000"
```

## Cleanup

To remove the HPA setup:

```bash
kubectl delete hpa paymetv-app-hpa -n default
kubectl delete deployment paymetv-app-deployment -n default
kubectl delete svc paymetv-app-service -n default
```

## References

- [Kubernetes HPA Documentation](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)
- [Gatling Documentation](https://gatling.io/docs/gatling/)
- [Metrics Server](https://github.com/kubernetes-sigs/metrics-server)

#### 2. Scale-Down Test

```bash
./scripts/run-gatling-tests.sh scale-down http://<service-url>
```


