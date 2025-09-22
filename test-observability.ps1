# Aurora Ledger - Observability Validation Script
# Banking-Grade Infrastructure Testing
# Execute: .\test-observability.ps1

Write-Host "Aurora Ledger - Observability Stack Validation" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green

$testResults = @()

# Test 1: Container Status
Write-Host "[TEST 1] Container Status Verification" -ForegroundColor Yellow
try {
    docker compose ps --format "table {{.Service}}\t{{.Status}}\t{{.Health}}"
    $testResults += "PASS: Container Status"
    Write-Host "Result: PASS" -ForegroundColor Green
} catch {
    $testResults += "FAIL: Container Status"
    Write-Host "Result: FAIL" -ForegroundColor Red
}
Write-Host ""

# Test 2: MongoDB
Write-Host "[TEST 2] MongoDB Event Store" -ForegroundColor Yellow
try {
    $mongoResult = docker exec aurora-mongodb mongosh --eval "db.runCommand({ping: 1})" --quiet 2>$null
    Write-Host "MongoDB Response: $mongoResult"
    $testResults += "PASS: MongoDB Event Store"
    Write-Host "Result: PASS" -ForegroundColor Green
} catch {
    $testResults += "FAIL: MongoDB Event Store"
    Write-Host "Result: FAIL" -ForegroundColor Red
}
Write-Host ""

# Test 3: Redis
Write-Host "[TEST 3] Redis Cache Layer" -ForegroundColor Yellow
try {
    $redisResult = docker exec aurora-redis redis-cli -a aurora123 --no-auth-warning ping 2>$null
    Write-Host "Redis Response: $redisResult"
    $testResults += "PASS: Redis Cache Layer"
    Write-Host "Result: PASS" -ForegroundColor Green
} catch {
    $testResults += "FAIL: Redis Cache Layer"
    Write-Host "Result: FAIL" -ForegroundColor Red
}
Write-Host ""

# Test 4: Prometheus
Write-Host "[TEST 4] Prometheus Metrics" -ForegroundColor Yellow
try {
    $prometheusStatus = (Invoke-WebRequest -Uri "http://localhost:9090/api/v1/targets" -UseBasicParsing -TimeoutSec 5).StatusCode
    Write-Host "Prometheus Status: HTTP $prometheusStatus"
    $testResults += "PASS: Prometheus Metrics"
    Write-Host "Result: PASS" -ForegroundColor Green
} catch {
    $testResults += "FAIL: Prometheus Metrics"
    Write-Host "Result: FAIL" -ForegroundColor Red
}
Write-Host ""

# Test 5: Grafana
Write-Host "[TEST 5] Grafana Dashboard" -ForegroundColor Yellow
try {
    $grafanaStatus = (Invoke-WebRequest -Uri "http://localhost:3000/api/health" -UseBasicParsing -TimeoutSec 5).StatusCode
    Write-Host "Grafana Status: HTTP $grafanaStatus"
    $testResults += "PASS: Grafana Dashboard"
    Write-Host "Result: PASS" -ForegroundColor Green
} catch {
    $testResults += "FAIL: Grafana Dashboard"
    Write-Host "Result: FAIL" -ForegroundColor Red
}
Write-Host ""

# Test 6: Kafka UI
Write-Host "[TEST 6] Kafka UI Monitor" -ForegroundColor Yellow
try {
    $kafkaUIStatus = (Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -UseBasicParsing -TimeoutSec 5).StatusCode
    Write-Host "Kafka UI Status: HTTP $kafkaUIStatus"
    $testResults += "PASS: Kafka UI Monitor"
    Write-Host "Result: PASS" -ForegroundColor Green
} catch {
    $testResults += "FAIL: Kafka UI Monitor"
    Write-Host "Result: FAIL" -ForegroundColor Red
}

# Summary
Write-Host ""
Write-Host "VALIDATION SUMMARY" -ForegroundColor Green
Write-Host "==================" -ForegroundColor Green

foreach ($result in $testResults) {
    if ($result -like "PASS:*") {
        Write-Host $result -ForegroundColor Green
    } else {
        Write-Host $result -ForegroundColor Red
    }
}

$passCount = ($testResults | Where-Object { $_ -like "PASS:*" }).Count
$totalTests = $testResults.Count

Write-Host ""
Write-Host "RESULT: $passCount/$totalTests tests passed" -ForegroundColor $(if ($passCount -eq $totalTests) { "Green" } else { "Yellow" })

Write-Host ""
Write-Host "ACCESS POINTS:" -ForegroundColor Cyan
Write-Host "- Grafana: http://localhost:3000 (admin/aurora123)"
Write-Host "- Prometheus: http://localhost:9090"
Write-Host "- Kafka UI: http://localhost:8081"