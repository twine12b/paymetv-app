# MySQL Deployment Documentation

## Overview

This directory contains the Kubernetes configuration for deploying a single-instance MySQL 8.4 database server for the PayMeTV application. The deployment uses a NodePort service to expose MySQL on port 30006, making it accessible from outside the Kubernetes cluster.

**Key Features:**
- MySQL 8.4 official image
- Persistent storage (250Mi PersistentVolume)
- NodePort service for external access
- Secret-based credential management
- Single replica deployment with Recreate strategy

---

## Architecture

### Components

```
┌─────────────────────────────────────────────────────────────┐
│                    MySQL Deployment Architecture             │
└─────────────────────────────────────────────────────────────┘

┌──────────────────┐
│  External Access │
│  (localhost)     │
└────────┬─────────┘
         │
         │ Port 30006 (NodePort)
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│  Kubernetes Service (mysql)                                  │
│  - Type: NodePort                                            │
│  - Port: 3306 (internal)                                     │
│  - NodePort: 30006 (external)                                │
│  - Namespace: database                                       │
└────────┬────────────────────────────────────────────────────┘
         │
         │ Port 3306
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│  Deployment (mysql-deployment)                               │
│  - Replicas: 1                                               │
│  - Strategy: Recreate                                        │
│  - Image: mysql:8.4                                          │
└────────┬────────────────────────────────────────────────────┘
         │
         │ Mounts
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│  PersistentVolumeClaim (mysql-pv-claim)                      │
│  - Storage: 250Mi                                            │
│  - AccessMode: ReadWriteOnce                                 │
└────────┬────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│  PersistentVolume (mysql-pv)                                 │
│  - HostPath: /var/lib/mysql                                  │
│  - Capacity: 250Mi                                           │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  Secrets (created by startup3.sh)                            │
│  - mysql-root-pass: Root password                            │
│  - mysql-user-pass: User credentials (todd/password)         │
│  - mysql-db-url: Database name (paymetv_db1)                 │
└─────────────────────────────────────────────────────────────┘
```

### Resource Definitions

1. **PersistentVolume (mysql-pv)**
   - Provides 250Mi of storage using hostPath
   - Path: `/var/lib/mysql`
   - Access Mode: ReadWriteOnce

2. **PersistentVolumeClaim (mysql-pv-claim)**
   - Requests 250Mi of storage
   - Binds to the PersistentVolume

3. **Service (mysql)**
   - Type: NodePort
   - Internal Port: 3306
   - External NodePort: 30006
   - Selector: `app=mysql`

4. **Deployment (mysql-deployment)**
   - Image: `mysql:8.4`
   - Replicas: 1
   - Strategy: Recreate (ensures clean shutdown/startup)
   - Environment variables from secrets

5. **Secrets** (created by startup3.sh)
   - `mysql-root-pass`: Root password
   - `mysql-user-pass`: Application user credentials
   - `mysql-db-url`: Database name

---

## Deployment Instructions

### Prerequisites

- Kubernetes cluster running (Docker Desktop, Minikube, or similar)
- `kubectl` CLI tool installed and configured
- Sufficient cluster resources (250Mi storage, 1 CPU, 1Gi memory)

### Step-by-Step Deployment

1. **Navigate to the database directory:**
   ```bash
   cd src/main/resources/database
   ```

2. **Run the startup script:**
   ```bash
   bash startup3.sh
   ```

   The script will:
   - Create the `database` namespace
   - Create required secrets (mysql-root-pass, mysql-user-pass, mysql-db-url)
   - Apply the mysql.yaml configuration
   - Deploy MySQL with PersistentVolume, Service, and Deployment

3. **Verify the deployment:**
   ```bash
   # Check if pod is running
   kubectl get pods -n database

   # Check service
   kubectl get service mysql -n database

   # Check PersistentVolume and PersistentVolumeClaim
   kubectl get pv mysql-pv
   kubectl get pvc mysql-pv-claim -n database
   ```

4. **Wait for MySQL to be ready:**
   ```bash
   kubectl wait --for=condition=ready pod -l app=mysql -n database --timeout=120s
   ```

---



**Setup:**
```bash
# Terminal 1: Start port forwarding (keep this running)
kubectl port-forward -n database service/mysql 3306:3306
```

**Connect:**
```bash
# Terminal 2: Connect using MySQL client
mysql -h 127.0.0.1 -P 3306 -u todd -ppassword paymetv_db1

# Or connect as root
mysql -h 127.0.0.1 -P 3306 -u root -pCh1ldren paymetv_db1
```

**Advantages:**
- ✅ Simple and reliable
- ✅ Works on all platforms
- ✅ No firewall issues
- ✅ Standard MySQL port (3306)

