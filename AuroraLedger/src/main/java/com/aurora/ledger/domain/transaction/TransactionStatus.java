package com.aurora.ledger.domain.transaction;

/**
 * TransactionStatus - Transaction lifecycle states for audit and compliance
 * 
 * 
 */
public enum TransactionStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled"),
    REVERSED("Reversed");
    
    private final String displayName;
    
    TransactionStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == REVERSED;
    }
    
    public boolean canBeReversed() {
        return this == COMPLETED;
    }
}
