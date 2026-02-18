#!/bin/bash

#############################################################################
# MySQL Connection Test Script (Port-Forward Method)
#
# Purpose: Automated testing of MySQL connectivity using port-forwarding
# Tests: Pod status, port-forward, MySQL connection, database access
# Method: Establishes kubectl port-forward and tests connection via localhost
#############################################################################

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
NAMESPACE="database"
SERVICE_NAME="mysql"
DEPLOYMENT_NAME="mysql-deployment"
DATABASE_NAME="paymetv_db1"
USER_NAME="todd"
USER_PASSWORD="password"
ROOT_USER="root"
ROOT_PASSWORD="Ch1ldren"
PORT_FORWARD_PORT=3306

# Test results tracking
TESTS_PASSED=0
TESTS_FAILED=0
TOTAL_TESTS=5

# Port-forward process ID
PF_PID=""

# Helper functions
print_header() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}\n"
}

print_test() {
    echo -e "${YELLOW}[TEST $1/$TOTAL_TESTS]${NC} $2"
}

print_pass() {
    echo -e "${GREEN}✓ PASS:${NC} $1"
    ((TESTS_PASSED++))
}

print_fail() {
    echo -e "${RED}✗ FAIL:${NC} $1"
    ((TESTS_FAILED++))
}

print_info() {
    echo -e "${BLUE}ℹ INFO:${NC} $1"
}

print_error() {
    echo -e "${RED}ERROR:${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    print_header "Checking Prerequisites"

    local all_tools_present=true

    # Check kubectl
    if command -v kubectl &> /dev/null; then
        print_info "kubectl: $(kubectl version --client --short 2>/dev/null | head -1)"
    else
        print_error "kubectl is not installed or not in PATH"
        all_tools_present=false
    fi

    # Check mysql client
    if command -v mysql &> /dev/null; then
        print_info "mysql client: $(mysql --version)"
    else
        print_error "mysql client is not installed or not in PATH"
        print_info "Install with: brew install mysql-client (macOS) or apt-get install mysql-client (Linux)"
        all_tools_present=false
    fi

    # Check nc (netcat)
    if command -v nc &> /dev/null; then
        print_info "nc (netcat): Available"
    else
        print_error "nc (netcat) is not installed or not in PATH"
        print_info "Install with: brew install netcat (macOS) or apt-get install netcat (Linux)"
        all_tools_present=false
    fi

    if [ "$all_tools_present" = false ]; then
        echo ""
        print_error "Missing required tools. Please install them and try again."
        exit 1
    fi

    echo ""
}

# Start port-forwarding
start_port_forward() {
    print_header "Starting Port-Forward Connection"
    print_info "Establishing port-forward to MySQL service..."

    # Start port-forward in background
    kubectl port-forward -n $NAMESPACE service/$SERVICE_NAME $PORT_FORWARD_PORT:3306 >/dev/null 2>&1 &
    PF_PID=$!

    # Wait for port-forward to establish
    sleep 3

    # Verify port-forward is running
    if ps -p $PF_PID > /dev/null 2>&1; then
        print_pass "Port-forward established (PID: $PF_PID)"
        print_info "Forwarding localhost:$PORT_FORWARD_PORT -> mysql:3306"
    else
        print_fail "Failed to establish port-forward"
        print_error "Port-forward process died immediately"
        exit 1
    fi
    echo ""
}

# Stop port-forwarding
stop_port_forward() {
    if [ -n "$PF_PID" ] && ps -p $PF_PID > /dev/null 2>&1; then
        print_info "Stopping port-forward (PID: $PF_PID)..."
        kill $PF_PID 2>/dev/null
        wait $PF_PID 2>/dev/null
        print_info "Port-forward stopped"
    fi
}