**Disadvantages:**
- ⚠️ Requires keeping terminal open
- ⚠️ Connection drops if port-forward terminates

---

### Method 2: NodePort Direct Access (Docker Desktop)

**Best for:** Persistent connections, GUI tools, production-like testing

**Connect:**
```bash
# Direct connection via NodePort
mysql -h 127.0.0.1 -P 30006 -u todd -ppassword paymetv_db1

# Or connect as root
mysql -h 127.0.0.1 -P 30006 -u root -pCh1ldren paymetv_db1
```

**For Docker Desktop on macOS/Windows:**
- Host: `localhost` or `127.0.0.1`
- Port: `30006`

**Advantages:**
- ✅ No port-forwarding needed
- ✅ Persistent connection
- ✅ Better for GUI tools
- ✅ Production-like setup

**Disadvantages:**
- ⚠️ Non-standard port (30006)
- ⚠️ May require firewall configuration on some systems

---

### Method 3: Connection from Within Cluster

**Best for:** Application pods, internal services

**Using Service DNS:**
```bash
# From another pod in the same namespace
mysql -h mysql -P 3306 -u todd -ppassword paymetv_db1

# From a pod in a different namespace
mysql -h mysql.database.svc.cluster.local -P 3306 -u todd -ppassword paymetv_db1
```

**Using kubectl exec:**
```bash
# Connect directly to the MySQL pod
kubectl exec -it deployment/mysql-deployment -n database -- mysql -uroot -pCh1ldren paymetv_db1
```

---

## Connection Strings

### MySQL CLI

#### Port Forwarding:
```bash
mysql -h 127.0.0.1 -P 3306 -u todd -ppassword paymetv_db1
```

#### NodePort:
```bash
mysql -h 127.0.0.1 -P 30006 -u todd -ppassword paymetv_db1
```

#### From within cluster:
```bash
mysql -h mysql.database.svc.cluster.local -P 3306 -u todd -ppassword paymetv_db1
```

---

### JDBC Connection Strings

#### Port Forwarding:
```
jdbc:mysql://localhost:3306/paymetv_db1?useSSL=false&allowPublicKeyRetrieval=true
```

#### NodePort:
```
jdbc:mysql://localhost:30006/paymetv_db1?useSSL=false&allowPublicKeyRetrieval=true
```

#### From within cluster:
```
jdbc:mysql://mysql.database.svc.cluster.local:3306/paymetv_db1?useSSL=false&allowPublicKeyRetrieval=true
```

**Spring Boot application.properties:**
```properties
# Using NodePort (external access)
spring.datasource.url=jdbc:mysql://localhost:30006/paymetv_db1?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=todd
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Using service DNS (from within cluster)
spring.datasource.url=jdbc:mysql://mysql.database.svc.cluster.local:3306/paymetv_db1?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=todd
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

---

### GUI Tools (MySQL Workbench, DBeaver, etc.)

#### Configuration for Port Forwarding:
```
Connection Name: PayMeTV MySQL (Port Forward)
Host: 127.0.0.1
Port: 3306
Database: paymetv_db1
Username: todd
Password: password
```

#### Configuration for NodePort:
```
Connection Name: PayMeTV MySQL (NodePort)
Host: 127.0.0.1
Port: 30006
Database: paymetv_db1
Username: todd
Password: password
```

---

### Standard MySQL URL Format

```
mysql://todd:password@localhost:30006/paymetv_db1
```

Or for root:
```
mysql://root:Ch1ldren@localhost:30006/paymetv_db1
```

---

## Testing Connection

### Quick Test Script

Run the automated test script to verify all connection methods:

```bash
cd src/main/resources/database/conf3
chmod +x test-mysql-connection.sh
./test-mysql-connection.sh
```

The script will test:
- ✅ Pod status
- ✅ Service configuration
- ✅ NodePort connectivity
- ✅ MySQL authentication
- ✅ Database access
- ✅ Port forwarding method

---

### Manual Testing

#### Test 1: Verify Pod is Running
```bash
kubectl get pods -n database -l app=mysql
# Expected: mysql-deployment-xxx   1/1   Running
```

#### Test 2: Verify Service
```bash
kubectl get service mysql -n database
# Expected: NodePort service on port 30006
```

#### Test 3: Test Port Connectivity
```bash
# Test if port 30006 is accessible
nc -zv localhost 30006
# Expected: Connection to localhost port 30006 [tcp/*] succeeded!
```

#### Test 4: Test MySQL Connection
```bash
# Test connection and execute query
mysql -h 127.0.0.1 -P 30006 -u root -pCh1ldren -e "SELECT 'Connection successful!' AS status;"
```

#### Test 5: Verify Database Exists
```bash
mysql -h 127.0.0.1 -P 30006 -u root -pCh1ldren -e "SHOW DATABASES;" | grep paymetv_db1
```

---

## Troubleshooting

### Issue 1: Pod Not Starting

**Symptoms:**
- Pod stuck in `Pending`, `ContainerCreating`, or `CrashLoopBackOff` state

**Diagnosis:**
```bash
# Check pod status
kubectl get pods -n database

