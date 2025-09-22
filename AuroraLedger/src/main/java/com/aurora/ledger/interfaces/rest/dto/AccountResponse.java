package com.aurora.ledger.interfaces.rest.dto;

import lombok.Data;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AccountResponse  Clean API response with proper encapsulation
 * Never exposes internal domain structure
 * 
 * 
 */
@Data
@Builder
@Schema(description = "Banking account information")
public class AccountResponse {
    
    @Schema(description = "Unique account identifier", example = "acc1234567890")
    private String accountId;
    
    @Schema(description = "Account number for customer operations", example = "1234567890")
    private String accountNumber;
    
    @Schema(description = "Account type", example = "CHECKING")
    private String accountType;
    
    @Schema(description = "Current account balance", example = "1250.75")
    private BigDecimal balance;
    
    @Schema(description = "Currency code", example = "BRL")
    private String currency;
    
    @Schema(description = "Account status", example = "ACTIVE")
    private String status;
    
    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
    
    // Customer data is intentionally masked for privacy
    @Schema(description = "Masked customer identifier", example = "***.***.***01")
    private String maskedCustomerCpf;
}