# Test 1: Verify MySQL pod is running
test_pod_status() {
    print_test 1 "Verifying MySQL pod is running in '$NAMESPACE' namespace"

    local pod_status=$(kubectl get pods -n $NAMESPACE -l app=mysql -o jsonpath='{.items[0].status.phase}' 2>/dev/null)
    local pod_name=$(kubectl get pods -n $NAMESPACE -l app=mysql -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

    if [ "$pod_status" = "Running" ]; then
        print_pass "Pod '$pod_name' is running"
        local ready=$(kubectl get pods -n $NAMESPACE -l app=mysql -o jsonpath='{.items[0].status.containerStatuses[0].ready}' 2>/dev/null)
        if [ "$ready" = "true" ]; then
            print_info "Pod is ready and healthy"
        else
            print_fail "Pod is running but not ready"
        fi
    else
        print_fail "Pod is not running (status: $pod_status)"
        print_info "Run: kubectl get pods -n $NAMESPACE -l app=mysql"
    fi
    echo ""
}

# Test 2: Test port-forward connectivity
test_port_forward_connectivity() {
    print_test 2 "Testing port-forward connectivity"

    # Check if port-forward process is still running
    if ps -p $PF_PID > /dev/null 2>&1; then
        print_pass "Port-forward process is running"
    else
        print_fail "Port-forward process has died"
        print_info "PID $PF_PID is not running"
        return
    fi

    # Test if port is accessible
    if nc -z localhost $PORT_FORWARD_PORT 2>/dev/null; then
        print_pass "Port $PORT_FORWARD_PORT is accessible on localhost"
    else
        print_fail "Cannot connect to localhost:$PORT_FORWARD_PORT"
        print_info "Port-forward may not be working correctly"
    fi
    echo ""
}

# Test 3: Test MySQL connection via port-forward using user credentials
test_mysql_connection_user() {
    print_test 3 "Testing MySQL connection via port-forward (user: $USER_NAME)"

    local result=$(mysql -h 127.0.0.1 -P $PORT_FORWARD_PORT -u $USER_NAME -p$USER_PASSWORD -e "SELECT 'success' AS result;" 2>&1)

    if echo "$result" | grep -q "success"; then
        print_pass "Successfully connected as user '$USER_NAME'"
    else
        print_fail "Failed to connect as user '$USER_NAME'"
        print_error "Error: $result"
    fi
    echo ""
}

# Test 4: Test MySQL connection via port-forward using root credentials
test_mysql_connection_root() {
    print_test 4 "Testing MySQL connection via port-forward (user: root)"

    local result=$(mysql -h 127.0.0.1 -P $PORT_FORWARD_PORT -u $ROOT_USER -p$ROOT_PASSWORD -e "SELECT 'success' AS result;" 2>&1)

    if echo "$result" | grep -q "success"; then
        print_pass "Successfully connected as root user"
    else
        print_fail "Failed to connect as root user"
        print_error "Error: $result"
    fi
    echo ""
}

# Test 5: Execute SQL query and verify database exists
test_database_access() {
    print_test 5 "Verifying database access and '$DATABASE_NAME' exists"

    # First, execute SHOW DATABASES
    local result=$(mysql -h 127.0.0.1 -P $PORT_FORWARD_PORT -u $ROOT_USER -p$ROOT_PASSWORD -e "SHOW DATABASES;" 2>&1)

    if echo "$result" | grep -q "Database"; then
        print_pass "Successfully executed SHOW DATABASES query"
        print_info "Available databases:"
        echo "$result" | grep -v "mysql: \[Warning\]" | tail -n +2 | while read db; do
            echo "  - $db"
        done
    else
        print_fail "Failed to execute SHOW DATABASES query"
        print_error "Error: $result"
        echo ""
        return
    fi

    # Verify paymetv_db1 exists
    if echo "$result" | grep -q "$DATABASE_NAME"; then
        print_pass "Database '$DATABASE_NAME' exists"

        # Try to use the database and show tables
        local tables=$(mysql -h 127.0.0.1 -P $PORT_FORWARD_PORT -u $ROOT_USER -p$ROOT_PASSWORD $DATABASE_NAME -e "SHOW TABLES;" 2>&1)
        if echo "$tables" | grep -q "Tables_in_$DATABASE_NAME"; then
            print_info "Tables in $DATABASE_NAME:"
            echo "$tables" | grep -v "mysql: \[Warning\]" | tail -n +2 | while read table; do
                echo "  - $table"
            done
        else
            print_info "Database exists but contains no tables yet"
        fi
    else
        print_fail "Database '$DATABASE_NAME' does not exist"
        print_info "The database should be created automatically by the MYSQL_DATABASE environment variable"
    fi
    echo ""
}

# Print summary
print_summary() {
    print_header "Test Summary"

    echo -e "Total Tests: $TOTAL_TESTS"
    echo -e "${GREEN}Passed: $TESTS_PASSED${NC}"
    echo -e "${RED}Failed: $TESTS_FAILED${NC}"
    echo ""

    if [ $TESTS_FAILED -eq 0 ]; then
        echo -e "${GREEN}========================================${NC}"
        echo -e "${GREEN}✓ ALL TESTS PASSED!${NC}"
        echo -e "${GREEN}========================================${NC}"
        echo ""
        echo -e "${GREEN}Your MySQL deployment is working correctly!${NC}"
        echo ""
        echo "Connection details (using port-forwarding):"
        echo "  Host: localhost (or 127.0.0.1)"
        echo "  Port: $PORT_FORWARD_PORT"
        echo "  Database: $DATABASE_NAME"
        echo "  User: $USER_NAME / Password: $USER_PASSWORD"
        echo "  Root: $ROOT_USER / Password: $ROOT_PASSWORD"
        echo ""
        echo "To connect, first start port-forwarding:"
        echo "  kubectl port-forward -n $NAMESPACE service/$SERVICE_NAME $PORT_FORWARD_PORT:3306"
        echo ""
        echo "Then connect with:"
        echo "  mysql -h 127.0.0.1 -P $PORT_FORWARD_PORT -u $USER_NAME -p$USER_PASSWORD $DATABASE_NAME"
        echo ""
        return 0
    else
        echo -e "${RED}========================================${NC}"
        echo -e "${RED}✗ SOME TESTS FAILED${NC}"
        echo -e "${RED}========================================${NC}"
        echo ""
        echo "Troubleshooting steps:"
        echo "1. Check pod status: kubectl get pods -n $NAMESPACE"
        echo "2. Check pod logs: kubectl logs -n $NAMESPACE -l app=mysql"
        echo "3. Check service: kubectl get service $SERVICE_NAME -n $NAMESPACE"
        echo "4. Verify secrets: kubectl get secrets -n $NAMESPACE"
        echo ""
        echo "For more help, see: src/main/resources/database/conf3/README.md"
        echo ""
        return 1
    fi
}

# Main execution
main() {
    print_header "MySQL Connection Test Suite (Port-Forward Method)"
    echo "Testing MySQL deployment in Kubernetes"
    echo "Namespace: $NAMESPACE"
    echo "Service: $SERVICE_NAME"
    echo "Database: $DATABASE_NAME"
    echo "Method: Port-forwarding to localhost:$PORT_FORWARD_PORT"
    echo ""

    # Check prerequisites
    check_prerequisites

    # Start port-forwarding
    start_port_forward

    # Ensure port-forward is cleaned up on exit
    trap stop_port_forward EXIT

    # Run tests
    test_pod_status
    test_port_forward_connectivity
    test_mysql_connection_user
    test_mysql_connection_root
    test_database_access

    # Print summary
    print_summary

    # Stop port-forward
    stop_port_forward

    # Exit with appropriate code
    if [ $TESTS_FAILED -eq 0 ]; then
        exit 0
    else
        exit 1
    fi
}

# Run main function
main

