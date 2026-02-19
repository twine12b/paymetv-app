# HPA Implementation Summary

## Overview

This document summarizes the complete Horizontal Pod Autoscaler (HPA) implementation for the PayMeTV Spring Boot application, including Gatling load tests for verification.

## What Was Implemented

### 1. Kubernetes Manifests

#### Deployment with Resource Limits
**File**: `src/main/resources/k8s/deployment-with-hpa.yaml`

- **Resource Requests**: CPU 200m, Memory 256Mi
- **Resource Limits**: CPU 500m, Memory 512Mi
- **Health Probes**: Liveness and Readiness checks
- **Initial Replicas**: 2

#### HPA Configuration
**File**: `src/main/resources/k8s/hpa.yaml`

- **Target Metric**: CPU Utilization at 70%
- **Min Replicas**: 2
- **Max Replicas**: 10
- **Scale-Up Behavior**: Aggressive (max 100% increase or 4 pods every 15s)
- **Scale-Down Behavior**: Conservative (max 50% decrease or 2 pods every 60s)

#### Service
**File**: `src/main/resources/k8s/service.yaml`

- **Type**: LoadBalancer
- **Port**: 80

### 2. Spring Boot Load Test Endpoints

**File**: `src/main/java/com/paymetv/app/controller/LoadTestController.java`

Four CPU-intensive endpoints for testing:

1. **`GET /api/load/cpu`** - Configurable CPU load generation
   - Parameters: `durationMs` (default: 1000ms), `intensity` (1-10, default: 5)
   
2. **`GET /api/load/fibonacci`** - Recursive Fibonacci calculation
   - Parameters: `n` (default: 35, max: 45)
   
3. **`GET /api/load/prime`** - Prime number calculation
   - Parameters: `limit` (default: 100000, max: 1000000)
   
4. **`GET /api/load/stats`** - Load test statistics

### 3. Gatling Load Test Simulations

All simulations are written in Scala and located in `src/test/scala/com/paymetv/app/gatling/`

#### HPAScaleUpSimulation.scala
- **Purpose**: Test scale-up behavior
- **Pattern**: Gradual ramp-up from 0 to target RPS
- **Duration**: 5 min ramp + 3 min sustain (configurable)
- **Scenarios**: CPU load, Fibonacci, Prime numbers, Health checks

#### HPAScaleDownSimulation.scala
- **Purpose**: Test scale-down behavior
- **Pattern**: High load → gradual decrease
- **Duration**: 5 min ramp-down + 3 min cool-down (configurable)
- **Scenarios**: CPU load, Fibonacci

#### HPASustainedLoadSimulation.scala
- **Purpose**: Test stability under constant load
- **Pattern**: Constant load for extended period
- **Duration**: 2 min ramp + 10 min sustain (configurable)
- **Scenarios**: Mixed workload (50% CPU, 30% Fib, 15% Prime, 5% Health)

#### HPASpikeTestSimulation.scala
- **Purpose**: Test response to sudden load spikes
- **Pattern**: Baseline → Spike → Baseline → Spike
- **Duration**: Multiple spike cycles
- **Scenarios**: CPU intensive, Fibonacci

### 4. Monitoring and Deployment Scripts

#### monitor-hpa.sh
**File**: `scripts/monitor-hpa.sh`

- Real-time HPA monitoring with colored output
- Displays: replica counts, CPU metrics, scaling status, pod usage
- Logs data to timestamped CSV file
- Refresh interval configurable (default: 5s)

#### verify-metrics-server.sh
**File**: `scripts/verify-metrics-server.sh`

- **Automatically detects cluster type** (local vs cloud)
- Checks if metrics-server is installed
- Installs if missing
- **Detects and fixes TLS certificate issues** automatically
- Applies `--kubelet-insecure-tls` flag for local clusters
- Configures proper kubelet address types
- Verifies metrics collection with retries
- Tests node and pod metrics
- Provides detailed status and troubleshooting info

**Key Features:**
- Fixes "readiness probe failed" errors
- Resolves "x509: cannot validate certificate" errors
- Safe for both local and production clusters
- Color-coded output for easy reading
- Comprehensive error handling

#### run-gatling-tests.sh
**File**: `scripts/run-gatling-tests.sh`

- Wrapper script for running Gatling tests
- Supports: scale-up, scale-down, sustained, spike tests
- Validates application accessibility
- Opens test reports automatically

#### deploy-hpa-setup.sh
**File**: `scripts/deploy-hpa-setup.sh`

- Complete deployment automation
- Builds frontend and backend
- Creates Docker image
- Deploys to Kubernetes
- Configures HPA
- Displays deployment status

### 5. Documentation

#### HPA-LOAD-TESTING-GUIDE.md
**File**: `docs/HPA-LOAD-TESTING-GUIDE.md`

Comprehensive guide covering:
- Prerequisites and setup
- Architecture and configuration
- Running load tests
- Monitoring HPA
- Test scenarios with expected results
- Troubleshooting
- Best practices
- Advanced configuration

