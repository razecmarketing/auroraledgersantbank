package com.aurora.ledger.domain.events;

import com.aurora.ledger.domain.account.Account;
import com.aurora.ledger.domain.account.AccountId;
import com.aurora.ledger.domain.account.AccountType;
import com.aurora.ledger.domain.common.Money;

import java.util.UUID;

/**
 * AccountCreatedEvent  Critical business event for audit and compliance
 * Captures every detail needed for regulatory reporting
 * 
 * 
 */
public class AccountCreatedEvent extends DomainEvent {
    
    private final AccountId accountId;
    private final String accountNumber;
    private final String customerCpf;
    private final AccountType accountType;
    private final Money initialBalance;
    
    private AccountCreatedEvent(Builder builder) {
        super(UUID.randomUUID().toString(), builder.correlationId, builder.userId, builder.version);
        this.accountId = builder.accountId;
        this.accountNumber = builder.accountNumber;
        this.customerCpf = builder.customerCpf;
        this.accountType = builder.accountType;
        this.initialBalance = builder.initialBalance;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private AccountId accountId;
        private String accountNumber;
        private String customerCpf;
        private AccountType accountType;
        private Money initialBalance;
        private String correlationId;
        private String userId;
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
        
        public Builder initialBalance(Money initialBalance) {
            this.initialBalance = initialBalance;
            return this;
        }
        
        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }
        
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder version(Long version) {
            this.version = version;
            return this;
        }
        
        public AccountCreatedEvent build() {
            return new AccountCreatedEvent(this);
        }
    }
    
    public static AccountCreatedEvent from(Account account, String correlationId, String userId) {
        return builder()
            .accountId(account.getAccountId())
            .accountNumber(account.getAccountNumber())
            .customerCpf(account.getCustomerCpf())
            .accountType(account.getAccountType())
            .initialBalance(account.getBalance())
            .correlationId(correlationId)
            .userId(userId)
            .version(account.getVersion())
            .build();
    }
    
    @Override
    public String getEventType() {
        return "AccountCreated";
    }
    
    @Override
    public String getAggregateId() {
        return accountId.getValue();
    }
    
    public AccountId getAccountId() {
        return accountId;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public String getCustomerCpf() {
        return customerCpf;
    }
    
    public AccountType getAccountType() {
        return accountType;
    }
    
    public Money getInitialBalance() {
        return initialBalance;
    }
    
    @Override
    public String toString() {
        return String.format("AccountCreatedEvent{accountId=%s, accountNumber=%s, customerCpf=%s, " +
                           "accountType=%s, initialBalance=%s, occurredAt=%s}", 
                           accountId, accountNumber, maskCpf(customerCpf), 
                           accountType, initialBalance, getOccurredAt());
    }
    
    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }
        return "***.***.***" + cpf.substring(cpf.length() - 2);
    }
}










