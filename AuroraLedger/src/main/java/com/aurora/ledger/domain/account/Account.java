package com.aurora.ledger.domain.account;

import com.aurora.ledger.domain.common.Money;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Account Aggregate Root - Core banking account with business rules
 * Implements double-entry bookkeeping principles and audit trail
 * 
 * 
 */
@Getter
@RequiredArgsConstructor
public class Account {
    
    private static final java.util.Random RANDOM = new java.util.Random();
    
    private final AccountId accountId;
    private final String accountNumber;
    private final String customerCpf;
    private final AccountType accountType;
    private final Money balance;
    private final AccountStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Long version; // Optimistic locking
    
    private Account(Builder builder) {
        this.accountId = builder.accountId;
        this.accountNumber = builder.accountNumber;
        this.customerCpf = builder.customerCpf;
        this.accountType = builder.accountType;
        this.balance = builder.balance;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.version = builder.version;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Creates a new account with initial deposit
     * Validates business rules for account opening
     */
    public static Account create(String customerCpf, AccountType accountType, Money initialDeposit) {
        validateCustomerCpf(customerCpf);
        validateInitialDeposit(initialDeposit, accountType);
        
        LocalDateTime now = LocalDateTime.now();
        
        return Account.builder()
            .accountId(AccountId.generate())
            .accountNumber(generateAccountNumber())
            .customerCpf(customerCpf)
            .accountType(accountType)
            .balance(initialDeposit)
            .status(AccountStatus.ACTIVE)
            .createdAt(now)
            .updatedAt(now)
            .version(0L)
            .build();
    }
    
    /**
     * Debits amount from account with business rule validation
     * Returns new account state (immutable)
     */
    public Account debit(Money amount, String reason) {
        validateDebitOperation(amount, reason);
        
        Money newBalance = this.balance.subtract(amount);
        
        return Account.builder()
            .accountId(this.accountId)
            .accountNumber(this.accountNumber)
            .customerCpf(this.customerCpf)
            .accountType(this.accountType)
            .balance(newBalance)
            .status(this.status)
            .createdAt(this.createdAt)
            .updatedAt(LocalDateTime.now())
            .version(this.version + 1)
            .build();
    }
    
    /**
     * Credits amount to account
     * Returns new account state (immutable)
     */
    public Account credit(Money amount, String reason) {
        validateCreditOperation(amount, reason);
        
        Money newBalance = this.balance.add(amount);
        
        return Account.builder()
            .accountId(this.accountId)
            .accountNumber(this.accountNumber)
            .customerCpf(this.customerCpf)
            .accountType(this.accountType)
            .balance(newBalance)
            .status(this.status)
            .createdAt(this.createdAt)
            .updatedAt(LocalDateTime.now())
            .version(this.version + 1)
            .build();
    }
    
    public boolean canDebit(Money amount) {
        if (!status.equals(AccountStatus.ACTIVE)) {
            return false;
        }
        
        Money minimumBalance = getMinimumBalance();
        Money balanceAfterDebit = balance.subtract(amount);
        
        return balanceAfterDebit.isGreaterThanOrEqual(minimumBalance);
    }
    
    private Money getMinimumBalance() {
        return switch (accountType) {
            case SAVINGS -> Money.zero(balance.getCurrency());
            case CHECKING -> Money.of("-1000.00", balance.getCurrency().getCurrencyCode());
            case BUSINESS -> Money.of("-5000.00", balance.getCurrency().getCurrencyCode());
        };
    }
    
    private static void validateCustomerCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer CPF is required");
        }
        // Add CPF validation logic here
    }
    
    private static void validateInitialDeposit(Money amount, AccountType accountType) {
        if (amount == null || amount.isNegative()) {
            throw new IllegalArgumentException("Initial deposit must be positive");
        }
        
        Money minimumDeposit = switch (accountType) {
            case SAVINGS -> Money.of("10.00", amount.getCurrency().getCurrencyCode());
            case CHECKING -> Money.of("50.00", amount.getCurrency().getCurrencyCode());
            case BUSINESS -> Money.of("500.00", amount.getCurrency().getCurrencyCode());
        };
        
        if (!amount.isGreaterThanOrEqual(minimumDeposit)) {
            throw new IllegalArgumentException(
                String.format("Minimum deposit for %s account is %s", accountType, minimumDeposit)
            );
        }
    }
    
    private void validateDebitOperation(Money amount, String reason) {
        if (amount == null || amount.isNegative() || amount.isZero()) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Debit reason is required for audit trail");
        }
        
        if (!canDebit(amount)) {
            throw new InsufficientFundsException(
                String.format("Insufficient funds. Balance: %s, Requested: %s", balance, amount)
            );
        }
    }
    
    private void validateCreditOperation(Money amount, String reason) {
        if (amount == null || amount.isNegative() || amount.isZero()) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Credit reason is required for audit trail");
        }
    }
    
    private static String generateAccountNumber() {
        // Santander-like account number format: AAAA-BBBB-CC
        return String.format("%04d-%04d-%02d", 
            RANDOM.nextInt(10000),
            RANDOM.nextInt(10000),
            RANDOM.nextInt(100)
        );
    }
    
    public static class Builder {
        private AccountId accountId;
        private String accountNumber;
        private String customerCpf;
        private AccountType accountType;
        private Money balance;
        private AccountStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long version;
        
        public Builder accountId(AccountId accountId) {
            this.accountId = accountId;
            return this;
        }
        
        public Builder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }
        
        public Builder customerCpf(String customerCpf) {
            this.customerCpf = customerCpf;
            return this;
        }
        
        public Builder accountType(AccountType accountType) {
            this.accountType = accountType;
            return this;
        }
        
        public Builder balance(Money balance) {
            this.balance = balance;
            return this;
        }
        
        public Builder status(AccountStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public Builder version(Long version) {
            this.version = version;
            return this;
        }
        
        public Account build() {
            return new Account(this);
        }
    }
}
