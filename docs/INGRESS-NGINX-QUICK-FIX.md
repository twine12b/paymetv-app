# Ingress NGINX Admission Webhook - Quick Fix Guide

## The Problem

**Error**: `MountVolume.SetUp failed for volume 'webhook-cert' : secret 'ingress-nginx-admission' not found`

**Cause**: The ingress-nginx controller tries to start before the admission webhook job creates the required secret.

## The Solution

Use the updated `setup_cert_manager.sh` script which properly sequences the deployment.

## Quick Fix Steps

### Option 1: Use the Fixed Setup Script (Recommended)

```bash
cd src/main/resources/conf
./setup_cert_manager.sh
```

The script now:
1. ✅ Applies all manifests
2. ✅ Waits for admission webhook job to complete
3. ✅ Verifies the secret was created
4. ✅ Waits for controller pod to be ready

### Option 2: Manual Fix

If you already have a failed deployment:

```bash
# 1. Clean up the failed deployment
cd src/main/resources/conf
./cleanup-ingress-nginx.sh

# 2. Re-run the setup script
./setup_cert_manager.sh
```

### Option 3: Diagnose First

If you want to understand what's wrong:

```bash
cd src/main/resources/conf
./troubleshoot-ingress-nginx.sh
```

This will check:
- Admission webhook job status
- Secret existence
- Controller deployment status
- Pod status and logs

## Verification

After running the setup script, verify everything is working:

```bash
# Check job completed
kubectl -n default get job ingress-nginx-admission-create
# Expected: 1/1 completions

# Check secret exists
kubectl -n default get secret ingress-nginx-admission
# Expected: secret found

# Check controller running
kubectl -n default get pods -l app.kubernetes.io/component=controller
# Expected: 1/1 Running
```

## What Changed in the Setup Script

### Before (Broken)

```bash
# Applied everything at once
kubectl -n default apply -f deploy.yaml

# Immediately waited for controller (which couldn't start)
until kubectl get pods | grep controller | grep Running; do
  sleep 5
done
```

**Problem**: Controller tries to start before secret exists.

### After (Fixed)

```bash
# Apply manifests
kubectl -n default apply -f deploy.yaml

# Wait for admission webhook job to complete
until kubectl wait --for=condition=complete job/ingress-nginx-admission-create; do
  sleep 5
done

# Verify secret was created
kubectl get secret ingress-nginx-admission

# Now wait for controller
until kubectl get pods | grep controller | grep Running; do
  sleep 5
done
```

**Solution**: Ensures secret exists before controller starts.

## Common Scenarios

### Scenario 1: Fresh Deployment

```bash
cd src/main/resources/conf
./setup_cert_manager.sh
```

**Expected**: Everything deploys successfully in sequence.

### Scenario 2: Failed Deployment

```bash
# Clean up
cd src/main/resources/conf
./cleanup-ingress-nginx.sh

# Re-deploy
./setup_cert_manager.sh
```

**Expected**: Clean slate, successful deployment.

### Scenario 3: Stuck Job

If the admission webhook job is stuck:

```bash
# Check job status
kubectl -n default describe job ingress-nginx-admission-create

# Check job pod logs
kubectl -n default logs -l job-name=ingress-nginx-admission-create

# If needed, delete and recreate
kubectl -n default delete job ingress-nginx-admission-create
kubectl -n default apply -f deploy.yaml
```

## Troubleshooting Commands

```bash
# Check admission webhook job
kubectl -n default get job ingress-nginx-admission-create

# Check admission webhook secret
kubectl -n default get secret ingress-nginx-admission

# Check controller pod
kubectl -n default get pods -l app.kubernetes.io/component=controller

# View controller logs
kubectl -n default logs -l app.kubernetes.io/component=controller

# Check pod events
kubectl -n default get events --sort-by='.lastTimestamp' | grep -i "mount\|volume"
```

## Scripts Available

| Script | Purpose | Usage |
|--------|---------|-------|
| `setup_cert_manager.sh` | Deploy everything with proper sequencing | `./setup_cert_manager.sh` |
| `troubleshoot-ingress-nginx.sh` | Diagnose ingress-nginx issues | `./troubleshoot-ingress-nginx.sh` |
| `cleanup-ingress-nginx.sh` | Remove all ingress-nginx components | `./cleanup-ingress-nginx.sh` |

## Summary

✅ **Root Cause**: Deployment sequencing issue
✅ **Solution**: Updated setup script with proper wait logic
✅ **Prevention**: Always use the setup script, don't apply manifests manually
✅ **Recovery**: Use cleanup script then re-run setup script

For detailed information, see [INGRESS-NGINX-TROUBLESHOOTING.md](./INGRESS-NGINX-TROUBLESHOOTING.md)

