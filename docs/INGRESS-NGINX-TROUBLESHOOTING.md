# Ingress NGINX Admission Webhook Troubleshooting Guide

## Overview

This guide addresses the common issue where the ingress-nginx controller pod fails to start with the error:

```
MountVolume.SetUp failed for volume 'webhook-cert' : secret 'ingress-nginx-admission' not found
```

## Root Cause

The ingress-nginx controller requires an admission webhook certificate stored in a Kubernetes Secret named `ingress-nginx-admission`. This secret is created by a Kubernetes Job (`ingress-nginx-admission-create`) that runs during deployment.

**The Problem**: When all manifests are applied simultaneously, the controller Deployment tries to start before the admission webhook Job completes, causing the controller pod to fail because the required secret doesn't exist yet.

## Solution Implemented

The `setup_cert_manager.sh` script has been enhanced to properly sequence the ingress-nginx deployment:

### Deployment Sequence

1. **Apply all manifests** (including Job and Deployment)
2. **Wait for admission webhook Job to complete**
3. **Verify the secret was created**
4. **Wait for controller pod to be ready**

### Key Features

✅ **Job Completion Wait** - Script waits up to 2 minutes for the admission webhook job to complete
✅ **Secret Verification** - Confirms the `ingress-nginx-admission` secret exists before proceeding
✅ **Error Detection** - Detects and reports job failures with detailed logs
✅ **Pod Status Monitoring** - Checks for pod errors (CrashLoopBackOff, Error states)
✅ **Comprehensive Logging** - Provides detailed output for troubleshooting

## Fixed Setup Script

The updated `setup_cert_manager.sh` includes:

```bash
# Step 6: Wait for admission webhook job to complete and secret to be created
echo "Step 6: Waiting for ingress-nginx admission webhook setup..."

# Wait for the admission-create job to complete
until kubectl -n ${namespace} wait --for=condition=complete --timeout=10s job/ingress-nginx-admission-create 2>/dev/null; do
    echo "Still waiting for admission webhook job..."
    sleep 5
done
echo "✓ Admission webhook job completed"

# Verify the secret was created
if ! kubectl -n ${namespace} get secret ingress-nginx-admission &>/dev/null; then
    echo "✗ Admission webhook secret not found!"
    exit 1
fi
echo "✓ Admission webhook secret created"

# Step 7: Wait for controller pod to be ready
echo "Step 7: Waiting for ingress-nginx controller pod to be ready..."
until kubectl -n ${namespace} get pods --no-headers | grep controller | awk '{print $1}' | \
  xargs -I{} kubectl -n ${namespace} get pod {} -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' | grep -q True; do
  sleep 5
  echo "Still waiting for controller pod..."
done
echo "✓ Ingress-nginx controller pod is ready"
```

## Troubleshooting Tools

### 1. Troubleshooting Script

Run the diagnostic script to check all ingress-nginx components:

```bash
cd src/main/resources/conf
./troubleshoot-ingress-nginx.sh
```

**What it checks**:
- ✅ Admission webhook job status
- ✅ Admission webhook secret existence
- ✅ Controller deployment status
- ✅ Controller pod status
- ✅ Admission webhook service
- ✅ ValidatingWebhookConfiguration

### 2. Cleanup Script

If you need to start fresh, use the cleanup script:

```bash
cd src/main/resources/conf
./cleanup-ingress-nginx.sh
```

**What it removes**:
- Controller deployment
- Admission webhook jobs
- Admission webhook secret
- Services
- ValidatingWebhookConfiguration
- IngressClass
- RBAC resources

After cleanup, re-run the setup script:

```bash
./setup_cert_manager.sh
```

## Manual Troubleshooting

### Check Admission Webhook Job Status

```bash
# Check if job exists
kubectl -n default get jobs

# Check job status
kubectl -n default describe job ingress-nginx-admission-create

# Check job pod logs
kubectl -n default logs -l job-name=ingress-nginx-admission-create
```

**Expected output**:
```
NAME                               COMPLETIONS   DURATION   AGE
ingress-nginx-admission-create     1/1           5s         2m
```

### Check Admission Webhook Secret

```bash
# Check if secret exists
kubectl -n default get secret ingress-nginx-admission

# View secret details
kubectl -n default describe secret ingress-nginx-admission
```

**Expected output**:
```
NAME                       TYPE                DATA   AGE
ingress-nginx-admission    Opaque              3      2m
```

