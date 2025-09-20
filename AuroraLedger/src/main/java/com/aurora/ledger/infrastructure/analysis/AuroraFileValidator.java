package com.aurora.ledger.infrastructure.analysis;

import com.aurora.ledger.application.command.Command;
import com.aurora.ledger.application.command.CommandHandler;
import com.aurora.ledger.domain.shared.DomainEvent;
import com.aurora.ledger.infrastructure.bus.CommandBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Aurora File Validator - CQRS File System Quality Assurance
 * Implements zero tolerance policy for code quality violations
 * Following Clean Architecture and CQRS principles
 */
@Component
public class AuroraFileValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(AuroraFileValidator.class);
    private static final String LATEST_REPORT_KEY = "latest";
    
    private final DeepFileIntegrityAnalyzer analyzer;
    private final DuplicateDetectionEngine duplicateEngine;
    private final CommandBus commandBus;
    
    // Read Model Cache for validation reports
    private final Map<String, Object> validationReports = new ConcurrentHashMap<>();
    
    public AuroraFileValidator(DeepFileIntegrityAnalyzer analyzer,
                              DuplicateDetectionEngine duplicateEngine,
                              CommandBus commandBus) {
        this.analyzer = analyzer;
        this.duplicateEngine = duplicateEngine;
        this.commandBus = commandBus;
        
        // Register command handlers
        commandBus.register(new ValidateProjectCommandHandler());
        
        logger.info("Aurora File Validator initialized with CQRS architecture");
    }
    
    /**
     * Execute comprehensive project validation
     */
    public void validateProject() {
        var command = new ValidateProjectCommand();
        commandBus.dispatch(command);
    }
    
    /**
     * Get latest validation report from Read Model
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getLatestValidationReport() {
        Object report = validationReports.getOrDefault(LATEST_REPORT_KEY, Map.of(
            "status", "NO_DATA",
            "message", "No validation performed yet"
        ));
        return (Map<String, Object>) report;
    }
    
    /**
     * Command to trigger project validation
     */
    public static class ValidateProjectCommand implements Command {
        private final String commandId = java.util.UUID.randomUUID().toString();
        private final java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
        
        @Override
        public String getCommandId() {
            return commandId;
        }
        
        @Override
        public java.time.LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        @Override
        public String getIssuedBy() {
            return "AURORA_SYSTEM";
        }
        
        @Override
        public void validate() {
            // Always valid - system command
        }
    }
    
    /**
     * Command Handler for project validation
     * Implements Write Side of CQRS
     */
    private class ValidateProjectCommandHandler implements CommandHandler<ValidateProjectCommand> {
        
        @Override
        public List<DomainEvent> handle(ValidateProjectCommand command) {
            logger.info("CQRS COMMAND: Starting project validation - {}", command.getCommandId());
            
            List<DomainEvent> events = new ArrayList<>();
            Path projectRoot = Paths.get(System.getProperty("user.dir"));
            
            try {
                // Execute deep file analysis
                var integrityReport = analyzer.analyzeProject(projectRoot.toString());
                var duplicateReport = duplicateEngine.analyzeProject(projectRoot);
                
                // Cache validation summary in Read Model  
                Map<String, Object> summary = Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "totalFiles", integrityReport.getTotalAnalyzedFiles(),
                    "duplicateFiles", duplicateReport.getDuplicateFiles().size(),
                    "status", "COMPLETED",
                    "correlationId", command.getCommandId()
                );
                validationReports.put(LATEST_REPORT_KEY, summary);
                
                // Generate completion event
                events.add(new ValidationCompletedEvent(
                    command.getCommandId(), 
                    integrityReport.getTotalAnalyzedFiles(),
                    duplicateReport.getDuplicateFiles().size()
                ));
                
                logger.info("CQRS EVENT: Project validation completed - {} files analyzed, {} duplicates found", 
                           integrityReport.getTotalAnalyzedFiles(), 
                           duplicateReport.getDuplicateFiles().size());
                
            } catch (Exception e) {
                logger.error("Project validation failed", e);
                events.add(new ValidationFailedEvent(command.getCommandId(), e.getMessage()));
            }
            
            return events;
        }
        
        @Override
        public Class<ValidateProjectCommand> getCommandType() {
            return ValidateProjectCommand.class;
        }
    }
    
    /**
     * Domain Event: Validation completed successfully
     */
    private static class ValidationCompletedEvent extends DomainEvent {
        private final String commandId;
        private final int totalFiles;
        private final int duplicateCount;
        
        public ValidationCompletedEvent(String commandId, int totalFiles, int duplicateCount) {
            super(commandId, 1L);
            this.commandId = commandId;
            this.totalFiles = totalFiles;
            this.duplicateCount = duplicateCount;
        }
        
        @Override
        public String getEventType() {
            return "ValidationCompleted";
        }
        
        @Override
        public Object getEventPayload() {
            return Map.of(
                "commandId", commandId,
                "totalFiles", totalFiles,
                "duplicateCount", duplicateCount
            );
        }
        
        @SuppressWarnings("unused")
        public int getTotalFiles() {
            return totalFiles;
        }
        
        @SuppressWarnings("unused")
        public int getDuplicateCount() {
            return duplicateCount;
        }
    }
    
    /**
     * Domain Event: Validation failed
     */
    private static class ValidationFailedEvent extends DomainEvent {
        private final String commandId;
        private final String errorMessage;
        
        public ValidationFailedEvent(String commandId, String errorMessage) {
            super(commandId, 1L);
            this.commandId = commandId;
            this.errorMessage = errorMessage;
        }
        
        @Override
        public String getEventType() {
            return "ValidationFailed";
        }
        
        @Override
        public Object getEventPayload() {
            return Map.of(
                "commandId", commandId,
                "errorMessage", errorMessage
            );
        }
    }
}
