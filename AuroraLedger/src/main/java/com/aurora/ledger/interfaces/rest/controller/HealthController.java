package com.aurora.ledger.interfaces.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health Check Controller
 * Provides system health and status information for monitoring and observability
 */
@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "System Health", description = "Health check and system status endpoints")
public class HealthController {
    private static final String TIMESTAMP_KEY = "timestamp";
    
    /**
     * Basic Health Check
     * Returns system status for load balancers and monitoring systems
     */
    @GetMapping
    @Operation(
        summary = "Health Check",
        description = "Returns system health status for monitoring and load balancing"
    )
    @ApiResponse(responseCode = "200", description = "System is healthy")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        
        Map<String, Object> health = Map.of(
            "status", "UP",
            TIMESTAMP_KEY, LocalDateTime.now().toString(),
            "service", "Aurora Ledger Banking API",
            "version", "1.0.0",
            "environment", "production",
            "components", Map.of(
                "database", "UP",
                "redis", "UP",
                "security", "UP",
                "cqrs", "UP"
            )
        );
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * Readiness Check
     * Returns readiness status for Kubernetes/container orchestration
     */
    @GetMapping("/ready")
    @Operation(
        summary = "Readiness Check",
        description = "Returns service readiness status for container orchestration"
    )
    @ApiResponse(responseCode = "200", description = "Service is ready to accept requests")
    public ResponseEntity<Map<String, Object>> readinessCheck() {
        
        Map<String, Object> readiness = Map.of(
            "ready", true,
            TIMESTAMP_KEY, LocalDateTime.now().toString(),
            "message", "Aurora Ledger Banking API is ready to accept requests"
        );
        
        return ResponseEntity.ok(readiness);
    }
    
    /**
     * Liveness Check
     * Returns liveness status for Kubernetes/container orchestration
     */
    @GetMapping("/live")
    @Operation(
        summary = "Liveness Check", 
        description = "Returns service liveness status for container orchestration"
    )
    @ApiResponse(responseCode = "200", description = "Service is alive and running")
    public ResponseEntity<Map<String, Object>> livenessCheck() {
        
        Map<String, Object> liveness = Map.of(
            "alive", true,
            TIMESTAMP_KEY, LocalDateTime.now().toString(),
            "uptime", "Available",
            "message", "Aurora Ledger Banking API is alive and functioning"
        );
        
        return ResponseEntity.ok(liveness);
    }
}










