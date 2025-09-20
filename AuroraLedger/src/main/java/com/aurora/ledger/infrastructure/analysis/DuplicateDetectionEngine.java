package com.aurora.ledger.infrastructure.analysis;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Duplicate Detection Engine
 * Prevents duplicate files, conflicting classes, and naming violations
 * Enforces master programming principles and architectural integrity
 */
@Component
public class DuplicateDetectionEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(DuplicateDetectionEngine.class);
    
    private final Map<String, Set<Path>> classNameRegistry = new ConcurrentHashMap<>();
    private final Map<String, Set<Path>> fileNameRegistry = new ConcurrentHashMap<>();
    private final Map<String, Path> packageRegistry = new ConcurrentHashMap<>();
    private final Set<Path> analyzedFiles = ConcurrentHashMap.newKeySet();
    
    /**
     * Comprehensive duplicate analysis report
     */
    public static class DuplicateAnalysisReport {
        private final Map<String, Set<Path>> duplicateClasses = new HashMap<>();
        private final Map<String, Set<Path>> duplicateFiles = new HashMap<>();
        private final Map<String, Set<Path>> conflictingPackages = new HashMap<>();
        private final Set<String> namingViolations = new HashSet<>();
        private final Set<String> architecturalViolations = new HashSet<>();
        private boolean hasCriticalIssues = false;
        
        public void addDuplicateClass(String className, Set<Path> paths) {
            duplicateClasses.put(className, paths);
            hasCriticalIssues = true;
        }
        
        public void addDuplicateFile(String fileName, Set<Path> paths) {
            duplicateFiles.put(fileName, paths);
            hasCriticalIssues = true;
        }
        
        public void addConflictingPackage(String packageName, Set<Path> paths) {
            conflictingPackages.put(packageName, paths);
            hasCriticalIssues = true;
        }
        
        public void addNamingViolation(String violation) {
            namingViolations.add(violation);
        }
        
        public void addArchitecturalViolation(String violation) {
            architecturalViolations.add(violation);
            hasCriticalIssues = true;
        }
        
        public boolean hasCriticalIssues() { return hasCriticalIssues; }
        public Map<String, Set<Path>> getDuplicateClasses() { return duplicateClasses; }
        public Map<String, Set<Path>> getDuplicateFiles() { return duplicateFiles; }
        public Map<String, Set<Path>> getConflictingPackages() { return conflictingPackages; }
        public Set<String> getNamingViolations() { return namingViolations; }
        public Set<String> getArchitecturalViolations() { return architecturalViolations; }
    }
    
    /**
     * Analyze entire project for duplicates and conflicts
     */
    public DuplicateAnalysisReport analyzeProject(Path projectRoot) {
        logger.info("Starting comprehensive duplicate detection analysis");
        
        DuplicateAnalysisReport report = new DuplicateAnalysisReport();
        
        try {
            // Clear registries for fresh analysis
            clearRegistries();
            
            // Walk through all Java files
            Files.walkFileTree(projectRoot, new DuplicateDetectionVisitor(report));
            
            // Analyze collected data for conflicts
            analyzeForDuplicates(report);
            analyzeNamingConventions(report);
            analyzeArchitecturalViolations(report);
            
            logger.info("Duplicate detection analysis completed. Critical issues: {}", 
                       report.hasCriticalIssues());
            
        } catch (IOException e) {
            logger.error("Failed to analyze project for duplicates", e);
            report.addArchitecturalViolation("Failed to complete duplicate analysis: " + e.getMessage());
        }
        
        return report;
    }
    
    /**
     * Clear all registries for fresh analysis
     */
    private void clearRegistries() {
        classNameRegistry.clear();
        fileNameRegistry.clear();
        packageRegistry.clear();
        analyzedFiles.clear();
    }
    
    /**
     * Analyze registries for duplicate conflicts
     */
    private void analyzeForDuplicates(DuplicateAnalysisReport report) {
        // Check for duplicate class names
        classNameRegistry.entrySet().stream()
            .filter(entry -> entry.getValue().size() > 1)
            .forEach(entry -> {
                report.addDuplicateClass(entry.getKey(), entry.getValue());
                logger.warn("DUPLICATE CLASS DETECTED: {} found in {} locations", 
                           entry.getKey(), entry.getValue().size());
            });
        
        // Check for duplicate file names
        fileNameRegistry.entrySet().stream()
            .filter(entry -> entry.getValue().size() > 1)
            .forEach(entry -> {
                report.addDuplicateFile(entry.getKey(), entry.getValue());
                logger.warn("DUPLICATE FILE DETECTED: {} found in {} locations", 
                           entry.getKey(), entry.getValue().size());
            });
    }
    
    /**
     * Analyze naming conventions violations
     */
    private void analyzeNamingConventions(DuplicateAnalysisReport report) {
        classNameRegistry.keySet().forEach(className -> {
            if (!isValidClassName(className)) {
                report.addNamingViolation("Invalid class name: " + className);
            }
        });
        
        packageRegistry.keySet().forEach(packageName -> {
            if (!isValidPackageName(packageName)) {
                report.addNamingViolation("Invalid package name: " + packageName);
            }
        });
    }
    
    /**
     * Analyze architectural violations
     */
    private void analyzeArchitecturalViolations(DuplicateAnalysisReport report) {
        // Check for Clean Architecture violations
        analyzedFiles.forEach(filePath -> {
            String pathStr = filePath.toString();
            
            // Domain layer should not depend on infrastructure
            if (pathStr.contains("/domain/") && 
                (pathStr.contains("Repository") || pathStr.contains("Controller") || pathStr.contains("Config"))) {
                report.addArchitecturalViolation("Domain layer violation: " + filePath);
            }
            
            // Application layer should not depend on presentation
            if (pathStr.contains("/application/") && pathStr.contains("Controller")) {
                report.addArchitecturalViolation("Application layer violation: " + filePath);
            }
        });
    }
    
    /**
     * Validate Java class name conventions
     */
    private boolean isValidClassName(String className) {
        return className.matches("^[A-Z][a-zA-Z0-9]*$") && 
               !className.contains("_") && 
               !className.toLowerCase().equals(className);
    }
    
    /**
     * Validate Java package name conventions
     */
    private boolean isValidPackageName(String packageName) {
        // Simple validation without regex catastrophic backtracking
        if (packageName == null || packageName.isEmpty()) {
            return false;
        }
        
        String[] parts = packageName.split("\\.");
        for (String part : parts) {
            if (!part.matches("^[a-z][a-z0-9]*$")) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * File visitor for duplicate detection
     */
    private class DuplicateDetectionVisitor extends SimpleFileVisitor<Path> {
        private final DuplicateAnalysisReport report;
        
        public DuplicateDetectionVisitor(DuplicateAnalysisReport report) {
            this.report = report;
        }
        
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (file.toString().endsWith(".java")) {
                analyzedFiles.add(file);
                
                String fileName = file.getFileName().toString();
                String className = fileName.replace(".java", "");
                
                // Register file name
                fileNameRegistry.computeIfAbsent(fileName, k -> ConcurrentHashMap.newKeySet()).add(file);
                
                // Register class name
                classNameRegistry.computeIfAbsent(className, k -> ConcurrentHashMap.newKeySet()).add(file);
                
                // Extract and register package
                try {
                    String packageName = extractPackageName(file);
                    if (packageName != null) {
                        Path existingPath = packageRegistry.get(packageName);
                        if (existingPath != null && !existingPath.getParent().equals(file.getParent())) {
                            report.addConflictingPackage(packageName, 
                                Set.of(existingPath.getParent(), file.getParent()));
                        } else {
                            packageRegistry.put(packageName, file);
                        }
                    }
                } catch (IOException e) {
                    logger.warn("Could not extract package from file: {}", file);
                }
            }
            
            return FileVisitResult.CONTINUE;
        }
        
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            logger.warn("Failed to visit file: {}", file, exc);
            return FileVisitResult.CONTINUE;
        }
        
        /**
         * Extract package name from Java file
         */
        private String extractPackageName(Path javaFile) throws IOException {
            try (var lines = Files.lines(javaFile)) {
                return lines
                    .filter(line -> line.trim().startsWith("package "))
                    .findFirst()
                    .map(line -> line.trim()
                        .replace("package ", "")
                        .replace(";", "")
                        .trim())
                    .orElse(null);
            }
        }
    }
    
    /**
     * Generate comprehensive report string
     */
    public String generateReport(DuplicateAnalysisReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("DUPLICATE DETECTION ANALYSIS REPORT\n");
        sb.append("=====================================\n\n");
        
        if (!report.hasCriticalIssues()) {
            sb.append("PROJECT STATUS: CLEAN\n");
            sb.append("No duplicates, conflicts, or violations detected.\n");
            sb.append("All master programming principles followed.\n\n");
        } else {
            sb.append("PROJECT STATUS: CRITICAL ISSUES DETECTED\n");
            sb.append("Immediate attention required!\n\n");
            
            // Duplicate classes
            if (!report.getDuplicateClasses().isEmpty()) {
                sb.append("DUPLICATE CLASSES (").append(report.getDuplicateClasses().size()).append("):\n");
                report.getDuplicateClasses().forEach((className, paths) -> {
                    sb.append("  CLASS: ").append(className).append("\n");
                    paths.forEach(path -> sb.append("    LOCATION: ").append(path).append("\n"));
                });
                sb.append("\n");
            }
            
            // Duplicate files
            if (!report.getDuplicateFiles().isEmpty()) {
                sb.append("DUPLICATE FILES (").append(report.getDuplicateFiles().size()).append("):\n");
                report.getDuplicateFiles().forEach((fileName, paths) -> {
                    sb.append("  FILE: ").append(fileName).append("\n");
                    paths.forEach(path -> sb.append("    LOCATION: ").append(path).append("\n"));
                });
                sb.append("\n");
            }
            
            // Conflicting packages
            if (!report.getConflictingPackages().isEmpty()) {
                sb.append("CONFLICTING PACKAGES (").append(report.getConflictingPackages().size()).append("):\n");
                report.getConflictingPackages().forEach((packageName, paths) -> {
                    sb.append("  PACKAGE: ").append(packageName).append("\n");
                    paths.forEach(path -> sb.append("    CONFLICT: ").append(path).append("\n"));
                });
                sb.append("\n");
            }
            
            // Naming violations
            if (!report.getNamingViolations().isEmpty()) {
                sb.append("NAMING VIOLATIONS (").append(report.getNamingViolations().size()).append("):\n");
                report.getNamingViolations().forEach(violation -> 
                    sb.append("  VIOLATION: ").append(violation).append("\n"));
                sb.append("\n");
            }
            
            // Architectural violations
            if (!report.getArchitecturalViolations().isEmpty()) {
                sb.append("ARCHITECTURAL VIOLATIONS (").append(report.getArchitecturalViolations().size()).append("):\n");
                report.getArchitecturalViolations().forEach(violation -> 
                    sb.append("  VIOLATION: ").append(violation).append("\n"));
                sb.append("\n");
            }
        }
        
        sb.append("Analysis completed at: ").append(new Date()).append("\n");
        return sb.toString();
    }
}
