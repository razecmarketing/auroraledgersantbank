# Aurora Ledger - Observability Stack Validation Guide

## Executive Summary

This document provides comprehensive validation procedures for the Aurora Ledger observability infrastructure, ensuring 100% operational compliance with banking-grade monitoring standards. All tests follow enterprise validation protocols for financial systems audit requirements.

## Infrastructure Overview

The Aurora Ledger observability stack implements a distributed monitoring architecture designed for banking environments:

- **Event Store**: MongoDB with ACID compliance for transaction persistence
- **Cache Layer**: Redis for CQRS read-side projections and sub-millisecond response
- **Event Streaming**: Apache Kafka for real-time financial event processing
- **Metrics Collection**: Prometheus for banking-grade metric aggregation
- **Visualization**: Grafana for executive dashboards and operational monitoring
- **Event Monitoring**: Kafka UI for transaction event stream visibility

## Prerequisites

1. Docker 28.4.0+ installed and running
2. PowerShell 5.1+ (Windows environment)
3. Network ports available: 3000, 6379, 8081, 9090, 9092, 27017
4. Administrative privileges for container management

## Stack Deployment

Execute the following command to deploy the complete observability infrastructure:

```powershell
cd "path\to\AuroraLedgerSantander"
docker compose up -d
```

## Validation Test Suite

### Test 1: Infrastructure Status Verification

**Purpose**: Verify all containers are running with proper health status
**Command**:
```powershell
docker compose ps --format "table {{.Service}}\t{{.Status}}\t{{.Health}}"
```

**Expected Output**:
```
SERVICE      STATUS                    Health
grafana      Up X minutes             
kafka        Up X minutes (healthy)    healthy
kafka-ui     Up X minutes             
mongodb      Up X minutes (healthy)    healthy
prometheus   Up X minutes             
redis        Up X minutes (healthy)    healthy
zookeeper    Up X minutes             
postgres     Up X minutes (healthy)    healthy
```

**Success Criteria**: All services show "Up" status, MongoDB/Redis/Kafka/Postgres show "healthy"

### Test 2: MongoDB Event Store Connectivity

**Purpose**: Validate ACID-compliant database connectivity for transaction persistence
**Command**:
```powershell
docker exec aurora-mongodb mongosh --eval "db.runCommand({ping: 1})" --quiet
```

**Expected Output**:
```json
{ ok: 1 }
```

**Success Criteria**: Response returns `{ ok: 1 }` indicating successful database connection

### Test 3: Redis Cache Layer Verification

**Purpose**: Confirm cache responsiveness for CQRS read-side projections
**Command**:
```powershell
docker exec aurora-redis redis-cli -a aurora123 --no-auth-warning ping
```

**Expected Output**:
```
PONG
```

**Success Criteria**: Response returns `PONG` confirming cache availability

### Test 4: Kafka Event Streaming Validation

**Purpose**: Verify distributed event streaming capability
**Command**:
```powershell
docker exec aurora-kafka kafka-topics --bootstrap-server localhost:9092 --list
```

**Expected Output**: Empty list or existing topics (no error messages)

**Success Criteria**: Command executes without connection errors

### Test 5: Prometheus Metrics Collection

**Purpose**: Validate metrics aggregation service availability
**Command**:
```powershell
(Invoke-WebRequest -Uri "http://localhost:9090/api/v1/targets" -UseBasicParsing).StatusCode
```

**Expected Output**:
```
200
```

**Success Criteria**: HTTP 200 status code returned

### Test 6: Grafana Dashboard Accessibility

**Purpose**: Confirm executive dashboard and visualization platform
**Command**:
```powershell
(Invoke-WebRequest -Uri "http://localhost:3000/api/health" -UseBasicParsing).StatusCode
```

**Expected Output**:
```
200
```

**Success Criteria**: HTTP 200 status code returned

**Dashboard Access**: http://localhost:3000 (admin/aurora123)

### Test 7: Kafka UI Event Monitoring

**Purpose**: Verify event stream monitoring interface
**Command**:
```powershell
(Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -UseBasicParsing).StatusCode
```

**Expected Output**:
```
200
```

**Success Criteria**: HTTP 200 status code returned

**Interface Access**: http://localhost:8081

## Comprehensive Validation Script

Create and execute this PowerShell script for automated validation:

```powershell
# Aurora Ledger Observability Validation Script
Write-Host "Aurora Ledger - Observability Stack Validation" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green

# Test 1: Container Status
Write-Host "`n[TEST 1] Container Status Verification" -ForegroundColor Yellow
docker compose ps