### Check Controller Pod Status

```bash
# Get controller pod
kubectl -n default get pods -l app.kubernetes.io/component=controller

# Describe pod for events
kubectl -n default describe pod <controller-pod-name>

# Check pod logs
kubectl -n default logs <controller-pod-name>
```

### Check for Mount Errors

```bash
# Look for volume mount errors in pod events
kubectl -n default get events --sort-by='.lastTimestamp' | grep -i "mount\|volume"
```

## Common Issues and Solutions

### Issue 1: Job Never Completes

**Symptoms**:
- Job shows 0/1 completions
- Job pod is in Error or CrashLoopBackOff state

**Diagnosis**:
```bash
kubectl -n default logs -l job-name=ingress-nginx-admission-create
```

**Common causes**:
- Network issues preventing certificate generation
- RBAC permissions missing
- Service account issues

**Solution**:
```bash
# Delete and recreate the job
kubectl -n default delete job ingress-nginx-admission-create
kubectl -n default apply -f deploy.yaml
```

### Issue 2: Secret Not Created

**Symptoms**:
- Job completes but secret doesn't exist
- Controller pod fails with "secret not found"

**Diagnosis**:
```bash
kubectl -n default get secrets | grep ingress-nginx
kubectl -n default logs -l job-name=ingress-nginx-admission-create
```

**Solution**:
```bash
# Check job logs for errors
kubectl -n default logs -l job-name=ingress-nginx-admission-create

# Manually create secret if needed (temporary workaround)
# Better: Fix the job and re-run
kubectl -n default delete job ingress-nginx-admission-create
kubectl -n default apply -f deploy.yaml
```

### Issue 3: Controller Pod in CrashLoopBackOff

**Symptoms**:
- Secret exists but pod still fails
- Pod restarts repeatedly

**Diagnosis**:
```bash
kubectl -n default describe pod <controller-pod-name>
kubectl -n default logs <controller-pod-name> --previous
```

**Common causes**:
- Invalid certificate in secret
- Port conflicts
- Resource constraints

**Solution**:
```bash
# Delete secret and job, recreate
kubectl -n default delete secret ingress-nginx-admission
kubectl -n default delete job ingress-nginx-admission-create
kubectl -n default apply -f deploy.yaml

# Wait for job to complete
kubectl -n default wait --for=condition=complete --timeout=120s job/ingress-nginx-admission-create
```

### Issue 4: Timeout Waiting for Job

**Symptoms**:
- Setup script times out waiting for job
- Job takes longer than 2 minutes

**Diagnosis**:
```bash
kubectl -n default get jobs
kubectl -n default get pods -l job-name=ingress-nginx-admission-create
kubectl -n default describe pod <job-pod-name>
```

**Solution**:
```bash
# Check if job pod is running
kubectl -n default get pods -l job-name=ingress-nginx-admission-create

# If pod is pending, check for resource issues
kubectl -n default describe pod <job-pod-name>

# If pod is running but slow, wait longer or check logs
kubectl -n default logs -l job-name=ingress-nginx-admission-create -f
```

## Verification Steps

After deployment, verify everything is working:

### 1. Check All Components

```bash
# Check job completed
kubectl -n default get jobs
# Expected: ingress-nginx-admission-create   1/1

# Check secret exists
kubectl -n default get secret ingress-nginx-admission
# Expected: ingress-nginx-admission   Opaque   3

# Check controller running
kubectl -n default get pods -l app.kubernetes.io/component=controller
# Expected: ingress-nginx-controller-xxx   1/1   Running

# Check services
kubectl -n default get svc -l app.kubernetes.io/name=ingress-nginx
# Expected: ingress-nginx-controller and ingress-nginx-controller-admission
```

### 2. Test Ingress Functionality

```bash
# Check IngressClass
kubectl get ingressclass
# Expected: nginx

# Check ValidatingWebhookConfiguration
kubectl get validatingwebhookconfiguration ingress-nginx-admission
# Expected: ingress-nginx-admission

# Test with a sample ingress
kubectl apply -f - <<EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: test-ingress
  namespace: default
spec:
  ingressClassName: nginx
  rules:
  - host: test.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: test-service
            port:
              number: 80
EOF

# Check ingress was created
kubectl -n default get ingress test-ingress

# Clean up test
kubectl -n default delete ingress test-ingress
```

