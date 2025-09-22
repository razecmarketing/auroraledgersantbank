package com.aurora.ledger.domain.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * UserId Value Object
 * Strong typing for user identification
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public final class UserId {
    
    private final String value;
    
    public static UserId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId cannot be null or empty");
        }
        return new UserId(value.trim());
    }
    
    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }
    
    @Override
    public String toString() {
        return value;
    }
}