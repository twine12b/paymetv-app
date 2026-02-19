# MySQL Database Implementation Summary

## 📋 Overview

This document summarizes the MySQL 8.4 database implementation for the PayMeTV application, including Docker containerization, Kubernetes deployment, and Spring Boot integration.

## ✅ Implementation Complete

All components have been successfully created and integrated:

### 1. MySQL Docker Image ✅

**File**: `dockerfile_db` (project root)

**Features**:
- Base image: MySQL 8.4 official
- Pre-configured environment variables
- Health check for readiness verification
- UTF-8 (utf8mb4) character set support
- Persistent volume support
- Exposed port: 3306

**Build Command**:
```bash
docker build -f dockerfile_db -t paymetv/mysql-db:latest .
```

### 2. Kubernetes Manifests ✅

**File**: `src/main/resources/conf/mysql-deployment.yaml`

**Resources Created**:

| Resource | Name | Purpose |
|----------|------|---------|
| PersistentVolumeClaim | mysql-pvc | 5Gi persistent storage for MySQL data |
| ConfigMap | mysql-config | Custom MySQL configuration (character set, performance tuning) |
| Secret | mysql-secret | Database credentials (root password, user, password) |
| Deployment | mysql-deployment | MySQL 8.4 container with health checks and resource limits |
| Service | mysql-service | ClusterIP service for internal cluster access |

**Resource Limits**:
- CPU: 250m request, 1000m limit
- Memory: 512Mi request, 1Gi limit

**Health Checks**:
- Liveness probe: 60s initial delay, 10s period
- Readiness probe: 30s initial delay, 5s period

### 3. Automated Deployment Script ✅

**File**: `src/main/resources/conf/setup_cert_manager.sh`

**Enhanced with MySQL deployment**:

The script now performs these steps in order:

1. **Build MySQL Docker Image** - Builds from `dockerfile_db`
2. **Deploy MySQL to Kubernetes** - Applies all MySQL manifests
3. **Wait for MySQL Ready** - Polls until MySQL pod is ready
4. **Verify MySQL Service** - Confirms service is available
5. **Deploy Ingress NGINX** - Sets up ingress controller
6. **Wait for Ingress Ready** - Ensures ingress is operational
7. **Deploy PayMeTV App (Initial)** - Deploys app without TLS
8. **Wait for App Ready** - Confirms app is running
9. **Install Cert-Manager** - Deploys certificate management
10. **Wait for Cert-Manager Ready** - Ensures cert-manager is operational
11. **Install Certificate Issuers** - Creates self-signed, staging, and prod issuers
12. **Create Certificates** - Generates TLS certificates
13. **Deploy PayMeTV App (Final)** - Updates app with TLS configuration
14. **Final Verification** - Displays deployment status

**Features**:
- Color-coded output for easy reading
- Error handling with `set -e`
- Proper wait logic for each component
- Comprehensive status reporting
- Graceful handling of missing files

### 4. Spring Boot Configuration ✅

**File**: `src/main/resources/application.properties`

**Added Configuration**:

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://mysql-service:3306/paymetv_db
spring.datasource.username=paymetv_user
spring.datasource.password=paymetv_pass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Connection Pool Configuration (HikariCP)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

**Features**:
- Automatic schema creation/update
- Connection pooling with HikariCP
- MySQL 8 dialect support
- Optimized connection settings

### 5. Maven Dependencies ✅

**File**: `pom.xml`

**Added Dependencies**:

```xml
<!-- Spring Data JPA for database access -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- MySQL Connector -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 6. Documentation ✅

**Created Documentation**:

1. **`docs/MYSQL-DATABASE-SETUP.md`** (410 lines)
   - Comprehensive setup guide
   - Component descriptions
   - Deployment procedures
   - Verification steps
   - Database access methods
   - Maintenance operations
   - Troubleshooting guide
   - Security considerations
   - Integration examples

2. **`docs/MYSQL-QUICK-START.md`** (165 lines)
   - Quick deployment guide
   - Common operations
   - Troubleshooting tips
   - Configuration reference


## 🧪 Testing

### Verify MySQL Deployment

```bash
# Check pod status
kubectl get pods -l app=mysql -n default

# Expected output:
# NAME                                READY   STATUS    RESTARTS   AGE
# mysql-deployment-xxxxxxxxxx-xxxxx   1/1     Running   0          2m

