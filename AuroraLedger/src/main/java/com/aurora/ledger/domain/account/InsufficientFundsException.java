package com.aurora.ledger.domain.account;

/**
 * InsufficientFundsException  Domain exception for banking business rules
 * 
 * 
 */
public class InsufficientFundsException extends RuntimeException {
    
    public InsufficientFundsException(String message) {
        super(message);
    }
    
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}










