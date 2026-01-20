# MySQL Database Setup for PayMeTV Application

## Overview

This guide explains the MySQL 8.4 database setup for the PayMeTV application, including Docker image creation, Kubernetes deployment, and Spring Boot integration.

## Components

### 1. MySQL Docker Image

**File**: `dockerfile_db` (project root)

**Base Image**: MySQL 8.4 official image

**Configuration**:
- Root password: `rootpassword` (configurable)
- Database name: `paymetv_db`
- Database user: `paymetv_user`
- Database password: `paymetv_pass`
- Character set: UTF-8 (utf8mb4)
- Port: 3306

**Features**:
- Health check for MySQL readiness
- Persistent volume support
- Proper UTF-8 character set configuration

### 2. Kubernetes Manifests

**File**: `src/main/resources/conf/mysql-deployment.yaml`

**Resources Created**:

#### PersistentVolumeClaim
- **Name**: `mysql-pvc`
- **Storage**: 5Gi
- **Access Mode**: ReadWriteOnce
- **Purpose**: Persistent storage for MySQL data

#### ConfigMap
- **Name**: `mysql-config`
- **Purpose**: Custom MySQL configuration
- **Settings**:
  - Character set: utf8mb4
  - Max connections: 200
  - InnoDB buffer pool: 256M
  - Slow query logging enabled

#### Secret
- **Name**: `mysql-secret`
- **Type**: Opaque
- **Contains**: Database credentials
  - MYSQL_ROOT_PASSWORD
  - MYSQL_DATABASE
  - MYSQL_USER
  - MYSQL_PASSWORD

**Note**: In production, use proper secret management (Sealed Secrets, External Secrets Operator, etc.)

#### Deployment
- **Name**: `mysql-deployment`
- **Replicas**: 1 (single instance)
- **Strategy**: Recreate (important for stateful apps)
- **Image**: `paymetv/mysql-db:latest`

**Resource Limits**:
- CPU Request: 250m
- CPU Limit: 1000m (1 core)
- Memory Request: 512Mi
- Memory Limit: 1Gi

**Health Checks**:
- **Liveness Probe**: Checks if MySQL is running (60s initial delay)
- **Readiness Probe**: Checks if MySQL accepts connections (30s initial delay)

**Volumes**:
- Persistent storage mounted at `/var/lib/mysql`
- ConfigMap mounted at `/etc/mysql/conf.d`

#### Service
- **Name**: `mysql-service`
- **Type**: ClusterIP (internal only)
- **Port**: 3306
- **Session Affinity**: ClientIP

### 3. Spring Boot Configuration

**File**: `src/main/resources/application.properties`

**Database Connection**:
```properties
spring.datasource.url=jdbc:mysql://mysql-service:3306/paymetv_db
spring.datasource.username=paymetv_user
spring.datasource.password=paymetv_pass
```

**JPA/Hibernate**:
- DDL Auto: `update` (creates/updates schema automatically)
- Dialect: MySQL8Dialect
- Show SQL: false (set to true for debugging)

**Connection Pool (HikariCP)**:
- Maximum pool size: 10
- Minimum idle: 5
- Connection timeout: 30s
- Idle timeout: 10 minutes
- Max lifetime: 30 minutes

### 4. Maven Dependencies

**File**: `pom.xml`

Added dependencies:
- `spring-boot-starter-data-jpa` - JPA/Hibernate support
- `mysql-connector-j` - MySQL JDBC driver (runtime scope)

## Deployment Process

### Automated Deployment

The `setup_cert_manager.sh` script handles the complete deployment:

```bash
cd src/main/resources/conf
./setup_cert_manager.sh
```

**Deployment Steps**:

1. **Build MySQL Docker Image**
   - Uses `dockerfile_db` from project root
   - Tags as `paymetv/mysql-db:latest`

2. **Deploy MySQL to Kubernetes**
   - Applies `mysql-deployment.yaml`
   - Creates PVC, ConfigMap, Secret, Deployment, Service

3. **Wait for MySQL Pod Ready**
   - Polls until MySQL pod is ready
   - Verifies readiness probe passes

4. **Verify MySQL Service**
   - Checks service is available
   - Displays service details

5. **Continue with Application Deployment**
   - Deploys Ingress NGINX
   - Deploys PayMeTV application
   - Installs cert-manager
   - Configures TLS certificates

### Test MySQL Connection from Pod

```bash
# Get MySQL pod name
MYSQL_POD=$(kubectl get pods -l app=mysql -n default -o jsonpath='{.items[0].metadata.name}')

# Connect to MySQL
kubectl exec -it $MYSQL_POD -n default -- mysql -u paymetv_user -ppaymetv_pass paymetv_db

# Run a test query
kubectl exec -it $MYSQL_POD -n default -- mysql -u paymetv_user -ppaymetv_pass paymetv_db -e "SHOW DATABASES;"
```

### Check MySQL Logs

```bash
kubectl logs -l app=mysql -n default --tail=50
```

### Test Connection from Application

Once the PayMeTV application is deployed, it will automatically connect to MySQL using the service name `mysql-service`.

Check application logs for database connection:
```bash
kubectl logs -l app=paymetv-app -n default | grep -i mysql
```

