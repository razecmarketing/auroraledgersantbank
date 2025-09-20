package com.aurora.ledger.application.account;

import com.aurora.ledger.domain.account.AccountType;
import lombok.Data;

import java.math.BigDecimal;

/**
 * CreateAccountCommand - Command pattern for account creation
 * Captures all required data with validation rules
 * 
 * 
 */
@Data
public class CreateAccountCommand {
    
    private final String customerCpf;
    private final AccountType accountType;
    private final BigDecimal initialDepositAmount;
    private final String currencyCode;
    private final String correlationId;
    private final String requestedByUserId;
    
    public CreateAccountCommand(String customerCpf, AccountType accountType, 
                               BigDecimal initialDepositAmount, String currencyCode,
                               String correlationId, String requestedByUserId) {
        this.customerCpf = customerCpf;
        this.accountType = accountType;
        this.initialDepositAmount = initialDepositAmount;
        this.currencyCode = currencyCode;
        this.correlationId = correlationId;
        this.requestedByUserId = requestedByUserId;
    }
}
