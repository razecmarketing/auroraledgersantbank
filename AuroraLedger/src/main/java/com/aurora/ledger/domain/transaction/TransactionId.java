package com.aurora.ledger.domain.transaction;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * TransactionId Value Object - Strong typing for transaction identification
 * 
 * 
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public final class TransactionId {
    
    private final String value;
    
    public static TransactionId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("TransactionId cannot be null or empty");
        }
        return new TransactionId(value.trim());
    }
    
    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID().toString());
    }
    
    @Override
    public String toString() {
        return value;
    }
}
