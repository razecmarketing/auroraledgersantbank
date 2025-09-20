package com.aurora.ledger.domain.account;

/**
 * AccountType - Banking account categories with specific rules
 * 
 * 
 */
public enum AccountType {
    SAVINGS("Savings Account"),
    CHECKING("Checking Account"),
    BUSINESS("Business Account");
    
    private final String displayName;
    
    AccountType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
