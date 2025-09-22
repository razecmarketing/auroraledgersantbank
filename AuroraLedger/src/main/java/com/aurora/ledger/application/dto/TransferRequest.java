package com.aurora.ledger.application.dto;

import lombok.Builder;
import lombok.Value;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * TransferRequest DTO
 * Input boundary for transfer operations between accounts
 * 
 * Following Clean Architecture principles:
 *  Transferspecific validation
 *  Immutable with constraints
 *  Antifraud ready structure
 * 
 * @compliance AML/KYC + Banking Transfer Standards
 */
@Value
@Builder
public class TransferRequest {
    
    @NotBlank(message = "Source account number is required")
    String sourceAccountNumber;
    
    @NotBlank(message = "Target account number is required")
    String targetAccountNumber;
    
    @NotNull(message = "Transfer amount is required")
    @Positive(message = "Transfer amount must be positive")
    BigDecimal amount;
    
    @NotBlank(message = "Transfer description is required")
    String description;
}