# Check pod events
kubectl describe pod -l app=mysql -n database

# Check logs
kubectl logs -n database -l app=mysql --tail=50
```

**Common Causes:**
1. **Secret not found** - Verify secrets exist:
   ```bash
   kubectl get secrets -n database
   ```
   Should show: `mysql-root-pass`, `mysql-user-pass`, `mysql-db-url`

2. **PersistentVolume issues** - Check PV/PVC:
   ```bash
   kubectl get pv mysql-pv
   kubectl get pvc mysql-pv-claim -n database
   ```

3. **Resource constraints** - Check node resources:
   ```bash
   kubectl describe node
   ```

**Solution:**
```bash
# Delete and recreate deployment
kubectl delete -f conf3/mysql.yaml
bash startup3.sh
```

---

### Issue 2: Cannot Connect from Local Machine

**Symptoms:**
- Connection timeout or refused
- `ERROR 2003 (HY000): Can't connect to MySQL server`

**Diagnosis:**
```bash
# Check if service exists
kubectl get service mysql -n database

# Check service endpoints
kubectl get endpoints mysql -n database

# Test port connectivity
nc -zv localhost 30006
```

**Common Causes:**
1. **Service not created** - Verify service exists
2. **Wrong port** - Ensure using port 30006 (not 3306)
3. **Pod not ready** - Wait for pod to be ready

**Solution:**
```bash
# Verify pod is running
kubectl wait --for=condition=ready pod -l app=mysql -n database --timeout=120s

# Test connection
mysql -h 127.0.0.1 -P 30006 -u root -pCh1ldren -e "SELECT 1;"
```

---

### Issue 3: Authentication Failed

**Symptoms:**
- `ERROR 1045 (28000): Access denied for user`

**Diagnosis:**
```bash
# Check secrets
kubectl get secret mysql-root-pass -n database -o jsonpath='{.data.password}' | base64 -d
kubectl get secret mysql-user-pass -n database -o jsonpath='{.data.username}' | base64 -d
kubectl get secret mysql-user-pass -n database -o jsonpath='{.data.password}' | base64 -d
```

**Common Causes:**
1. **Wrong password** - Verify credentials match secrets
2. **User doesn't exist** - Check MySQL user creation

**Solution:**
```bash
# Connect as root and verify users
kubectl exec -it deployment/mysql-deployment -n database -- mysql -uroot -pCh1ldren -e "SELECT user, host FROM mysql.user;"
```

---

### Issue 4: Database Not Found

**Symptoms:**
- `ERROR 1049 (42000): Unknown database 'paymetv_db1'`

**Diagnosis:**
```bash
# List databases
kubectl exec -it deployment/mysql-deployment -n database -- mysql -uroot -pCh1ldren -e "SHOW DATABASES;"
```

**Solution:**
```bash
# Create database if missing
kubectl exec -it deployment/mysql-deployment -n database -- mysql -uroot -pCh1ldren -e "CREATE DATABASE IF NOT EXISTS paymetv_db1;"
```

---

## Maintenance

### Backup Database

#### Method 1: Using mysqldump
```bash
# Backup to local file
kubectl exec deployment/mysql-deployment -n database -- mysqldump -uroot -pCh1ldren paymetv_db1 > backup.sql

# Backup all databases
kubectl exec deployment/mysql-deployment -n database -- mysqldump -uroot -pCh1ldren --all-databases > full-backup.sql
```

#### Method 2: Using kubectl cp
```bash
# Create backup inside pod
kubectl exec deployment/mysql-deployment -n database -- mysqldump -uroot -pCh1ldren paymetv_db1 > /tmp/backup.sql

# Copy backup to local machine
kubectl cp database/mysql-deployment-xxx:/tmp/backup.sql ./backup.sql
```

---

### Restore Database

```bash
# Restore from local file
kubectl exec -i deployment/mysql-deployment -n database -- mysql -uroot -pCh1ldren paymetv_db1 < backup.sql

# Or copy file to pod first
kubectl cp ./backup.sql database/mysql-deployment-xxx:/tmp/backup.sql
kubectl exec deployment/mysql-deployment -n database -- mysql -uroot -pCh1ldren paymetv_db1 < /tmp/backup.sql
```

