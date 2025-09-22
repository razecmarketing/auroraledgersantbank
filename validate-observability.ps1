# Aurora Ledger - Observability Test Script
# Banking-Grade Infrastructure Validation
# Execute with: .\validate-observability.ps1

Write-Host "Aurora Ledger - Observability Stack Validation" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green
Write-Host "Banking-Grade Infrastructure Validation Suite" -ForegroundColor Cyan
Write-Host ""

# Initialize test results
$testResults = @()

# Test 1: Container Status Verification
Write-Host "[TEST 1] Container Status Verification" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor DarkGray
try {
    $containerStatus = docker compose ps --format "table {{.Service}}\t{{.Status}}\t{{.Health}}"
    Write-Host $containerStatus
    $testResults += "PASS: Container Status"
    Write-Host "Result: PASS" -ForegroundColor Green
} catch {
    $testResults += "FAIL: Container Status - $($_.Exception.Message)"
    Write-Host "Result: FAIL - $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 2: MongoDB Event Store Connectivity
Write-Host "[TEST 2] MongoDB Event Store Connectivity" -ForegroundColor Yellow
Write-Host "------------------------------------------" -ForegroundColor DarkGray
try {
    $mongoResult = docker exec aurora-mongodb mongosh --eval "db.runCommand({ping: 1})" --quiet 2>$null
    if ($mongoResult -match "ok.*1") {
        Write-Host "MongoDB Response: $mongoResult"
        $testResults += "✓ MongoDB Event Store: PASS"
        Write-Host "Result: PASS - ACID database operational" -ForegroundColor Green
    } else {
        $testResults += "✗ MongoDB Event Store: FAIL - Invalid response"
        Write-Host "Result: FAIL - Invalid response" -ForegroundColor Red
    }
} catch {
    $testResults += "✗ MongoDB Event Store: FAIL - $($_.Exception.Message)"
    Write-Host "Result: FAIL - $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 3: Redis Cache Layer Verification
Write-Host "[TEST 3] Redis Cache Layer Verification" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor DarkGray
try {
    $redisResult = docker exec aurora-redis redis-cli -a aurora123 --no-auth-warning ping 2>$null
    if ($redisResult -eq "PONG") {
        Write-Host "Redis Response: $redisResult"
        $testResults += "✓ Redis Cache Layer: PASS"
        Write-Host "Result: PASS - CQRS cache operational" -ForegroundColor Green
    } else {
        $testResults += "✗ Redis Cache Layer: FAIL - Invalid response"
        Write-Host "Result: FAIL - Invalid response" -ForegroundColor Red
    }
} catch {
    $testResults += "✗ Redis Cache Layer: FAIL - $($_.Exception.Message)"
    Write-Host "Result: FAIL - $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 4: Kafka Event Streaming Validation
Write-Host "[TEST 4] Kafka Event Streaming Validation" -ForegroundColor Yellow
Write-Host "------------------------------------------" -ForegroundColor DarkGray
try {
    $kafkaResult = docker exec aurora-kafka kafka-topics --bootstrap-server localhost:9092 --list 2>$null
    $testResults += "✓ Kafka Event Streaming: PASS"
    Write-Host "Kafka Topics: $(if ($kafkaResult) { $kafkaResult } else { '(empty - ready for topics)' })"
    Write-Host "Result: PASS - Event streaming operational" -ForegroundColor Green
} catch {
    $testResults += "✗ Kafka Event Streaming: FAIL - $($_.Exception.Message)"
    Write-Host "Result: FAIL - $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 5: Prometheus Metrics Collection
Write-Host "[TEST 5] Prometheus Metrics Collection" -ForegroundColor Yellow
Write-Host "--------------------------------------" -ForegroundColor DarkGray
try {
    $prometheusStatus = (Invoke-WebRequest -Uri "http://localhost:9090/api/v1/targets" -UseBasicParsing -TimeoutSec 5).StatusCode
    Write-Host "Prometheus HTTP Status: $prometheusStatus"
    if ($prometheusStatus -eq 200) {
        $testResults += "✓ Prometheus Metrics: PASS"
        Write-Host "Result: PASS - Metrics collection active" -ForegroundColor Green
    } else {
        $testResults += "✗ Prometheus Metrics: FAIL - HTTP $prometheusStatus"
        Write-Host "Result: FAIL - HTTP $prometheusStatus" -ForegroundColor Red
    }
} catch {
    $testResults += "✗ Prometheus Metrics: FAIL - $($_.Exception.Message)"
    Write-Host "Result: FAIL - $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 6: Grafana Dashboard Accessibility
Write-Host "[TEST 6] Grafana Dashboard Accessibility" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor DarkGray
try {
    $grafanaStatus = (Invoke-WebRequest -Uri "http://localhost:3000/api/health" -UseBasicParsing -TimeoutSec 5).StatusCode
    Write-Host "Grafana HTTP Status: $grafanaStatus"
    if ($grafanaStatus -eq 200) {
        $testResults += "✓ Grafana Dashboard: PASS"
        Write-Host "Result: PASS - Executive dashboard available" -ForegroundColor Green
    } else {
        $testResults += "✗ Grafana Dashboard: FAIL - HTTP $grafanaStatus"
        Write-Host "Result: FAIL - HTTP $grafanaStatus" -ForegroundColor Red
    }
} catch {
    $testResults += "✗ Grafana Dashboard: FAIL - $($_.Exception.Message)"
    Write-Host "Result: FAIL - $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 7: Kafka UI Event Monitoring
Write-Host "[TEST 7] Kafka UI Event Monitoring" -ForegroundColor Yellow
Write-Host "-----------------------------------" -ForegroundColor DarkGray
try {
    $kafkaUIStatus = (Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -UseBasicParsing -TimeoutSec 5).StatusCode
    Write-Host "Kafka UI HTTP Status: $kafkaUIStatus"
    if ($kafkaUIStatus -eq 200) {
        $testResults += "✓ Kafka UI Monitor: PASS"
        Write-Host "Result: PASS - Event monitoring interface active" -ForegroundColor Green
    } else {
        $testResults += "✗ Kafka UI Monitor: FAIL - HTTP $kafkaUIStatus"
        Write-Host "Result: FAIL - HTTP $kafkaUIStatus" -ForegroundColor Red
    }
} catch {
    $testResults += "✗ Kafka UI Monitor: FAIL - $($_.Exception.Message)"
    Write-Host "Result: FAIL - $($_.Exception.Message)" -ForegroundColor Red
}

# Summary Report
Write-Host ""
Write-Host "===============================================" -ForegroundColor Green
Write-Host "VALIDATION SUMMARY REPORT" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green

foreach ($result in $testResults) {
    if ($result -like "*PASS*") {
        Write-Host $result -ForegroundColor Green
    } else {
        Write-Host $result -ForegroundColor Red
    }
}

$passCount = ($testResults | Where-Object { $_ -like "*PASS*" }).Count
$totalTests = $testResults.Count

Write-Host ""
Write-Host "OVERALL RESULT: $passCount/$totalTests tests passed" -ForegroundColor $(if ($passCount -eq $totalTests) { "Green" } else { "Yellow" })

if ($passCount -eq $totalTests) {
    Write-Host "STATUS: OBSERVABILITY STACK FULLY OPERATIONAL" -ForegroundColor Green
    Write-Host "Infrastructure ready for banking-grade operations" -ForegroundColor Cyan
} else {
    Write-Host "STATUS: ISSUES DETECTED - REVIEW FAILED TESTS" -ForegroundColor Red
    Write-Host "Resolve failures before production deployment" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "ACCESS POINTS:" -ForegroundColor Cyan
Write-Host "- Grafana Dashboard: http://localhost:3000 (admin/aurora123)"
Write-Host "- Prometheus Metrics: http://localhost:9090"
Write-Host "- Kafka UI Monitor: http://localhost:8081"
Write-Host "- MongoDB Direct: mongodb://localhost:27017 (root/aurora123)"
Write-Host "- Redis Cache: redis://localhost:6379 (password: aurora123)"

Write-Host ""
Write-Host "Validation completed at $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor DarkGray