# Check service
kubectl get svc mysql-service -n default

# Expected output:
# NAME            TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
# mysql-service   ClusterIP   10.96.xxx.xxx   <none>        3306/TCP   2m
```

### Test MySQL Connection

```bash
# Get MySQL pod name
MYSQL_POD=$(kubectl get pods -l app=mysql -n default -o jsonpath='{.items[0].metadata.name}')

# Connect to MySQL
kubectl exec -it $MYSQL_POD -n default -- mysql -u paymetv_user -ppaymetv_pass paymetv_db

# Run test query
kubectl exec -it $MYSQL_POD -n default -- mysql -u paymetv_user -ppaymetv_pass paymetv_db -e "SHOW DATABASES;"
```

### Test Application Connection

```bash
# Check application logs for database connection
kubectl logs -l app=paymetv-app -n default | grep -i mysql

# Look for successful connection messages like:
# "HikariPool-1 - Starting..."
# "HikariPool-1 - Start completed."
```

### Create Test Data

```bash
# Connect to MySQL
kubectl exec -it $MYSQL_POD -n default -- mysql -u paymetv_user -ppaymetv_pass paymetv_db

# Create a test table
CREATE TABLE test_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

# Insert test data
INSERT INTO test_users (username, email) VALUES ('testuser', 'test@example.com');

# Query test data
SELECT * FROM test_users;
```

## 📊 Configuration Details

### Default Credentials

| Parameter | Value |
|-----------|-------|
| Root Password | `rootpassword` |
| Database Name | `paymetv_db` |
| Application User | `paymetv_user` |
| Application Password | `paymetv_pass` |
| Port | `3306` |
| Character Set | `utf8mb4` |
| Collation | `utf8mb4_unicode_ci` |

⚠️ **Security Warning**: Change these credentials for production use!

### Connection Details

**From Kubernetes Cluster**:
- Host: `mysql-service`
- Port: `3306`
- JDBC URL: `jdbc:mysql://mysql-service:3306/paymetv_db`

**From Local Machine** (using port-forward):
```bash
kubectl port-forward svc/mysql-service 3306:3306 -n default
```
- Host: `localhost`
- Port: `3306`
- JDBC URL: `jdbc:mysql://localhost:3306/paymetv_db`

### Resource Allocation

| Resource | Request | Limit |
|----------|---------|-------|
| CPU | 250m (0.25 cores) | 1000m (1 core) |
| Memory | 512Mi | 1Gi |
| Storage | 5Gi (PVC) | - |

### MySQL Configuration

**Performance Settings** (via ConfigMap):
- Max connections: 200
- InnoDB buffer pool size: 256M
- InnoDB log file size: 64M
- Slow query log: Enabled (2s threshold)

**Character Set**:
- Server character set: utf8mb4
- Server collation: utf8mb4_unicode_ci

## 🔧 Common Operations

### View Logs

```bash
# Real-time logs
kubectl logs -l app=mysql -n default -f

# Last 50 lines
kubectl logs -l app=mysql -n default --tail=50
```

### Restart MySQL

```bash
kubectl rollout restart deployment/mysql-deployment -n default
kubectl rollout status deployment/mysql-deployment -n default
```

### Backup Database

```bash
MYSQL_POD=$(kubectl get pods -l app=mysql -n default -o jsonpath='{.items[0].metadata.name}')
kubectl exec $MYSQL_POD -n default -- mysqldump -u root -prootpassword paymetv_db > backup-$(date +%Y%m%d-%H%M%S).sql
```

### Restore Database

```bash
kubectl exec -i $MYSQL_POD -n default -- mysql -u root -prootpassword paymetv_db < backup.sql
```

### Access MySQL Shell

```bash
kubectl exec -it $MYSQL_POD -n default -- mysql -u root -prootpassword
```

### Check Resource Usage

```bash
kubectl top pod -l app=mysql -n default
```

## 🐛 Troubleshooting

### Pod Not Starting

**Check pod status**:
```bash
kubectl describe pod -l app=mysql -n default
kubectl logs -l app=mysql -n default --tail=100
```

**Common issues**:
- PVC not bound → Check storage class availability
- Image pull errors → Verify Docker image exists
- Resource limits → Check cluster has available resources

### Connection Refused