## Best Practices

### 1. Always Use the Setup Script

The `setup_cert_manager.sh` script handles the proper sequencing automatically. Don't apply manifests manually unless troubleshooting.

### 2. Monitor Job Completion

If deploying manually, always wait for the job to complete:

```bash
kubectl -n default wait --for=condition=complete --timeout=120s job/ingress-nginx-admission-create
```

### 3. Check Logs on Failure

Always check job logs if the admission webhook setup fails:

```bash
kubectl -n default logs -l job-name=ingress-nginx-admission-create
```

### 4. Clean Up Before Retry

If deployment fails, use the cleanup script before retrying:

```bash
./cleanup-ingress-nginx.sh
./setup_cert_manager.sh
```

### 5. Verify Secret Before Controller

Never deploy the controller without verifying the secret exists:

```bash
kubectl -n default get secret ingress-nginx-admission
```

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Ingress NGINX Deployment                  │
└─────────────────────────────────────────────────────────────┘

Step 1: Apply Manifests
├── ServiceAccount (ingress-nginx-admission)
├── RBAC (Roles, RoleBindings, ClusterRoles, ClusterRoleBindings)
├── Job (ingress-nginx-admission-create) ──┐
├── Deployment (ingress-nginx-controller)  │
├── Services                               │
└── ValidatingWebhookConfiguration         │
                                           │
Step 2: Job Execution                      │
┌──────────────────────────────────────────┘
│
├── Job Pod Starts
├── Generates TLS Certificate
├── Creates Secret (ingress-nginx-admission)
│   ├── ca.crt
│   ├── tls.crt
│   └── tls.key
└── Job Completes ──┐
                    │
Step 3: Controller Starts
┌───────────────────┘
│
├── Controller Pod Starts
├── Mounts Secret (webhook-cert volume)
├── Starts Admission Webhook Server
└── Pod Ready ──┐
                │
Step 4: System Ready
┌───────────────┘
│
├── Ingress Controller Running
├── Admission Webhook Active
└── Ready to Handle Ingress Resources
```

## Quick Reference

### Useful Commands

```bash
# Check admission webhook job
kubectl -n default get job ingress-nginx-admission-create

# Check admission webhook secret
kubectl -n default get secret ingress-nginx-admission

# Check controller pod
kubectl -n default get pods -l app.kubernetes.io/component=controller

# View controller logs
kubectl -n default logs -l app.kubernetes.io/component=controller

# Restart controller
kubectl -n default rollout restart deployment ingress-nginx-controller

# Delete and recreate admission webhook
kubectl -n default delete job ingress-nginx-admission-create
kubectl -n default delete secret ingress-nginx-admission
kubectl -n default apply -f deploy.yaml
```

### Script Locations

- **Setup Script**: `src/main/resources/conf/setup_cert_manager.sh`
- **Troubleshooting Script**: `src/main/resources/conf/troubleshoot-ingress-nginx.sh`
- **Cleanup Script**: `src/main/resources/conf/cleanup-ingress-nginx.sh`

### Log Locations

```bash
# Admission webhook job logs
kubectl -n default logs -l job-name=ingress-nginx-admission-create

# Controller logs
kubectl -n default logs -l app.kubernetes.io/component=controller

# All ingress-nginx logs
kubectl -n default logs -l app.kubernetes.io/name=ingress-nginx
```

## Summary

The ingress-nginx admission webhook issue is caused by improper deployment sequencing. The solution is to:

1. ✅ Apply all manifests (including Job and Deployment)
2. ✅ Wait for admission webhook Job to complete
3. ✅ Verify the secret was created
4. ✅ Wait for controller pod to be ready

The updated `setup_cert_manager.sh` script handles this automatically with proper error checking and detailed logging.

For issues, use the troubleshooting script (`troubleshoot-ingress-nginx.sh`) to diagnose problems, and the cleanup script (`cleanup-ingress-nginx.sh`) to start fresh if needed.

## Additional Resources

- [Ingress NGINX Documentation](https://kubernetes.github.io/ingress-nginx/)
- [Admission Webhooks](https://kubernetes.io/docs/reference/access-authn-authz/admission-controllers/)
- [Kubernetes Jobs](https://kubernetes.io/docs/concepts/workloads/controllers/job/)
- [Kubernetes Secrets](https://kubernetes.io/docs/concepts/configuration/secret/)


