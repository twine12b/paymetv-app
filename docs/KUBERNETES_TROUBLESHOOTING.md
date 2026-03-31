# Kubernetes Pod Startup Troubleshooting Guide

## Issue: Liveness Probe Failures

### Symptom
```
Liveness probe failed: Get "http://10.244.0.20:80/actuator/health/liveness": dial tcp 10.244.0.20:80: connect: connection refused
```

Pods fail to start and show status `CrashLoopBackOff` or `Running` but not `Ready`.

---

## Root Causes & Solutions

### ✅ FIXED: Actuator Endpoints Not Configured

**Problem:** Spring Boot Actuator health endpoints were not exposed in `application.properties`.

**Solution:** Added the following configuration to `src/main/resources/application.properties`:

```properties
# Spring Boot Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.probes.enabled=true
management.endpoint.health.show-details=always
management.health.livenessState.enabled=true
management.health.readinessState.enabled=true
management.endpoints.web.base-path=/actuator
management.health.db.enabled=true
management.health.diskspace.enabled=true
```

### ✅ FIXED: Insufficient Startup Time

**Problem:** Initial delay was too short (30s) for Spring Boot application to fully start.

**Solution:** Increased delays in Kubernetes manifests:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 80
  initialDelaySeconds: 90  # Increased from 30 to 90
  periodSeconds: 10
  timeoutSeconds: 5
  failureThreshold: 3

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 80
  initialDelaySeconds: 60  # Increased from 20 to 60
  periodSeconds: 5
  timeoutSeconds: 3
  failureThreshold: 3
```

---

## Testing & Verification

### 1. Test Locally (Before Deploying to K8s)

```bash
# Start the application locally
mvn spring-boot:run

# Test health endpoints
./scripts/test_health_endpoints.sh localhost 80

# Or manually test each endpoint
curl http://localhost:80/actuator/health
curl http://localhost:80/actuator/health/liveness
curl http://localhost:80/actuator/health/readiness
```

**Expected Response:**
```json
{
  "status": "UP",
  "groups": ["liveness","readiness"]
}
```

### 2. Test in Docker Container

```bash
# Build and run Docker container
docker build -t paymetv-app:latest .
docker run -d -p 80:80 --name paymetv-test paymetv-app:latest

# Wait for startup (90 seconds recommended)
sleep 90

# Test endpoints
./scripts/test_health_endpoints.sh localhost 80

# Check logs
docker logs paymetv-test

# Clean up
docker stop paymetv-test
docker rm paymetv-test
```

### 3. Deploy to Kubernetes

```bash
# Deploy the application
cd scripts
./paymetv_start_all.sh dev

# Monitor pod status
kubectl get pods -w

# Check pod details
kubectl describe pod <pod-name>

# View pod logs
kubectl logs <pod-name>

# Test endpoint from inside pod
kubectl exec -it <pod-name> -- curl http://localhost:80/actuator/health/liveness
```

---

## Debugging Commands

### Check Pod Status
```bash
kubectl get pods
kubectl get pods -o wide
```

### View Pod Events
```bash
kubectl describe pod <pod-name>
```

### Check Pod Logs
```bash
# Recent logs
kubectl logs <pod-name>

# Follow logs
kubectl logs -f <pod-name>

# Previous container logs (if crashed)
kubectl logs <pod-name> --previous
```

### Test Endpoint from Inside Pod
```bash
kubectl exec -it <pod-name> -- curl -v http://localhost:80/actuator/health/liveness
```

### Port Forward for Local Testing
```bash
kubectl port-forward <pod-name> 8080:80
curl http://localhost:8080/actuator/health/liveness
```

---

## Common Issues & Solutions

### Issue: Port 80 Requires Root Privileges

**Solution:** Use port 8080 instead:

1. Update `application.properties`:
   ```properties
   server.port=8080
   ```

2. Update Kubernetes manifests (both `dep-before.yaml` and `dep-after.yaml`):
   ```yaml
   containerPort: 8080
   ```
   ```yaml
   livenessProbe:
     httpGet:
       port: 8080
   readinessProbe:
     httpGet:
       port: 8080
   ```

3. Update Service:
   ```yaml
   ports:
     - port: 80
       targetPort: 8080
   ```

### Issue: Database Connection Failures

**Symptom:** Readiness probe fails but liveness passes.

**Solution:** Ensure MySQL is running and accessible:
```bash
kubectl get pods -n database
kubectl logs <mysql-pod-name> -n database
```

Update connection string in `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://mysql-service.database.svc.cluster.local:3306/paymetv_db
```

---

## Expected Pod Status Timeline

| Time | Pod Status | Ready | Reason |
|------|------------|-------|--------|
| 0s | Pending | 0/1 | Scheduling |
| 5s | ContainerCreating | 0/1 | Pulling image |
| 15s | Running | 0/1 | Waiting for readiness (60s delay) |
| 60s | Running | 0/1 | Readiness probe starting |
| 75s | Running | 1/1 | ✅ Ready to serve traffic |


