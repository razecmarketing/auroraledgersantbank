package com.aurora.ledger.domain.transaction;

/**
 * TransactionType  Banking transaction categories
 * 
 * 
 */
public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    TRANSFER("Transfer"),
    PIX("PIX Payment"),
    TED("TED Transfer"),
    DOC("DOC Transfer"),
    PAYMENT("Bill Payment"),
    INVESTMENT("Investment"),
    LOAN("Loan"),
    INTEREST("Interest"),
    FEE("Fee"),
    REVERSAL("Reversal");
    
    private final String displayName;
    
    TransactionType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean requiresDoubleEntry() {
        return this != INTEREST && this != FEE; // These might be single entry for simplicity
    }
}










