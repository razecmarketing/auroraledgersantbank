package com.aurora.ledger.domain.account;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * AccountId Value Object - Strong typing for account identification
 * 
 * 
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public final class AccountId {
    
    private final String value;
    
    public static AccountId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("AccountId cannot be null or empty");
        }
        return new AccountId(value.trim());
    }
    
    public static AccountId generate() {
        return new AccountId(UUID.randomUUID().toString());
    }
    
    @Override
    public String toString() {
        return value;
    }
}
