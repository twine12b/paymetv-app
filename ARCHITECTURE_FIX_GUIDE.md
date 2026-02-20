# Architecture Mismatch Fix Guide

## Problem Summary
The Docker image `paymetv/paymetv-app:latest` was built on Windows AMD64, but the Kubernetes deployment files specified `nodeSelector: kubernetes.io/arch: arm64`, causing image pull failures.

## Solutions Implemented

### ✅ QUICK FIX (Option 2) - ALREADY APPLIED
**Status: COMPLETE** - The deployment files have been updated to remove the ARM64 nodeSelector.

**Changes Made:**
- Removed `nodeSelector: kubernetes.io/arch: arm64` from `src/main/resources/conf/dep-before.yaml`
- Removed `nodeSelector: kubernetes.io/arch: arm64` from `src/main/resources/conf/dep-after.yaml`
- Added `imagePullPolicy: IfNotPresent` to both files

**What this means:**
- Your existing AMD64 image will now work on your cluster
- Kubernetes will schedule pods on any available node (not restricted to ARM64)
- No need to rebuild the Docker image

**Next Steps:**
1. Re-run your setup script:
   ```bash
   ./src/main/resources/conf/setup_cert_manager.sh dev
   ```

2. Verify the deployment:
   ```bash
   kubectl get pods -l app=paymetv-app
   kubectl describe pod -l app=paymetv-app
   ```

---

### 🚀 RECOMMENDED SOLUTION (Option 1) - Multi-Architecture Build
**Status: READY TO USE** - A new build script has been created for future use.

**Why use this approach:**
- Supports both AMD64 and ARM64 architectures
- More flexible for different deployment environments
- Future-proof solution
- Can restore the ARM64 nodeSelector if needed

**Prerequisites:**
1. Docker Desktop with buildx support (already included in modern Docker)
2. Docker Hub account with push access to `paymetv/paymetv-app`

**How to Build Multi-Architecture Image:**

1. **Login to Docker Hub:**
   ```bash
   docker login
   ```

2. **Run the multi-architecture build script:**
   ```bash
   ./build-and-push-multiarch.sh
   ```

   This script will:
   - Build the frontend (npm run build)
   - Build the backend (mvn clean install)
   - Create a Docker buildx builder
   - Build images for both linux/amd64 and linux/arm64
   - Push the multi-arch image to Docker Hub

3. **Verify the multi-architecture manifest:**
   ```bash
   docker buildx imagetools inspect paymetv/paymetv-app:latest
   ```

   You should see both platforms listed:
   ```
   Name:      docker.io/paymetv/paymetv-app:latest
   MediaType: application/vnd.docker.distribution.manifest.list.v2+json
   Digest:    sha256:...
   
   Manifests:
     Name:      docker.io/paymetv/paymetv-app:latest@sha256:...
     MediaType: application/vnd.docker.distribution.manifest.v2+json
     Platform:  linux/amd64
     
     Name:      docker.io/paymetv/paymetv-app:latest@sha256:...
     MediaType: application/vnd.docker.distribution.manifest.v2+json
     Platform:  linux/arm64
   ```

4. **Deploy to Kubernetes:**
   ```bash
   ./src/main/resources/conf/setup_cert_manager.sh dev
   ```

---

## Comparison of Solutions

| Aspect | Option 2 (Quick Fix) | Option 1 (Multi-Arch) |
|--------|---------------------|----------------------|
| **Implementation** | ✅ Already done | Ready to use |
| **Build Time** | No rebuild needed | Longer build time |
| **Flexibility** | Works on current cluster | Works on any architecture |
| **Future-proof** | May need changes later | Fully portable |
| **Complexity** | Simple | Moderate |
| **Recommended for** | Immediate fix | Production deployments |

---

## Current Deployment File Status

Both `dep-before.yaml` and `dep-after.yaml` now have:
```yaml
spec:
  containers:
    - name: paymetv-app
      image: paymetv/paymetv-app:latest
      imagePullPolicy: IfNotPresent
```

The ARM64 nodeSelector has been removed, allowing the AMD64 image to run on your cluster.

---

## Troubleshooting

### If you still see image pull errors:

1. **Check if the image exists:**
   ```bash
   docker pull paymetv/paymetv-app:latest
   ```

2. **Verify your cluster architecture:**
   ```bash
   kubectl get nodes -o wide
   ```

3. **Check pod events:**
   ```bash
   kubectl describe pod -l app=paymetv-app
   ```

4. **If the image is private, add imagePullSecrets:**
   ```bash
   kubectl create secret docker-registry regcred \
     --docker-server=https://index.docker.io/v1/ \
     --docker-username=<your-username> \
     --docker-password=<your-password> \
     --docker-email=<your-email>
   ```

   Then add to deployment:
   ```yaml
   spec:
     imagePullSecrets:
       - name: regcred
   ```

---

## Recommendation

**For immediate deployment:** Use the Quick Fix (Option 2) - already applied ✅

**For production/long-term:** Build and push the multi-architecture image using `./build-and-push-multiarch.sh`

