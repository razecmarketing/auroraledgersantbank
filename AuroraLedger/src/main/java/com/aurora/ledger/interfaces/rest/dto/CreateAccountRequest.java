package com.aurora.ledger.interfaces.rest.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * CreateAccountRequest  Bankinggrade API contract
 * Demonstrates enterprise validation and documentation
 * 
 * 
 */
@Data
@Schema(description = "Request to create a new banking account")
public class CreateAccountRequest {
    
    @NotBlank(message = "Customer CPF is required")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}\\d{2}|\\d{11}", 
             message = "CPF must be in format 000.000.00000 or 11 digits")
    @Schema(description = "Customer CPF", example = "123.456.78901")
    private String customerCpf;
    
    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "SAVINGS|CHECKING|BUSINESS", 
             message = "Account type must be SAVINGS, CHECKING, or BUSINESS")
    @Schema(description = "Type of banking account", 
            allowableValues = {"SAVINGS", "CHECKING", "BUSINESS"},
            example = "CHECKING")
    private String accountType;
    
    @NotNull(message = "Initial deposit amount is required")
    @DecimalMin(value = "0.01", message = "Initial deposit must be at least 0.01")
    @DecimalMax(value = "1000000.00", message = "Initial deposit cannot exceed 1,000,000.00")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 2 decimal places")
    @Schema(description = "Initial deposit amount", example = "100.50")
    private BigDecimal initialDepositAmount;
    
    @NotBlank(message = "Currency code is required")
    @Pattern(regexp = "BRL|USD|EUR", message = "Supported currencies: BRL, USD, EUR")
    @Schema(description = "Currency code (ISO 4217)", 
            allowableValues = {"BRL", "USD", "EUR"},
            example = "BRL")
    private String currencyCode;
}