---

### Restart MySQL

```bash
# Restart by deleting pod (Deployment will recreate it)
kubectl delete pod -l app=mysql -n database

# Or restart deployment
kubectl rollout restart deployment mysql-deployment -n database

# Wait for pod to be ready
kubectl wait --for=condition=ready pod -l app=mysql -n database --timeout=120s
```

---

### Scale Deployment

**Note:** This deployment uses a single replica with Recreate strategy. Scaling to multiple replicas is not recommended without setting up MySQL replication.

```bash
# Current configuration is single replica
kubectl get deployment mysql-deployment -n database

# To scale (not recommended without replication setup)
# kubectl scale deployment mysql-deployment -n database --replicas=1
```

---

### Update MySQL Version

```bash
# Edit mysql.yaml and change image version
# image: mysql:8.4 -> image: mysql:8.5

# Apply changes
kubectl apply -f conf3/mysql.yaml

# Monitor rollout
kubectl rollout status deployment mysql-deployment -n database
```

---

### Clean Up

```bash
# Delete all resources
kubectl delete -f conf3/mysql.yaml

# Delete secrets
kubectl delete secret mysql-root-pass mysql-user-pass mysql-db-url -n database

# Delete namespace (if no other resources)
kubectl delete namespace database

# Delete PersistentVolume
kubectl delete pv mysql-pv
```

---

## Security Considerations

### Current Setup (Development)

⚠️ **This configuration is suitable for development/testing only**

**Security Issues:**
- Passwords stored in plain text in startup3.sh
- NodePort exposes MySQL to the network
- No TLS/SSL encryption
- Simple passwords
- Root access enabled

### Production Recommendations

For production deployments, consider:

1. **Use Kubernetes Secrets properly:**
   ```bash
   # Create secrets from files (not command line)
   kubectl create secret generic mysql-root-pass --from-file=password=./root-password.txt -n database
   ```

2. **Enable SSL/TLS:**
   - Configure MySQL to require SSL connections
   - Use certificates for encryption

3. **Use stronger passwords:**
   - Generate random passwords
   - Use password managers

4. **Restrict network access:**
   - Use ClusterIP instead of NodePort
   - Implement NetworkPolicies
   - Use firewall rules

5. **Implement RBAC:**
   - Limit who can access secrets
   - Restrict kubectl access

6. **Use StatefulSet for production:**
   - Better for database workloads
   - Stable network identities
   - Ordered deployment/scaling

7. **Implement backup strategy:**
   - Automated backups
   - Off-site backup storage
   - Regular restore testing

---

## Additional Resources

- [MySQL Official Documentation](https://dev.mysql.com/doc/)
- [Kubernetes Persistent Volumes](https://kubernetes.io/docs/concepts/storage/persistent-volumes/)
- [Kubernetes Services](https://kubernetes.io/docs/concepts/services-networking/service/)
- [MySQL on Kubernetes Best Practices](https://kubernetes.io/docs/tasks/run-application/run-single-instance-stateful-application/)

---

## Support

For issues or questions:
1. Check the Troubleshooting section above
2. Run the test script: `./test-mysql-connection.sh`
3. Check pod logs: `kubectl logs -n database -l app=mysql`
4. Review Kubernetes events: `kubectl get events -n database --sort-by='.lastTimestamp'`

---

**Last Updated:** 2026-02-18
**MySQL Version:** 8.4
**Kubernetes Version:** 1.35+

## Connection Details

### Database Information

| Parameter | Value |
|-----------|-------|
| **Database Name** | `paymetv_db1` |
| **Service Name** | `mysql` |
| **Namespace** | `database` |
| **Internal Port** | `3306` |
| **External NodePort** | `30006` |

### Credentials

| User | Username | Password | Purpose |
|------|----------|----------|---------|
| **Application User** | `todd` | `password` | Application database access |
| **Root User** | `root` | `Ch1ldren` | Administrative access |

---

## Connection Methods

### Method 1: Port Forwarding (Recommended for Development)

**Best for:** Development, testing, temporary access


Connect via the command line:
```bash
kubectl port-forward -n database svc/mysql 30006:3306
```

Then connect using your local MySQL client:
```bash
mysql -h 127.0.0.1 -P 30006 -u todd -p
```
Alternatively you can use the following command to connect using the root user:
```bash
mysql -h 127.0.0.1 -P 30006 -u root -pCh1ldren
```
You can connect using Kubernetes exec as well:
```bash
kubectl exec -it deployment/mysql-deployment -n database -- mysql -uroot -pCh1ldren paymetv_db1
```


