# Metrics Server Troubleshooting Guide

## Common Issue: Readiness Probe Failed

### Symptoms

```bash
kubectl get pods -n kube-system -l k8s-app=metrics-server
# Output shows: 0/1 Running (not ready)

kubectl describe pod -n kube-system -l k8s-app=metrics-server
# Shows: Warning  Unhealthy  Readiness probe failed: HTTP probe failed with statuscode: 500
```

### Root Cause

The most common cause is **TLS certificate verification failure** in local Kubernetes clusters (Docker Desktop, Minikube, Kind, k3s).

**Error in logs:**
```
E1219 18:10:08.999611       1 scraper.go:149] "Failed to scrape node" 
err="Get \"https://172.18.0.4:10250/metrics/resource\": 
tls: failed to verify certificate: x509: cannot validate certificate for 172.18.0.4 
because it doesn't contain any IP SANs" node="desktop-control-plane"
```

**Why this happens:**
- Local Kubernetes clusters often use self-signed certificates for kubelets
- These certificates may not include IP Subject Alternative Names (SANs)
- The metrics-server, by default, strictly validates TLS certificates
- Without proper SANs, TLS verification fails
- Metrics-server cannot scrape kubelet metrics
- Readiness probe fails because no metrics are available

## Automatic Fix

Our `verify-metrics-server.sh` script **automatically detects and fixes** this issue:

```bash
./scripts/verify-metrics-server.sh
```

**What it does:**

1. **Detects cluster type** (local vs cloud)
2. **Checks for TLS errors** in metrics-server logs
3. **Applies fix for local clusters**:
   - Adds `--kubelet-insecure-tls` flag (skips TLS verification)
   - Adds `--kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname`
4. **Waits for rollout** and verifies metrics are working
5. **Tests metrics API** with `kubectl top nodes` and `kubectl top pods`

## Manual Fix

If you need to fix it manually:

### Option 1: Patch Existing Deployment

```bash
# Add the --kubelet-insecure-tls flag
kubectl patch deployment metrics-server -n kube-system --type='json' -p='[
  {
    "op": "add",
    "path": "/spec/template/spec/containers/0/args/-",
    "value": "--kubelet-insecure-tls"
  }
]'

# Wait for rollout to complete
kubectl rollout status deployment/metrics-server -n kube-system

# Verify it's working
kubectl top nodes
```

### Option 2: Install with Flags from Start

```bash
# Download the manifest
curl -sL https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml -o metrics-server.yaml

# Add the flags (macOS/Linux compatible)
sed -i.bak '/- --metric-resolution=15s/a\
        - --kubelet-insecure-tls\
        - --kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname
' metrics-server.yaml

# Apply the modified manifest
kubectl apply -f metrics-server.yaml

# Clean up
rm metrics-server.yaml metrics-server.yaml.bak
```

### Option 3: Using kubectl edit

```bash
kubectl edit deployment metrics-server -n kube-system
```

Add these lines under `spec.template.spec.containers[0].args`:
```yaml
- --kubelet-insecure-tls
- --kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname
```

## Understanding the Flags

### `--kubelet-insecure-tls`

**What it does:**
- Skips TLS certificate verification when connecting to kubelets
- Allows metrics-server to connect even with invalid/self-signed certificates

**When to use:**
- ✅ Local development clusters (Docker Desktop, Minikube, Kind, k3s)
- ✅ Testing environments
- ❌ **NEVER in production clusters**

**Security implications:**
- In local dev: Safe (single-user, isolated environment)
- In production: **Dangerous** (vulnerable to man-in-the-middle attacks)

### `--kubelet-preferred-address-types`

**What it does:**
- Specifies the order of address types to use when connecting to kubelets
- `InternalIP,ExternalIP,Hostname` tries internal IP first

**Why it's needed:**
- Some clusters have multiple network interfaces
- Ensures metrics-server uses the correct address to reach kubelets
- Prevents connection failures due to unreachable addresses

## Verification Steps

After applying the fix:

### 1. Check Pod Status
```bash
kubectl get pods -n kube-system -l k8s-app=metrics-server
# Should show: 1/1 Running
```

### 2. Check Logs (Should be clean)
```bash
kubectl logs -n kube-system -l k8s-app=metrics-server --tail=20
# Should NOT show TLS errors
```

### 3. Test Node Metrics
```bash
kubectl top nodes
# Should show CPU and memory usage
```

### 4. Test Pod Metrics
```bash
kubectl top pods -n kube-system
# Should show metrics for all pods
```

### 5. Verify HPA Can Read Metrics
```bash
kubectl get hpa -A
# CPU column should show percentages, not <unknown>
```

## Cluster-Specific Notes

### Docker Desktop
- **Issue**: Very common
- **Fix**: Automatic with our script
- **Reason**: Uses self-signed certificates without IP SANs

### Minikube
- **Issue**: Common
- **Fix**: Automatic with our script
- **Alternative**: `minikube addons enable metrics-server` (pre-configured)

### Kind (Kubernetes in Docker)
- **Issue**: Very common
- **Fix**: Automatic with our script
- **Reason**: Similar to Docker Desktop

### k3s/k3d
- **Issue**: Sometimes occurs
- **Fix**: Automatic with our script
- **Note**: k3s has its own metrics-server that may work better

### Cloud Clusters (GKE, EKS, AKS)
- **Issue**: Rare (proper certificates usually in place)
- **Fix**: Our script detects cloud clusters and doesn't apply TLS bypass
- **If issues occur**: Check cloud provider documentation for metrics-server setup

## Production Considerations

**For production clusters, DO NOT use `--kubelet-insecure-tls`**

Instead, ensure proper TLS certificates:

1. **Use proper CA-signed certificates** for kubelets
2. **Include IP SANs** in kubelet certificates
3. **Configure certificate rotation** (kubelet auto-rotation)
4. **Use cloud provider's managed metrics-server** when available

Example for proper certificate configuration:
```yaml
# kubelet-config.yaml
apiVersion: kubelet.config.k8s.io/v1beta1
kind: KubeletConfiguration
serverTLSBootstrap: true
rotateCertificates: true
```

## Still Having Issues?

### Check Deployment Configuration
```bash
kubectl get deployment metrics-server -n kube-system -o yaml
```

Look for the args section - should include:
```yaml
args:
  - --cert-dir=/tmp
  - --secure-port=10250
  - --kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname
  - --kubelet-use-node-status-port
  - --metric-resolution=15s
  - --kubelet-insecure-tls  # For local clusters only
```

### Check RBAC Permissions
```bash
kubectl get clusterrole system:metrics-server
kubectl get clusterrolebinding system:metrics-server
```

### Check Network Connectivity
```bash
# Get metrics-server pod name
POD=$(kubectl get pod -n kube-system -l k8s-app=metrics-server -o jsonpath='{.items[0].metadata.name}')

# Check if it can reach kubelet
kubectl exec -n kube-system $POD -- wget -O- http://$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[0].address}'):10255/metrics
```

### Restart metrics-server
```bash
kubectl rollout restart deployment/metrics-server -n kube-system
kubectl rollout status deployment/metrics-server -n kube-system
```

## References

- [Kubernetes Metrics Server](https://github.com/kubernetes-sigs/metrics-server)
- [Metrics Server Configuration](https://github.com/kubernetes-sigs/metrics-server#configuration)
- [HPA Documentation](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)

