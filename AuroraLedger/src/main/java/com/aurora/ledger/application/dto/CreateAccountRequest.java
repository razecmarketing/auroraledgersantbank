package com.aurora.ledger.application.dto;

import com.aurora.ledger.domain.account.AccountType;
import lombok.Builder;
import lombok.Value;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * CreateAccountRequest DTO
 * Input boundary for account creation use case
 * 
 * Following Clean Architecture principles:
 *  Input DTO in application layer
 *  Immutable with validation
 *  No business logic
 * 
 * @compliance PCI DSS + LGPD (CPF validation)
 */
@Value
@Builder
public class CreateAccountRequest {
    
    @NotBlank(message = "Customer CPF is required")
    @Pattern(regexp = "\\d{11}", message = "CPF must contain exactly 11 digits")
    String customerCpf;
    
    @NotNull(message = "Account type is required")
    AccountType accountType;
    
    @NotNull(message = "Initial balance is required")
    @PositiveOrZero(message = "Initial balance must be zero or positive")
    BigDecimal initialBalance;
}









