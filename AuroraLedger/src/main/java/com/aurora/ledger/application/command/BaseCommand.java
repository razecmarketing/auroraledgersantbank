package com.aurora.ledger.application.command;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract Base Command Implementation
 * Implements common command infrastructure following CQRS patterns
 */
public abstract class BaseCommand implements Command {
    
    private final String commandId;
    private final LocalDateTime createdAt;
    private final String issuedBy;
    
    protected BaseCommand(String issuedBy) {
        this.commandId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.issuedBy = issuedBy;
    }
    
    @Override
    public String getCommandId() {
        return commandId;
    }
    
    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    @Override
    public String getIssuedBy() {
        return issuedBy;
    }
    
    /**
     * Template method for validation
     * Subclasses must implement specific validation rules
     */
    @Override
    public void validate() {
        validateBasicRules();
        validateBusinessRules();
    }
    
    /**
     * Basic validation common to all commands
     */
    private void validateBasicRules() {
        if (issuedBy == null || issuedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Command must specify who issued it");
        }
    }
    
    /**
     * Business-specific validation - implemented by concrete commands
     */
    protected abstract void validateBusinessRules();
    
    @Override
    public String toString() {
        return String.format("Command[%s]{id=%s, issuedBy=%s, createdAt=%s}", 
                getClass().getSimpleName(), commandId, issuedBy, createdAt);
    }
}
