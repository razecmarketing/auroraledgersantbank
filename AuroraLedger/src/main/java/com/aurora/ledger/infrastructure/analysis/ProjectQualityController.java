package com.aurora.ledger.infrastructure.analysis;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Project Quality Controller
 * REST API for project integrity and duplicate detection
 */
@RestController
@RequestMapping("/api/quality")
public class ProjectQualityController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectQualityController.class);
    private static final String USER_DIR_PROPERTY = "user.dir";
    private static final String SUCCESS_KEY = "success";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String MESSAGE_KEY = "message";
    private static final String HAS_CRITICAL_ISSUES_KEY = "hasCriticalIssues";
    
    private final DuplicateDetectionEngine duplicateDetectionEngine;
    
    public ProjectQualityController(DuplicateDetectionEngine duplicateDetectionEngine) {
        this.duplicateDetectionEngine = duplicateDetectionEngine;
    }
    
    /**
     * Run comprehensive project analysis
     */
    @GetMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeProject() {
        logger.info("Project Quality Analysis - Starting comprehensive analysis");
        
        try {
            String projectRoot = System.getProperty(USER_DIR_PROPERTY);
            
            // Run duplicate detection
            var duplicateReport = duplicateDetectionEngine.analyzeProject(Paths.get(projectRoot));
            
            // Generate response
            Map<String, Object> response = new HashMap<>();
            response.put(SUCCESS_KEY, true);
            response.put(TIMESTAMP_KEY, java.time.LocalDateTime.now());
            response.put("projectRoot", projectRoot);
            
            // Duplicate analysis results
            Map<String, Object> duplicateAnalysis = new HashMap<>();
            duplicateAnalysis.put(HAS_CRITICAL_ISSUES_KEY, duplicateReport.hasCriticalIssues());
            duplicateAnalysis.put("duplicateClasses", duplicateReport.getDuplicateClasses().size());
            duplicateAnalysis.put("duplicateFiles", duplicateReport.getDuplicateFiles().size());
            duplicateAnalysis.put("namingViolations", duplicateReport.getNamingViolations().size());
            duplicateAnalysis.put("architecturalViolations", duplicateReport.getArchitecturalViolations().size());
            response.put("duplicateAnalysis", duplicateAnalysis);
            
            // Overall status
            boolean overallSuccess = !duplicateReport.hasCriticalIssues();
            response.put("overallStatus", overallSuccess ? "CLEAN" : "ISSUES_DETECTED");
            
            if (!overallSuccess) {
                logger.error("Project Quality Analysis - CRITICAL ISSUES DETECTED!");
                if (logger.isErrorEnabled()) {
                    logger.error("Duplicate Report:\n{}", duplicateDetectionEngine.generateReport(duplicateReport));
                }
            } else {
                logger.info("Project Quality Analysis - All checks passed successfully!");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Project quality analysis failed", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(SUCCESS_KEY, false);
            errorResponse.put(MESSAGE_KEY, "Analysis failed: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Get detailed duplicate detection report
     */
    @GetMapping("/duplicates")
    public ResponseEntity<Map<String, Object>> getDuplicateReport() {
        logger.info("Generating detailed duplicate detection report");
        
        try {
            String projectRoot = System.getProperty(USER_DIR_PROPERTY);
            var duplicateReport = duplicateDetectionEngine.analyzeProject(Paths.get(projectRoot));
            
            Map<String, Object> response = new HashMap<>();
            response.put(SUCCESS_KEY, true);
            response.put(TIMESTAMP_KEY, java.time.LocalDateTime.now());
            response.put("report", duplicateDetectionEngine.generateReport(duplicateReport));
            response.put(HAS_CRITICAL_ISSUES_KEY, duplicateReport.hasCriticalIssues());
            response.put("duplicateClasses", duplicateReport.getDuplicateClasses());
            response.put("duplicateFiles", duplicateReport.getDuplicateFiles());
            response.put("conflictingPackages", duplicateReport.getConflictingPackages());
            response.put("namingViolations", duplicateReport.getNamingViolations());
            response.put("architecturalViolations", duplicateReport.getArchitecturalViolations());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to generate duplicate report", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(SUCCESS_KEY, false);
            errorResponse.put(MESSAGE_KEY, "Report generation failed: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Get project health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getProjectHealth() {
        logger.info("Checking project health status");
        
        try {
            String projectRoot = System.getProperty(USER_DIR_PROPERTY);
            var duplicateReport = duplicateDetectionEngine.analyzeProject(Paths.get(projectRoot));
            
            Map<String, Object> response = new HashMap<>();
            response.put(SUCCESS_KEY, true);
            response.put(TIMESTAMP_KEY, java.time.LocalDateTime.now());
            response.put("status", duplicateReport.hasCriticalIssues() ? "UNHEALTHY" : "HEALTHY");
            response.put(HAS_CRITICAL_ISSUES_KEY, duplicateReport.hasCriticalIssues());
            response.put("issues", duplicateReport.getDuplicateClasses().size() + 
                                  duplicateReport.getDuplicateFiles().size() +
                                  duplicateReport.getArchitecturalViolations().size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to check project health", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(SUCCESS_KEY, false);
            errorResponse.put(MESSAGE_KEY, "Health check failed: " + e.getMessage());
            errorResponse.put("status", "ERROR");
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
