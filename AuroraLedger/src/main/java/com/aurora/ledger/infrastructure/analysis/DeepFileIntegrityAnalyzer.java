package com.aurora.ledger.infrastructure.analysis;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * Aurora File System Deep Analysis Engine
 * Performs comprehensive analysis of all project files to detect:
 *  Empty files
 *  Malformed Java classes
 *  Incomplete implementations
 *  Missing critical content
 */
@Component
public class DeepFileIntegrityAnalyzer {
    
    private static final Logger logger = LoggerFactory.getLogger(DeepFileIntegrityAnalyzer.class);
    
    private final Set<String> suspiciousPatterns = Set.of(
        "TODO", "FIXME", "XXX", "HACK", "temporary", "placeholder"
    );

    public FileIntegrityReport analyzeProject(String projectPath) {
        logger.info("Starting deep file integrity analysis for: {}", projectPath);
        
        FileIntegrityReport report = new FileIntegrityReport();
        Path rootPath = Paths.get(projectPath);
        
        try {
            Files.walkFileTree(rootPath, new FileIntegrityVisitor(report));
        } catch (IOException e) {
            logger.error("Failed to analyze project structure", e);
            report.addCriticalError("Failed to walk project tree: " + e.getMessage());
        }
        
        return report;
    }
    
    private class FileIntegrityVisitor extends SimpleFileVisitor<Path> {
        private final FileIntegrityReport report;
        
        public FileIntegrityVisitor(FileIntegrityReport report) {
            this.report = report;
        }
        
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            String fileName = file.getFileName().toString();
            
            // Skip system files and build artifacts
            if (shouldSkipFile(fileName, file)) {
                return FileVisitResult.CONTINUE;
            }
            
            analyzeFile(file);
            return FileVisitResult.CONTINUE;
        }
        
        private boolean shouldSkipFile(String fileName, Path file) {
            String pathStr = file.toString().toLowerCase();
            
            return fileName.startsWith(".") ||
                   fileName.endsWith(".class") ||
                   fileName.endsWith(".jar") ||
                   fileName.endsWith(".war") ||
                   pathStr.contains("target" + File.separator) ||
                   pathStr.contains(".git" + File.separator) ||
                   false; // Backend-only scope: no client-side artifacts expected
        }

        private void analyzeFile(Path file) {
            try {
                String content = Files.readString(file);
                String fileName = file.getFileName().toString();
                
                // Check if file is empty
                if (content.trim().isEmpty()) {
                    report.addEmptyFile(file.toString());
                    logger.warn("Empty file detected: {}", file);
                    return;
                }
                
                // Analyze Java files specifically
                if (fileName.endsWith(".java")) {
                    analyzeJavaFile(file, content);
                }
                
                // Analyze configuration files
                if (isConfigFile(fileName)) {
                    analyzeConfigFile(file, content);
                }
                
                // Check for suspicious patterns
                checkSuspiciousPatterns(file, content);
                
                report.incrementAnalyzedFiles();
                
            } catch (IOException e) {
                report.addUnreadableFile(file.toString(), e.getMessage());
                logger.error("Cannot read file: {}", file, e);
            }
        }

        private void analyzeJavaFile(Path file, String content) {
            String fileName = file.getFileName().toString();
            
            // Check for basic Java structure
            boolean hasClassDeclaration = content.contains("class ") || 
                                         content.contains("interface ") || 
                                         content.contains("enum ");
            
            if (!hasClassDeclaration && !fileName.equals("packageinfo.java")) {
                report.addMalformedFile(file.toString(), "Missing class/interface/enum declaration");
                logger.warn("Java file without proper declaration: {}", file);
            }
            
            // Check for package declaration
            if (!content.contains("package ") && !fileName.equals("moduleinfo.java")) {
                report.addIncompleteFile(file.toString(), "Missing package declaration");
            }
            
            // Check for methods in classes (excluding interfaces and enums with only constants)
            if (content.contains("class ") && !content.contains("public ") && 
                !content.contains("private ") && !content.contains("protected ")) {
                report.addSuspiciousFile(file.toString(), "Class without any methods or fields");
            }
            
            // Check for proper imports
            boolean hasImports = content.lines()
                .anyMatch(line -> line.trim().startsWith("import "));
                
            // Detect potential stub classes
            if (content.length() < 200 && hasClassDeclaration && !hasImports) {
                report.addSuspiciousFile(file.toString(), "Very small class without imports  possibly incomplete");
            }
        }

        private void analyzeConfigFile(Path file, String content) {
            String fileName = file.getFileName().toString();
            
            if ((fileName.equals("application.yml") || fileName.equals("application.properties"))
                && content.length() < 50) {
                report.addIncompleteFile(file.toString(), "Configuration file seems minimal");
            }
            
            if (fileName.equals("pom.xml") && (!content.contains("<dependencies>") || !content.contains("<build>"))) {
                report.addMalformedFile(file.toString(), "Maven POM missing essential sections");
            }
        }

        private void checkSuspiciousPatterns(Path file, String content) {
            for (String pattern : suspiciousPatterns) {
                if (content.toUpperCase().contains(pattern.toUpperCase())) {
                    report.addSuspiciousFile(file.toString(), "Contains suspicious pattern: " + pattern);
                }
            }
        }

