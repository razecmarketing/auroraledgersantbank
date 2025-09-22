package com.aurora.ledger.domain.services;

import com.aurora.ledger.domain.account.Account;
import com.aurora.ledger.domain.account.AccountType;
import com.aurora.ledger.domain.common.Money;

/**
 * Strategy interface for accountspecific business rules and validations.
 * Implements Strategy Pattern for different account type behaviors.
 */
public interface AccountValidationStrategy {
    
    boolean supports(AccountType accountType);
    
    void validateAccountCreation(String customerCpf, Money initialDeposit);
    
    void validateDebitOperation(Account account, Money amount, String reason);
    
    void validateCreditOperation(Account account, Money amount, String reason);
    
    Money calculateMinimumBalance(Account account);
    
    Money calculateDailyLimit(Account account);
}