#### HPA-QUICK-START.md
**File**: `docs/HPA-QUICK-START.md`

5-minute quick start guide with:
- Rapid deployment steps
- Quick test commands
- Expected behavior
- Common troubleshooting

#### METRICS-SERVER-TROUBLESHOOTING.md
**File**: `docs/METRICS-SERVER-TROUBLESHOOTING.md`

Comprehensive metrics-server troubleshooting guide:
- Detailed explanation of TLS certificate issues
- Root cause analysis
- Automatic and manual fix procedures
- Cluster-specific notes (Docker Desktop, Minikube, Kind, etc.)
- Production considerations
- Security implications of TLS bypass
- Verification steps

#### Gatling README
**File**: `src/test/scala/com/paymetv/app/gatling/README.md`

Gatling-specific documentation:
- Simulation descriptions
- Configuration options
- Running tests
- Customization guide

### 6. Maven Configuration

**File**: `pom.xml` (updated)

Added dependencies and plugins:
- Gatling Highcharts (v3.11.5)
- Gatling App
- Scala Maven Plugin (v4.9.2)
- Gatling Maven Plugin (v4.9.6)

## File Structure

```
paymetv-app/
├── src/
│   ├── main/
│   │   ├── java/com/paymetv/app/
│   │   │   └── controller/
│   │   │       └── LoadTestController.java          # Load test endpoints
│   │   └── resources/
│   │       └── k8s/
│   │           ├── deployment-with-hpa.yaml         # Deployment manifest
│   │           ├── hpa.yaml                         # HPA manifest
│   │           └── service.yaml                     # Service manifest
│   └── test/
│       └── scala/com/paymetv/app/gatling/
│           ├── HPAScaleUpSimulation.scala           # Scale-up test
│           ├── HPAScaleDownSimulation.scala         # Scale-down test
│           ├── HPASustainedLoadSimulation.scala     # Sustained load test
│           ├── HPASpikeTestSimulation.scala         # Spike test
│           └── README.md                            # Gatling docs
├── scripts/
│   ├── monitor-hpa.sh                               # HPA monitoring
│   ├── verify-metrics-server.sh                     # Metrics verification
│   ├── run-gatling-tests.sh                         # Test runner
│   └── deploy-hpa-setup.sh                          # Deployment automation
├── docs/
│   ├── HPA-LOAD-TESTING-GUIDE.md                    # Comprehensive guide
│   ├── HPA-QUICK-START.md                           # Quick start
│   ├── HPA-IMPLEMENTATION-SUMMARY.md                # This file
│   └── METRICS-SERVER-TROUBLESHOOTING.md            # Metrics server fixes
└── pom.xml                                          # Updated with Gatling
```

## Quick Start

```bash
# 1. Deploy application with HPA
./scripts/deploy-hpa-setup.sh default

# 2. Start monitoring (in separate terminal)
./scripts/monitor-hpa.sh default 5

# 3. Get service URL
export SERVICE_URL=http://$(kubectl get svc paymetv-app-service -n default -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

# 4. Run load test
./scripts/run-gatling-tests.sh scale-up ${SERVICE_URL}

# 5. View report
open target/gatling/$(ls -t target/gatling/ | head -1)/index.html
```

## Key Features

✅ **Production-Ready HPA Configuration** - Optimized scaling policies
✅ **Comprehensive Load Tests** - 4 different test scenarios
✅ **Real-Time Monitoring** - Visual HPA status with logging
✅ **Automated Deployment** - One-command setup
✅ **Detailed Documentation** - Quick start and comprehensive guides
✅ **CPU-Intensive Endpoints** - Realistic load generation
✅ **Metrics Verification** - Automated metrics-server setup
✅ **Automatic TLS Fix** - Detects and fixes certificate issues in local clusters
✅ **Test Reports** - Beautiful Gatling HTML reports
✅ **Cluster Type Detection** - Smart configuration for local vs cloud clusters

## Testing Workflow

1. **Deploy** → `./scripts/deploy-hpa-setup.sh`
2. **Monitor** → `./scripts/monitor-hpa.sh`
3. **Test** → `./scripts/run-gatling-tests.sh [type] [url]`
4. **Analyze** → Review Gatling reports and HPA logs
5. **Iterate** → Adjust HPA configuration based on results

## Next Steps

1. Review the [Quick Start Guide](./HPA-QUICK-START.md)
2. Read the [Comprehensive Guide](./HPA-LOAD-TESTING-GUIDE.md)
3. Deploy and run your first test
4. Fine-tune HPA parameters based on results
5. Integrate into CI/CD pipeline

## Support

For issues or questions:
1. Check [Troubleshooting](./HPA-LOAD-TESTING-GUIDE.md#troubleshooting) section
2. Review Gatling reports for performance insights
3. Check HPA events: `kubectl describe hpa paymetv-app-hpa -n default`
4. Review application logs: `kubectl logs -n default -l app=paymetv-app`

