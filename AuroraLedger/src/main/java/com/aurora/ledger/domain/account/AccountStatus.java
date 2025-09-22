package com.aurora.ledger.domain.account;

/**
 * AccountStatus  Account lifecycle states
 * 
 * 
 */
public enum AccountStatus {
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    CLOSED("Closed"),
    PENDING_APPROVAL("Pending Approval");
    
    private final String displayName;
    
    AccountStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isTransactionAllowed() {
        return this == ACTIVE;
    }
}










