# Aurora Ledger - Quick Validation Commands

## Banking-Grade Infrastructure Validation

### Prerequisites Verification
```powershell
# Verify Docker is running
docker --version

# Verify PowerShell version
$PSVersionTable.PSVersion

# Check port availability
netstat -an | findstr ":3000 :6379 :8081 :9090 :9092 :27017"
```

### Stack Deployment
```powershell
# Navigate to project directory
cd "C:\Users\LENOVO\OneDrive\Área de Trabalho\AuroraLedgerSantander"

# Deploy observability stack
docker compose up -d

# Wait for services to stabilize (30 seconds)
Start-Sleep 30
```

### Individual Service Validation

#### 1. Container Status Check
```powershell
docker compose ps --format "table {{.Service}}\t{{.Status}}\t{{.Health}}"
```
**Expected**: All services "Up", healthy services marked "healthy"

#### 2. MongoDB Event Store Test
```powershell
docker exec aurora-mongodb mongosh --eval "db.runCommand({ping: 1})" --quiet
```
**Expected**: `{ ok: 1 }`

#### 3. Redis Cache Test
```powershell
docker exec aurora-redis redis-cli -a aurora123 --no-auth-warning ping
```
**Expected**: `PONG`

#### 4. Kafka Event Streaming Test
```powershell
docker exec aurora-kafka kafka-topics --bootstrap-server localhost:9092 --list
```
**Expected**: No error messages (empty list or existing topics)

#### 5. Prometheus Metrics Test
```powershell
(Invoke-WebRequest -Uri "http://localhost:9090/api/v1/targets" -UseBasicParsing).StatusCode
```
**Expected**: `200`

#### 6. Grafana Dashboard Test
```powershell
(Invoke-WebRequest -Uri "http://localhost:3000/api/health" -UseBasicParsing).StatusCode
```
**Expected**: `200`

#### 7. Kafka UI Interface Test
```powershell
(Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -UseBasicParsing).StatusCode
```
**Expected**: `200`

### Comprehensive Validation
```powershell
# Execute complete validation script
.\validate-observability.ps1
```

### Manual Access Verification

#### Grafana Dashboard Access
1. Open browser: http://localhost:3000
2. Login: admin / aurora123
3. Verify dashboard loads successfully

#### Prometheus Metrics Access
1. Open browser: http://localhost:9090
2. Navigate to Status > Targets
3. Verify target endpoints are listed

#### Kafka UI Access
1. Open browser: http://localhost:8081
2. Verify cluster information displays
3. Check topics and brokers sections

### Performance Validation

#### Response Time Test
```powershell
# Redis cache latency test
Measure-Command { docker exec aurora-redis redis-cli -a aurora123 --no-auth-warning ping }

# MongoDB response time test
Measure-Command { docker exec aurora-mongodb mongosh --eval "db.runCommand({ping: 1})" --quiet }
```

#### Load Test Simulation
```powershell
# Multiple Redis operations
1..10 | ForEach-Object { 
    docker exec aurora-redis redis-cli -a aurora123 --no-auth-warning set "test$_" "value$_"
    docker exec aurora-redis redis-cli -a aurora123 --no-auth-warning get "test$_"
}

# Cleanup test data
1..10 | ForEach-Object { 
    docker exec aurora-redis redis-cli -a aurora123 --no-auth-warning del "test$_"
}
```

### Troubleshooting Commands

#### Service Logs Inspection
```powershell
# View specific service logs
docker compose logs mongodb --tail=20
docker compose logs redis --tail=20
docker compose logs kafka --tail=20
docker compose logs prometheus --tail=20
docker compose logs grafana --tail=20

# Follow logs in real-time
docker compose logs -f kafka
```

#### Network Connectivity Test
```powershell
# Test internal network connectivity
docker exec aurora-redis ping aurora-mongodb -c 3
docker exec aurora-mongodb ping aurora-kafka -c 3
```

#### Resource Usage Monitoring
```powershell
# Container resource usage
docker stats --no-stream

# Specific container resource usage
docker stats aurora-mongodb aurora-redis aurora-kafka --no-stream
```

### Stack Management Commands

#### Restart Individual Services
```powershell
# Restart specific service
docker compose restart kafka
docker compose restart mongodb
docker compose restart redis

# Restart all services
docker compose restart
```

#### Clean Deployment
```powershell
# Stop and remove all containers
docker compose down

# Remove volumes (WARNING: Data loss)
docker compose down -v

# Clean deployment
docker compose up -d --force-recreate
```

#### Health Check Monitoring
```powershell
# Continuous health monitoring
while ($true) {
    Clear-Host
    Write-Host "Aurora Ledger - Health Status: $(Get-Date)" -ForegroundColor Green
    docker compose ps --format "table {{.Service}}\t{{.Status}}\t{{.Health}}"
    Start-Sleep 5
}
```

### Production Readiness Checklist

- [ ] All containers show "Up" status
- [ ] Health checks return "healthy" for critical services
- [ ] MongoDB ping responds with `{ ok: 1 }`
- [ ] Redis ping responds with `PONG`
- [ ] Kafka topics command executes without errors
- [ ] Prometheus returns HTTP 200 on targets endpoint
- [ ] Grafana returns HTTP 200 on health endpoint
- [ ] Kafka UI returns HTTP 200 on health endpoint
- [ ] All web interfaces accessible via browser
- [ ] Response times under acceptable thresholds
- [ ] No error messages in service logs

### Integration with Spring Boot

Once validated, uncomment the Spring Boot backend in docker-compose.yml:

```yaml
# Uncomment these lines in docker-compose.yml
app-backend:
  build:
    context: ./AuroraLedger
    dockerfile: Dockerfile
  # ... rest of configuration
```

Then redeploy:
```powershell
docker compose up -d
```

This ensures the observability stack is fully operational before integrating the application backend.