        private boolean isConfigFile(String fileName) {
            return fileName.endsWith(".xml") ||
                   fileName.endsWith(".yml") ||
                   fileName.endsWith(".yaml") ||
                   fileName.endsWith(".properties") ||
                   fileName.endsWith(".json");
        }
    }
    
    public static class FileIntegrityReport {
        private final List<String> emptyFiles = new ArrayList<>();
        private final List<String> malformedFiles = new ArrayList<>();
        private final List<String> incompleteFiles = new ArrayList<>();
        private final List<String> suspiciousFiles = new ArrayList<>();
        private final List<String> unreadableFiles = new ArrayList<>();
        private final List<String> criticalErrors = new ArrayList<>();
        private final Map<String, String> fileIssues = new HashMap<>();
        
        private int totalAnalyzedFiles = 0;
        
        public void addEmptyFile(String path) {
            emptyFiles.add(path);
        }
        
        public void addMalformedFile(String path, String reason) {
            malformedFiles.add(path);
            fileIssues.put(path, reason);
        }
        
        public void addIncompleteFile(String path, String reason) {
            incompleteFiles.add(path);
            fileIssues.put(path, reason);
        }
        
        public void addSuspiciousFile(String path, String reason) {
            suspiciousFiles.add(path);
            fileIssues.put(path, reason);
        }
        
        public void addUnreadableFile(String path, String reason) {
            unreadableFiles.add(path);
            fileIssues.put(path, reason);
        }
        
        public void addCriticalError(String error) {
            criticalErrors.add(error);
        }
        
        public void incrementAnalyzedFiles() {
            totalAnalyzedFiles++;
        }
        
        public boolean hasIssues() {
            return !emptyFiles.isEmpty() || !malformedFiles.isEmpty() || 
                   !incompleteFiles.isEmpty() || !unreadableFiles.isEmpty() ||
                   !criticalErrors.isEmpty();
        }
        
        public boolean hasCriticalIssues() {
            return !emptyFiles.isEmpty() || !malformedFiles.isEmpty() || !criticalErrors.isEmpty();
        }
        
        public String generateDetailedReport() {
            StringBuilder report = new StringBuilder();
            
            report.append("AURORA LEDGER  FILE SYSTEM INTEGRITY ANALYSIS\n");
            report.append("=" .repeat(60)).append("\n");
            report.append(String.format("Total files analyzed: %d%n", totalAnalyzedFiles));
            report.append(String.format("Analysis timestamp: %s%n%n", 
                java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"))));
            
            if (!criticalErrors.isEmpty()) {
                report.append("CRITICAL ERRORS:\n");
                criticalErrors.forEach(error -> report.append("  ERROR: ").append(error).append("\n"));
                report.append("\n");
            }
            
            if (!emptyFiles.isEmpty()) {
                report.append("EMPTY FILES ").append("(").append(emptyFiles.size()).append("):\n");
                emptyFiles.forEach(file -> report.append("  EMPTY: ").append(file).append("\n"));
                report.append("\n");
            }
            
            if (!malformedFiles.isEmpty()) {
                report.append("MALFORMED FILES ").append("(").append(malformedFiles.size()).append("):\n");
                malformedFiles.forEach(file -> report.append("  MALFORMED: ").append(file)
                    .append("  ").append(fileIssues.get(file)).append("\n"));
                report.append("\n");
            }
            
            if (!incompleteFiles.isEmpty()) {
                report.append("INCOMPLETE FILES ").append("(").append(incompleteFiles.size()).append("):\n");
                incompleteFiles.forEach(file -> report.append("  INCOMPLETE: ").append(file)
                    .append("  ").append(fileIssues.get(file)).append("\n"));
                report.append("\n");
            }
            
            if (!suspiciousFiles.isEmpty()) {
                report.append("SUSPICIOUS PATTERNS ").append("(").append(suspiciousFiles.size()).append("):\n");
                suspiciousFiles.forEach(file -> report.append("  SUSPICIOUS: ").append(file)
                    .append("  ").append(fileIssues.get(file)).append("\n"));
                report.append("\n");
            }
            
            if (!unreadableFiles.isEmpty()) {
                report.append("UNREADABLE FILES ").append("(").append(unreadableFiles.size()).append("):\n");
                unreadableFiles.forEach(file -> report.append("  UNREADABLE: ").append(file)
                    .append("  ").append(fileIssues.get(file)).append("\n"));
                report.append("\n");
            }
            
            // Summary
            if (!hasIssues()) {
                report.append("PROJECT INTEGRITY: EXCELLENT\n");
                report.append("All files are properly structured and complete!\n");
            } else if (hasCriticalIssues()) {
                report.append("PROJECT INTEGRITY: CRITICAL ISSUES DETECTED\n");
                report.append("Immediate attention required for critical problems!\n");
            } else {
                report.append("PROJECT INTEGRITY: MINOR ISSUES DETECTED\n");
                report.append("Some improvements recommended but not critical.\n");
            }
            
            return report.toString();
        }
        
        // Getters
        public List<String> getEmptyFiles() { return new ArrayList<>(emptyFiles); }
        public List<String> getMalformedFiles() { return new ArrayList<>(malformedFiles); }
        public List<String> getIncompleteFiles() { return new ArrayList<>(incompleteFiles); }
        public List<String> getSuspiciousFiles() { return new ArrayList<>(suspiciousFiles); }
        public List<String> getUnreadableFiles() { return new ArrayList<>(unreadableFiles); }
        public List<String> getCriticalErrors() { return new ArrayList<>(criticalErrors); }
        public int getTotalAnalyzedFiles() { return totalAnalyzedFiles; }
    }
}