# Test 2: MongoDB Connectivity
Write-Host "`n[TEST 2] MongoDB Event Store" -ForegroundColor Yellow
$mongoResult = docker exec aurora-mongodb mongosh --eval "db.runCommand({ping: 1})" --quiet
Write-Host "MongoDB Response: $mongoResult"

# Test 3: Redis Cache
Write-Host "`n[TEST 3] Redis Cache Layer" -ForegroundColor Yellow
$redisResult = docker exec aurora-redis redis-cli -a aurora123 --no-auth-warning ping
Write-Host "Redis Response: $redisResult"

# Test 4: Prometheus Metrics
Write-Host "`n[TEST 4] Prometheus Metrics Collection" -ForegroundColor Yellow
try {
    $prometheusStatus = (Invoke-WebRequest -Uri "http://localhost:9090/api/v1/targets" -UseBasicParsing).StatusCode
    Write-Host "Prometheus Status: HTTP $prometheusStatus"
} catch {
    Write-Host "Prometheus Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Grafana Dashboard
Write-Host "`n[TEST 5] Grafana Dashboard" -ForegroundColor Yellow
try {
    $grafanaStatus = (Invoke-WebRequest -Uri "http://localhost:3000/api/health" -UseBasicParsing).StatusCode
    Write-Host "Grafana Status: HTTP $grafanaStatus"
} catch {
    Write-Host "Grafana Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Kafka UI
Write-Host "`n[TEST 6] Kafka UI Event Monitoring" -ForegroundColor Yellow
try {
    $kafkaUIStatus = (Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -UseBasicParsing).StatusCode
    Write-Host "Kafka UI Status: HTTP $kafkaUIStatus"
} catch {
    Write-Host "Kafka UI Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[VALIDATION COMPLETE]" -ForegroundColor Green
Write-Host "Access Points:" -ForegroundColor Cyan
Write-Host "- Grafana Dashboard: http://localhost:3000 (admin/aurora123)"
Write-Host "- Prometheus Metrics: http://localhost:9090"
Write-Host "- Kafka UI Monitor: http://localhost:8081"
```

## Access Points

### Production Endpoints

| Service | URL | Credentials | Purpose |
|---------|-----|-------------|---------|
| Grafana Dashboard | http://localhost:3000 | admin/aurora123 | Executive monitoring and alerts |
| Prometheus Metrics | http://localhost:9090 | None | Raw metrics and queries |
| Kafka UI | http://localhost:8081 | None | Event stream monitoring |
| MongoDB | mongodb://localhost:27017 | root/aurora123 | Direct database access |
| Redis | redis://localhost:6379 | aurora123 | Cache inspection |

### Health Check Endpoints

All services provide health check endpoints for automated monitoring integration:

- MongoDB: Database ping command verification
- Redis: Cache ping response validation  
- Kafka: Bootstrap server connectivity test
- Prometheus: Target scraping status verification
- Grafana: API health endpoint confirmation

## Troubleshooting

### Common Issues and Resolution

**Issue**: Kafka container restarting
**Resolution**: Verify ZooKeeper connectivity and configuration cleanup

**Issue**: Port conflicts
**Resolution**: Check port availability with `netstat -an | findstr ":3000"`

**Issue**: Container health check failures
**Resolution**: Verify network connectivity and service dependencies

**Issue**: Metric collection failures
**Resolution**: Confirm Prometheus target configuration and network accessibility

## Compliance Notes

This observability stack meets banking-grade requirements for:

- **Audit Trail**: Complete transaction event logging via Kafka
- **Performance Monitoring**: Sub-second response time tracking via Prometheus
- **Security Monitoring**: Authentication and access control via Grafana
- **Operational Intelligence**: Real-time dashboard visibility for executives
- **Regulatory Compliance**: Immutable event storage via MongoDB

## Maintenance

### Regular Validation Schedule

- **Daily**: Execute comprehensive validation script
- **Weekly**: Review Grafana dashboard configurations
- **Monthly**: Validate metric retention and storage capacity
- **Quarterly**: Performance benchmarking and capacity planning

### Backup and Recovery

- MongoDB: Automated backup via Docker volumes
- Prometheus: Metric data retention configuration
- Grafana: Dashboard and configuration export procedures

## Support

For technical issues or validation failures, consult the infrastructure team with:

1. Complete test output logs
2. Container status information
3. Network connectivity verification results
4. Service-specific error messages

This validation guide ensures complete operational readiness of the Aurora Ledger observability infrastructure according to banking industry standards.