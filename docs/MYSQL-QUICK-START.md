# MySQL Database Quick Start Guide

## 🚀 Quick Deployment

### Option 1: Automated Setup (Recommended)

Run the complete setup script that deploys MySQL, Ingress NGINX, PayMeTV app, and cert-manager:

```bash
cd src/main/resources/conf
./setup_cert_manager.sh
```

This script will:
1. ✅ Build MySQL Docker image
2. ✅ Deploy MySQL to Kubernetes
3. ✅ Wait for MySQL to be ready
4. ✅ Deploy the rest of the infrastructure

### Option 2: MySQL Only

Deploy just the MySQL database:

```bash
# Build MySQL Docker image
docker build -f dockerfile_db -t paymetv/mysql-db:latest .

# Deploy to Kubernetes
kubectl apply -f src/main/resources/conf/mysql-deployment.yaml

# Wait for MySQL to be ready
kubectl wait --for=condition=ready pod -l app=mysql -n default --timeout=300s
```

## 📋 Quick Verification

### Check MySQL Status

```bash
# Check pod
kubectl get pods -l app=mysql -n default

# Check service
kubectl get svc mysql-service -n default

# Check persistent volume
kubectl get pvc mysql-pvc -n default
```

### Test MySQL Connection

```bash
# Get pod name
MYSQL_POD=$(kubectl get pods -l app=mysql -n default -o jsonpath='{.items[0].metadata.name}')

# Connect to MySQL
kubectl exec -it $MYSQL_POD -n default -- mysql -u paymetv_user -ppaymetv_pass paymetv_db

# Run test query
kubectl exec -it $MYSQL_POD -n default -- mysql -u paymetv_user -ppaymetv_pass paymetv_db -e "SHOW DATABASES;"
```

## 🔧 Configuration

### Default Credentials

- **Root Password**: `rootpassword`
- **Database**: `paymetv_db`
- **User**: `paymetv_user`
- **Password**: `paymetv_pass`

### Connection Details

**From within Kubernetes:**
- Host: `mysql-service`
- Port: `3306`

**From local machine (port-forward):**
```bash
kubectl port-forward svc/mysql-service 3306:3306 -n default
```
Then connect to `localhost:3306`

### Spring Boot Configuration

The application automatically connects using:
```properties
spring.datasource.url=jdbc:mysql://mysql-service:3306/paymetv_db
spring.datasource.username=paymetv_user
spring.datasource.password=paymetv_pass
```

## 🛠️ Common Operations

### View Logs

```bash
kubectl logs -l app=mysql -n default --tail=50 -f
```

### Restart MySQL

```bash
kubectl rollout restart deployment/mysql-deployment -n default
```

### Backup Database

```bash
MYSQL_POD=$(kubectl get pods -l app=mysql -n default -o jsonpath='{.items[0].metadata.name}')
kubectl exec $MYSQL_POD -n default -- mysqldump -u root -prootpassword paymetv_db > backup.sql
```

### Restore Database

```bash
kubectl exec -i $MYSQL_POD -n default -- mysql -u root -prootpassword paymetv_db < backup.sql
```

## 🐛 Troubleshooting

### Pod Not Ready

```bash
# Check pod status
kubectl describe pod -l app=mysql -n default

# Check logs
kubectl logs -l app=mysql -n default --tail=100
```

### Connection Issues

```bash
# Check service endpoints
kubectl get endpoints mysql-service -n default

# Test from another pod
kubectl run -it --rm debug --image=mysql:8.4 --restart=Never -- mysql -h mysql-service -u paymetv_user -ppaymetv_pass paymetv_db
```

### Resource Issues

```bash
# Check resource usage
kubectl top pod -l app=mysql -n default

# Check events
kubectl get events -n default --sort-by='.lastTimestamp' | grep mysql
```

## 📚 Resources

- **Full Documentation**: [MYSQL-DATABASE-SETUP.md](./MYSQL-DATABASE-SETUP.md)
- **Dockerfile**: `dockerfile_db` (project root)
- **Kubernetes Manifests**: `src/main/resources/conf/mysql-deployment.yaml`
- **Spring Boot Config**: `src/main/resources/application.properties`

## 🔒 Security Notes

⚠️ **Important**: The default credentials are for development only!

For production:
1. Use proper secret management (Sealed Secrets, External Secrets)
2. Change default passwords
3. Enable SSL/TLS for connections
4. Implement network policies
5. Set up regular backups

## ✅ What's Included

The MySQL deployment includes:

- ✅ MySQL 8.4 database server
- ✅ Persistent storage (5Gi PVC)
- ✅ Health checks (liveness & readiness probes)
- ✅ Resource limits (CPU: 1 core, Memory: 1Gi)
- ✅ Custom configuration via ConfigMap
- ✅ Secure credentials via Secret
- ✅ ClusterIP service for internal access
- ✅ UTF-8 character set support
- ✅ Connection pooling (HikariCP)
- ✅ Automatic schema management (Hibernate)

## 🎯 Next Steps

1. ✅ Deploy MySQL using the setup script
2. ✅ Verify MySQL is running and ready
3. ✅ Deploy PayMeTV application
4. ✅ Check application connects to database
5. ✅ Create your JPA entities and repositories
6. ✅ Test database operations

Happy coding! 🚀

