package com.aurora.ledger.domain.services;

import com.aurora.ledger.domain.account.Account;
import com.aurora.ledger.domain.account.AccountType;
import com.aurora.ledger.domain.common.Money;
import org.springframework.stereotype.Component;

/**
 * Concrete strategy for savings account business rules.
 * Implements conservative validation rules for savings accounts.
 */
@Component
public class SavingsAccountStrategy implements AccountValidationStrategy {
    
    private static final Money MINIMUM_DEPOSIT = Money.brl("10.00");
    private static final Money MINIMUM_BALANCE = Money.brl("0.00");
    private static final Money DAILY_LIMIT = Money.brl("5000.00");
    
    @Override
    public boolean supports(AccountType accountType) {
        return AccountType.SAVINGS.equals(accountType);
    }
    
    @Override
    public void validateAccountCreation(String customerCpf, Money initialDeposit) {
        if (!initialDeposit.isGreaterThanOrEqual(MINIMUM_DEPOSIT)) {
            throw new IllegalArgumentException(
                String.format("Minimum deposit for savings account is %s", MINIMUM_DEPOSIT));
        }
    }
    
    @Override
    public void validateDebitOperation(Account account, Money amount, String reason) {
        Money balanceAfterDebit = account.getBalance().subtract(amount);
        
        if (!balanceAfterDebit.isGreaterThanOrEqual(MINIMUM_BALANCE)) {
            throw new IllegalArgumentException(
                String.format("Savings account cannot go below minimum balance: %s", MINIMUM_BALANCE));
        }
        
        if (amount.isGreaterThan(DAILY_LIMIT)) {
            throw new IllegalArgumentException(
                String.format("Daily withdrawal limit exceeded. Maximum: %s", DAILY_LIMIT));
        }
    }
    
    @Override
    public void validateCreditOperation(Account account, Money amount, String reason) {
        // Savings accounts have no credit restrictions
    }
    
    @Override
    public Money calculateMinimumBalance(Account account) {
        return MINIMUM_BALANCE;
    }
    
    @Override
    public Money calculateDailyLimit(Account account) {
        return DAILY_LIMIT;
    }
}
