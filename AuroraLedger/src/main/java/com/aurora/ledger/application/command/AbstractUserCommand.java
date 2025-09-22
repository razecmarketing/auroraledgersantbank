package com.aurora.ledger.application.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Abstract Base Command for User Operations
 * Implements common functionality shared by userinitiated commands
 * Follows DRY principle and Command Pattern best practices
 */
public abstract class AbstractUserCommand implements Command {
    
    protected final String commandId;
    protected final LocalDateTime createdAt;
    protected final String userLogin;
    protected final BigDecimal amount;
    protected final String correlationId;
    
    protected AbstractUserCommand(String userLogin, BigDecimal amount) {
        this.commandId = java.util.UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.userLogin = userLogin;
        this.amount = amount;
        this.correlationId = java.util.UUID.randomUUID().toString();
    }
    
    @Override
    public final String getCommandId() {
        return commandId;
    }
    
    @Override
    public final LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    @Override
    public final String getIssuedBy() {
        return userLogin;
    }
    
    @Override
    public void validate() {
        if (userLogin == null || userLogin.trim().isEmpty()) {
            throw new IllegalArgumentException("User login cannot be null or empty");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(getAmountErrorMessage());
        }
        if (amount.scale() > 2) {
            throw new IllegalArgumentException("Amount cannot have more than 2 decimal places");
        }
        
        // Allow subclasses to add specific validation
        validateSpecific();
    }
    
    /**
     * Subclasses should implement specific validation logic here
     */
    protected abstract void validateSpecific();
    
    /**
     * Subclasses should provide specific error message for amount validation
     */
    protected abstract String getAmountErrorMessage();
    
    // Common getters
    public final String getUserLogin() {
        return userLogin;
    }
    
    public final BigDecimal getAmount() {
        return amount;
    }
    
    public final String getCorrelationId() {
        return correlationId;
    }
}










