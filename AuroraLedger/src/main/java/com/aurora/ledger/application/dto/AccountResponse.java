package com.aurora.ledger.application.dto;

import com.aurora.ledger.domain.account.AccountType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * AccountResponse DTO
 * Output boundary for account operations
 * 
 * Following Clean Architecture principles:
 *  Account data projection
 *  Immutable response structure
 *  Banking compliance ready
 * 

 * @compliance Banking Account Standards + LGPD
 */
@Value
@Builder
public class AccountResponse {
    
    String accountNumber;
    String customerCpf;
    AccountType accountType;
    BigDecimal balance;
    String status;
}









