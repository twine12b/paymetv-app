# Gatling Load Test Simulations for HPA

This directory contains Gatling load test simulations designed to test Horizontal Pod Autoscaling (HPA) behavior.

## Simulations

### 1. HPAScaleUpSimulation.scala

**Purpose**: Test HPA scale-up behavior

**Load Pattern**:
- Gradually ramps up from 0 to target RPS over 5 minutes
- Sustains load for 3 minutes
- Multiple concurrent scenarios (CPU, Fibonacci, Prime, Health Check)

**Configuration**:
```bash
mvn gatling:test \
  -Dgatling.simulationClass=com.paymetv.app.gatling.HPAScaleUpSimulation \
  -DbaseUrl=http://your-service-url \
  -DtargetRps=100 \
  -DrampDuration=5 \
  -DsustainDuration=3
```

**Expected Outcome**:
- Pods scale from 2 to 6-8 replicas
- CPU stabilizes around 70%

### 2. HPAScaleDownSimulation.scala

**Purpose**: Test HPA scale-down behavior

**Load Pattern**:
- Starts with high load (100 RPS)
- Gradually decreases to 10% over 5 minutes
- Maintains low load for 3 minutes

**Configuration**:
```bash
mvn gatling:test \
  -Dgatling.simulationClass=com.paymetv.app.gatling.HPAScaleDownSimulation \
  -DbaseUrl=http://your-service-url \
  -DinitialRps=100 \
  -DrampDownDuration=5 \
  -DcoolDownDuration=3
```

**Expected Outcome**:
- Pods scale down from 6-8 to 2-3 replicas
- Scale-down occurs after 60s stabilization window

### 3. HPASustainedLoadSimulation.scala

**Purpose**: Test HPA stability under constant load

**Load Pattern**:
- Ramps up to target RPS over 2 minutes
- Maintains constant load for 10 minutes
- Mixed workload (50% CPU, 30% Fibonacci, 15% Prime, 5% Health)

**Configuration**:
```bash
mvn gatling:test \
  -Dgatling.simulationClass=com.paymetv.app.gatling.HPASustainedLoadSimulation \
  -DbaseUrl=http://your-service-url \
  -DtargetRps=80 \
  -DtestDuration=10
```

**Expected Outcome**:
- Replica count remains stable (±1 pod)
- CPU stays within 60-80% range
- No thrashing (rapid scale up/down)

### 4. HPASpikeTestSimulation.scala

**Purpose**: Test HPA response to sudden load spikes

**Load Pattern**:
- Baseline load (20 RPS)
- Sudden spike to 150 RPS
- Returns to baseline
- Second spike
- Two concurrent scenarios

**Configuration**:
```bash
mvn gatling:test \
  -Dgatling.simulationClass=com.paymetv.app.gatling.HPASpikeTestSimulation \
  -DbaseUrl=http://your-service-url \
  -DbaselineRps=20 \
  -DspikeRps=150 \
  -DspikeDuration=2
```

**Expected Outcome**:
- Rapid scale-up within 15-30 seconds
- Graceful scale-down after spike ends
- Handles multiple spikes

## Running Tests

### Using the Helper Script

```bash
# From project root
./scripts/run-gatling-tests.sh [test-type] [base-url]

# Examples
./scripts/run-gatling-tests.sh scale-up http://paymetv.example.com
./scripts/run-gatling-tests.sh scale-down http://paymetv.example.com
./scripts/run-gatling-tests.sh sustained http://paymetv.example.com
./scripts/run-gatling-tests.sh spike http://paymetv.example.com
```

### Using Maven Directly

```bash
# Run specific simulation
mvn gatling:test -Dgatling.simulationClass=com.paymetv.app.gatling.HPAScaleUpSimulation

# Run all simulations
mvn gatling:test
```

## Test Endpoints

The simulations use these application endpoints:

- `GET /api/load/cpu?durationMs=2000&intensity=7` - CPU load generation
- `GET /api/load/fibonacci?n=38` - Fibonacci calculation
- `GET /api/load/prime?limit=500000` - Prime number calculation
- `GET /api/health` - Health check

## Viewing Reports

After each test, Gatling generates an HTML report:

```bash
# Open latest report (macOS)
open target/gatling/$(ls -t target/gatling/ | head -1)/index.html

# Open latest report (Linux)
xdg-open target/gatling/$(ls -t target/gatling/ | head -1)/index.html
```

## Monitoring During Tests

Always monitor HPA while running tests:

```bash
# In a separate terminal
./scripts/monitor-hpa.sh default 5
```

## Customizing Tests

### Adjusting Load Levels

Edit the simulation files to change:
- Request rates (RPS)
- Ramp-up/down durations
- Endpoint parameters (CPU intensity, Fibonacci n, etc.)
- Pause durations between requests

### Adding New Scenarios

Create a new Scala file in this directory:

```scala
package com.paymetv.app.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class MyCustomSimulation extends Simulation {
  val httpProtocol = http
    .baseUrl(System.getProperty("baseUrl", "http://localhost"))
    .acceptHeader("application/json")

  val myScenario = scenario("My Custom Test")
    .exec(
      http("My Request")
        .get("/api/my-endpoint")
        .check(status.is(200))
    )

  setUp(
    myScenario.inject(
      rampUsersPerSec(0).to(50).during(2.minutes)
    )
  ).protocols(httpProtocol)
}
```

## Best Practices

1. **Start with low load** - Understand baseline behavior first
2. **Monitor HPA** - Always watch scaling behavior during tests
3. **Allow stabilization** - Wait between tests for metrics to stabilize
4. **Review reports** - Analyze Gatling reports after each test
5. **Check logs** - Monitor application logs for errors
6. **Adjust gradually** - Fine-tune load parameters incrementally

## Troubleshooting

### Compilation Errors

```bash
# Clean and recompile
mvn clean compile test-compile
```

### Connection Refused

```bash
# Verify service is accessible
curl http://your-service-url/api/health

# Check Kubernetes service
kubectl get svc paymetv-app-service -n default
```

### High Failure Rate

- Reduce target RPS
- Increase ramp-up duration
- Check application logs for errors
- Verify sufficient cluster resources

## References

- [Gatling Documentation](https://gatling.io/docs/gatling/)
- [Gatling Scala DSL](https://gatling.io/docs/gatling/reference/current/core/simulation/)
- [HPA Load Testing Guide](../../../../docs/HPA-LOAD-TESTING-GUIDE.md)