## Database Access

### From Within Kubernetes Cluster

Applications in the same namespace can connect using:
- **Hostname**: `mysql-service`
- **Port**: `3306`
- **Database**: `paymetv_db`
- **Username**: `paymetv_user`
- **Password**: `paymetv_pass`

### From Outside Kubernetes (Development)

For local development, you can port-forward:

```bash
kubectl port-forward svc/mysql-service 3306:3306 -n default
```

Then connect using:
```bash
mysql -h 127.0.0.1 -P 3306 -u paymetv_user -ppaymetv_pass paymetv_db
```

Or use a GUI tool (MySQL Workbench, DBeaver, etc.):
- Host: `localhost`
- Port: `3306`
- Database: `paymetv_db`
- Username: `paymetv_user`
- Password: `paymetv_pass`

## Maintenance

### Backup Database

```bash
# Get MySQL pod name
MYSQL_POD=$(kubectl get pods -l app=mysql -n default -o jsonpath='{.items[0].metadata.name}')

# Create backup
kubectl exec $MYSQL_POD -n default -- mysqldump -u root -prootpassword paymetv_db > backup.sql

# Or backup all databases
kubectl exec $MYSQL_POD -n default -- mysqldump -u root -prootpassword --all-databases > backup-all.sql
```

### Restore Database

```bash
# Restore from backup
kubectl exec -i $MYSQL_POD -n default -- mysql -u root -prootpassword paymetv_db < backup.sql
```

### Scale/Restart MySQL

```bash
# Restart MySQL pod
kubectl rollout restart deployment/mysql-deployment -n default

# Check rollout status
kubectl rollout status deployment/mysql-deployment -n default
```

### Update MySQL Configuration

Edit the ConfigMap and restart:

```bash
kubectl edit configmap mysql-config -n default
kubectl rollout restart deployment/mysql-deployment -n default
```

### Update Credentials

**Important**: Changing credentials requires updating both the Secret and application configuration.

```bash
# Update secret
kubectl edit secret mysql-secret -n default

# Update application.properties and rebuild app
# Then restart both MySQL and application
kubectl rollout restart deployment/mysql-deployment -n default
kubectl rollout restart deployment/paymetv-app-deployment -n default
```

## Troubleshooting

### Pod Not Starting

Check pod events:
```bash
kubectl describe pod -l app=mysql -n default
```

Common issues:
- PVC not bound (check storage class)
- Resource limits too low
- Image pull errors

### Connection Refused

Check service and endpoints:
```bash
kubectl get svc mysql-service -n default
kubectl get endpoints mysql-service -n default
```

Verify MySQL is listening:
```bash
kubectl exec -it $MYSQL_POD -n default -- netstat -tlnp | grep 3306
```

### Readiness Probe Failing

Check MySQL logs:
```bash
kubectl logs -l app=mysql -n default --tail=100
```

Common causes:
- MySQL still initializing (wait longer)
- Incorrect credentials in probe
- Resource constraints

### Data Persistence Issues

Check PVC status:
```bash
kubectl get pvc mysql-pvc -n default
kubectl describe pvc mysql-pvc -n default
```

Verify volume mount:
```bash
kubectl exec -it $MYSQL_POD -n default -- df -h /var/lib/mysql
```

### Performance Issues

Check resource usage:
```bash
kubectl top pod -l app=mysql -n default
```

Adjust resource limits in `mysql-deployment.yaml` if needed.

## Security Considerations

### Production Recommendations

1. **Use Secret Management**
   - Implement Sealed Secrets or External Secrets Operator
   - Never commit secrets to version control
   - Rotate credentials regularly

2. **Network Policies**
   - Restrict access to MySQL service
   - Only allow connections from PayMeTV app pods

3. **TLS/SSL**
   - Enable SSL for MySQL connections
   - Use certificates for authentication

4. **Backup Strategy**
   - Implement automated backups
   - Store backups in secure location
   - Test restore procedures regularly

5. **Resource Limits**
   - Set appropriate CPU/memory limits
   - Monitor resource usage
   - Adjust based on workload

6. **High Availability**
   - Consider MySQL replication
   - Use StatefulSet for multi-replica setup
   - Implement proper backup/restore procedures

## Integration with PayMeTV Application

The PayMeTV Spring Boot application automatically:

1. **Connects to MySQL** on startup using `mysql-service:3306`
2. **Creates/updates schema** using Hibernate DDL auto-update
3. **Manages connection pool** using HikariCP
4. **Handles transactions** using Spring's @Transactional

### Example Entity

To use the database in your application, create JPA entities:

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

    // Getters and setters
}
```

### Example Repository

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

## Summary

The MySQL database setup provides:

✅ **Persistent Storage** - Data survives pod restarts
✅ **Health Checks** - Automatic restart on failure
✅ **Resource Management** - CPU/memory limits
✅ **Configuration Management** - ConfigMap for settings
✅ **Secret Management** - Credentials stored securely
✅ **Service Discovery** - Accessible via `mysql-service`
✅ **Automated Deployment** - Integrated into setup script
✅ **Spring Boot Integration** - Automatic connection and schema management

For questions or issues, refer to the troubleshooting section or check the Kubernetes and MySQL documentation.


