package com.aurora.ledger.application.dto;

import lombok.Builder;
import lombok.Value;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * TransactionRequest DTO
 * Input boundary for transaction operations (deposit/withdraw)
 * 
 * Following Clean Architecture principles:
 *  Single responsibility (transaction data)
 *  Immutable with validation
 *  Banking compliance ready
 * 
 * @compliance Banking Transaction Standards
 */
@Value
@Builder
public class TransactionRequest {
    
    @NotBlank(message = "Account number is required")
    String accountNumber;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    BigDecimal amount;
    
    @NotBlank(message = "Description is required")
    String description;
}









