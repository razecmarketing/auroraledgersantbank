package com.aurora.ledger.domain.services;

import com.aurora.ledger.domain.account.AccountType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory service for account validation strategies.
 * Implements Factory Pattern with dependency injection and strategy resolution.
 */
@Service
public class AccountValidationFactory {
    
    private final Map<AccountType, AccountValidationStrategy> strategies;
    
    public AccountValidationFactory(List<AccountValidationStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                this::getAccountTypeForStrategy,
                Function.identity()
            ));
    }
    
    public AccountValidationStrategy getStrategy(AccountType accountType) {
        AccountValidationStrategy strategy = strategies.get(accountType);
        if (strategy == null) {
            throw new IllegalArgumentException(
                "No validation strategy found for account type: " + accountType);
        }
        return strategy;
    }
    
    private AccountType getAccountTypeForStrategy(AccountValidationStrategy strategy) {
        for (AccountType accountType : AccountType.values()) {
            if (strategy.supports(accountType)) {
                return accountType;
            }
        }
        throw new IllegalStateException(
            "Strategy must support at least one account type: " + strategy.getClass());
    }
}
