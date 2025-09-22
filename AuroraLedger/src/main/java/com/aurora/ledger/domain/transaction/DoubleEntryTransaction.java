package com.aurora.ledger.domain.transaction;

import com.aurora.ledger.domain.account.AccountId;
import com.aurora.ledger.domain.common.Money;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DoubleEntryTransaction  Core banking transaction implementing doubleentry bookkeeping
 * Every transaction has debits and credits that must balance (sum = 0)
 * This ensures data integrity and audit compliance required by banking regulations
 * 
 * 
 */
@Getter
@RequiredArgsConstructor
public final class DoubleEntryTransaction {
    
    private final TransactionId transactionId;
    private final TransactionType type;
    private final String description;
    private final List<LedgerEntry> entries;
    private final TransactionStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String correlationId; // For distributed tracing
    private final Long version; // Optimistic locking
    
    private DoubleEntryTransaction(Builder builder) {
        this.transactionId = builder.transactionId;
        this.type = builder.type;
        this.description = builder.description;
        this.entries = Collections.unmodifiableList(new ArrayList<>(builder.entries));
        this.status = builder.status;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.correlationId = builder.correlationId;
        this.version = builder.version;
        
        validateDoubleEntryRules();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Creates a simple transfer transaction between two accounts
     * This is the most common banking operation
     */
    public static DoubleEntryTransaction createTransfer(
            AccountId fromAccount, 
            AccountId toAccount, 
            Money amount, 
            String description,
            String correlationId) {
        
        LocalDateTime now = LocalDateTime.now();
        
        return DoubleEntryTransaction.builder()
            .transactionId(TransactionId.generate())
            .type(TransactionType.TRANSFER)
            .description(description)
            .addDebit(fromAccount, amount, "Transfer out")
            .addCredit(toAccount, amount, "Transfer in")
            .status(TransactionStatus.PENDING)
            .createdAt(now)
            .updatedAt(now)
            .correlationId(correlationId)
            .version(0L)
            .build();
    }
    
    /**
     * Creates a deposit transaction (external source to account)
     * Uses a system account as the contraaccount
     */
    public static DoubleEntryTransaction createDeposit(
            AccountId targetAccount, 
            Money amount, 
            String description,
            String correlationId) {
        
        LocalDateTime now = LocalDateTime.now();
        AccountId systemCashAccount = AccountId.of("SYSTEM_CASH");
        
        return DoubleEntryTransaction.builder()
            .transactionId(TransactionId.generate())
            .type(TransactionType.DEPOSIT)
            .description(description)
            .addDebit(systemCashAccount, amount, "Cash deposit")
            .addCredit(targetAccount, amount, "Deposit received")
            .status(TransactionStatus.PENDING)
            .createdAt(now)
            .updatedAt(now)
            .correlationId(correlationId)
            .version(0L)
            .build();
    }
    
    /**
     * Creates a withdrawal transaction (account to external destination)
     * Uses a system account as the contraaccount
     */
    public static DoubleEntryTransaction createWithdrawal(
            AccountId sourceAccount, 
            Money amount, 
            String description,
            String correlationId) {
        
        LocalDateTime now = LocalDateTime.now();
        AccountId systemCashAccount = AccountId.of("SYSTEM_CASH");
        
        return DoubleEntryTransaction.builder()
            .transactionId(TransactionId.generate())
            .type(TransactionType.WITHDRAWAL)
            .description(description)
            .addDebit(sourceAccount, amount, "Withdrawal")
            .addCredit(systemCashAccount, amount, "Cash withdrawal")
            .status(TransactionStatus.PENDING)
            .createdAt(now)
            .updatedAt(now)
            .correlationId(correlationId)
            .version(0L)
            .build();
    }
    
    /**
     * Returns a new transaction with updated status
     * Maintains immutability
     */
    public DoubleEntryTransaction withStatus(TransactionStatus newStatus) {
        if (this.status.isFinal() && newStatus != this.status) {
            throw new IllegalStateException(
                String.format("Cannot change status from %s to %s", this.status, newStatus)
            );
        }
        
        return DoubleEntryTransaction.builder()
            .transactionId(this.transactionId)
            .type(this.type)
            .description(this.description)
            .entries(this.entries)
            .status(newStatus)
            .createdAt(this.createdAt)
            .updatedAt(LocalDateTime.now())
            .correlationId(this.correlationId)
            .version(this.version + 1)
            .build();
    }
    
    /**
     * Creates a reversal transaction
     * Swaps debits and credits to undo the original transaction
     */
    public DoubleEntryTransaction createReversal(String reason, String correlationId) {
        if (!this.status.canBeReversed()) {
            throw new IllegalStateException(
                String.format("Transaction %s cannot be reversed from status %s", 
                    this.transactionId, this.status)
            );
        }
        
        LocalDateTime now = LocalDateTime.now();
        Builder reversalBuilder = DoubleEntryTransaction.builder()
            .transactionId(TransactionId.generate())
            .type(TransactionType.REVERSAL)
            .description(String.format("Reversal of %s: %s", this.transactionId, reason))
            .status(TransactionStatus.PENDING)
            .createdAt(now)
            .updatedAt(now)
            .correlationId(correlationId)
            .version(0L);
        
        // Reverse all entries (debits become credits and vice versa)
        for (LedgerEntry entry : this.entries) {
            if (entry.isDebit()) {
                reversalBuilder.addCredit(entry.getAccountId(), entry.getAmount(), 
                    "Reversal: " + entry.getDescription());
            } else {
                reversalBuilder.addDebit(entry.getAccountId(), entry.getAmount(), 
                    "Reversal: " + entry.getDescription());
            }
        }
        
        return reversalBuilder.build();
    }
    
    /**
     * Validates doubleentry bookkeeping rules:
     * 1. Sum of all debits must equal sum of all credits
     * 2. Must have at least one debit and one credit
     * 3. All amounts must be positive
     */
    private void validateDoubleEntryRules() {
        if (entries.isEmpty()) {
            throw new IllegalArgumentException("Transaction must have at least one entry");
        }
        
        Money totalDebits = Money.zero(entries.get(0).getAmount().getCurrency());
        Money totalCredits = Money.zero(entries.get(0).getAmount().getCurrency());
        
        boolean hasDebit = false;
        boolean hasCredit = false;
        
        for (LedgerEntry entry : entries) {
            if (entry.getAmount().isNegative() || entry.getAmount().isZero()) {
                throw new IllegalArgumentException("All entry amounts must be positive");
            }
            
            if (entry.isDebit()) {
                totalDebits = totalDebits.add(entry.getAmount());
                hasDebit = true;
            } else {
                totalCredits = totalCredits.add(entry.getAmount());
                hasCredit = true;
            }
        }
        
        if (!hasDebit || !hasCredit) {
            throw new IllegalArgumentException(
                "Transaction must have at least one debit and one credit entry");
        }
        
        if (!totalDebits.equals(totalCredits)) {
            throw new IllegalArgumentException(
                String.format("Doubleentry rule violated: Debits (%s) must equal Credits (%s)", 
                    totalDebits, totalCredits));
        }
    }
    
    public Money getTotalAmount() {
        return entries.stream()
            .filter(LedgerEntry::isDebit)
            .map(LedgerEntry::getAmount)
            .reduce(Money.zero(entries.get(0).getAmount().getCurrency()), Money::add);
    }
    
    public List<AccountId> getInvolvedAccounts() {
        return entries.stream()
            .map(LedgerEntry::getAccountId)
            .distinct()
            .toList();
    }
    
    public static class Builder {
        private TransactionId transactionId;
        private TransactionType type;
        private String description;
        private final List<LedgerEntry> entries = new ArrayList<>();
        private TransactionStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String correlationId;
        private Long version;
        
        public Builder transactionId(TransactionId transactionId) {
            this.transactionId = transactionId;
            return this;
        }
        
        public Builder type(TransactionType type) {
            this.type = type;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder addDebit(AccountId accountId, Money amount, String description) {
            this.entries.add(LedgerEntry.debit(accountId, amount, description));
            return this;
        }
        
        public Builder addCredit(AccountId accountId, Money amount, String description) {
            this.entries.add(LedgerEntry.credit(accountId, amount, description));
            return this;
        }
        
        public Builder entries(List<LedgerEntry> entries) {
            this.entries.clear();
            this.entries.addAll(entries);
            return this;
        }
        
        public Builder status(TransactionStatus status) {
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
        
        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }
        
        public Builder version(Long version) {
            this.version = version;
            return this;
        }
        
        public DoubleEntryTransaction build() {
            return new DoubleEntryTransaction(this);
        }
    }
}