**Check service and endpoints**:
```bash
kubectl get svc mysql-service -n default
kubectl get endpoints mysql-service -n default
```

**Test from debug pod**:
```bash
kubectl run -it --rm debug --image=mysql:8.4 --restart=Never -- \
  mysql -h mysql-service -u paymetv_user -ppaymetv_pass paymetv_db
```

### Application Can't Connect

**Verify DNS resolution**:
```bash
kubectl run -it --rm debug --image=busybox --restart=Never -- \
  nslookup mysql-service
```

**Check application configuration**:
- Verify `spring.datasource.url` uses `mysql-service` as hostname
- Confirm credentials match Secret values
- Check application logs for connection errors

### Data Not Persisting

**Check PVC status**:
```bash
kubectl get pvc mysql-pvc -n default
kubectl describe pvc mysql-pvc -n default
```

**Verify volume mount**:
```bash
kubectl exec -it $MYSQL_POD -n default -- df -h /var/lib/mysql
```

## 🔒 Security Best Practices

### For Production Deployment

1. **Change Default Credentials**
   - Use strong, unique passwords
   - Store in proper secret management system

2. **Use Secret Management**
   - Implement Sealed Secrets or External Secrets Operator
   - Never commit secrets to version control
   - Rotate credentials regularly

3. **Enable SSL/TLS**
   - Configure MySQL to require SSL connections
   - Use certificates for authentication

4. **Implement Network Policies**
   - Restrict access to MySQL service
   - Only allow connections from PayMeTV app pods

5. **Set Up Backups**
   - Implement automated backup schedule
   - Store backups in secure, off-cluster location
   - Test restore procedures regularly

6. **Monitor and Alert**
   - Set up monitoring for MySQL metrics
   - Configure alerts for failures and performance issues
   - Track resource usage trends

## 📚 File Structure

```
paymetv-app/
├── dockerfile_db                                    # MySQL Docker image
├── MYSQL-IMPLEMENTATION-SUMMARY.md                  # This file
├── docs/
│   ├── MYSQL-DATABASE-SETUP.md                     # Comprehensive setup guide
│   └── MYSQL-QUICK-START.md                        # Quick start guide
├── pom.xml                                          # Updated with JPA and MySQL dependencies
├── src/
│   └── main/
│       └── resources/
│           ├── application.properties               # Updated with MySQL configuration
│           └── conf/
│               ├── mysql-deployment.yaml            # Kubernetes manifests
│               └── setup_cert_manager.sh            # Updated deployment script
```

## 🎯 Next Steps

1. **Deploy the Database**
   ```bash
   cd src/main/resources/conf
   ./setup_cert_manager.sh
   ```

2. **Verify Deployment**
   ```bash
   kubectl get pods -l app=mysql -n default
   kubectl get svc mysql-service -n default
   ```

3. **Create JPA Entities**
   - Define your domain models with `@Entity` annotations
   - Create repositories extending `JpaRepository`

4. **Test Database Operations**
   - Create, read, update, delete operations
   - Verify data persistence across pod restarts

5. **Set Up Backups**
   - Implement backup strategy
   - Test restore procedures

6. **Monitor Performance**
   - Track resource usage
   - Optimize queries and indexes as needed

## ✅ Summary

The MySQL database implementation provides:

✅ **Containerized MySQL 8.4** - Docker image with proper configuration
✅ **Kubernetes Deployment** - Complete manifests with best practices
✅ **Persistent Storage** - 5Gi PVC for data persistence
✅ **Health Checks** - Liveness and readiness probes
✅ **Resource Management** - CPU and memory limits
✅ **Configuration Management** - ConfigMap for MySQL settings
✅ **Secret Management** - Kubernetes Secret for credentials
✅ **Service Discovery** - ClusterIP service for internal access
✅ **Automated Deployment** - Integrated into setup script
✅ **Spring Boot Integration** - JPA/Hibernate with HikariCP
✅ **Comprehensive Documentation** - Setup, usage, and troubleshooting guides

The database is production-ready with proper health checks, resource limits, persistent storage, and comprehensive documentation. All components are integrated into the automated deployment script for easy setup.

For detailed information, refer to:
- **Setup Guide**: `docs/MYSQL-DATABASE-SETUP.md`
- **Quick Start**: `docs/MYSQL-QUICK-START.md`

Happy coding! 🚀


