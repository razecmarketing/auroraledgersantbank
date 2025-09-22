package com.aurora.ledger.domain.transaction;

import com.aurora.ledger.domain.account.AccountId;
import com.aurora.ledger.domain.common.Money;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * LedgerEntry  Individual debit or credit entry in doubleentry bookkeeping
 * Each entry represents one side of a doubleentry transaction
 * 
 * 
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public final class LedgerEntry {
    
    private final AccountId accountId;
    private final Money amount;
    private final EntryType entryType;
    private final String description;
    
    /**
     * Creates a debit entry  represents money leaving an account
     */
    public static LedgerEntry debit(AccountId accountId, Money amount, String description) {
        validateEntry(accountId, amount, description);
        return new LedgerEntry(accountId, amount, EntryType.DEBIT, description);
    }
    
    /**
     * Creates a credit entry  represents money entering an account
     */
    public static LedgerEntry credit(AccountId accountId, Money amount, String description) {
        validateEntry(accountId, amount, description);
        return new LedgerEntry(accountId, amount, EntryType.CREDIT, description);
    }
    
    public boolean isDebit() {
        return entryType == EntryType.DEBIT;
    }
    
    public boolean isCredit() {
        return entryType == EntryType.CREDIT;
    }
    
    private static void validateEntry(AccountId accountId, Money amount, String description) {
        if (accountId == null) {
            throw new IllegalArgumentException("AccountId is required");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        if (amount.isNegative() || amount.isZero()) {
            throw new IllegalArgumentException("Entry amount must be positive");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Entry description is required");
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s %s %s: %s", 
            entryType, accountId, amount, description);
    }
    
    public enum EntryType {
        DEBIT("Dr"),
        CREDIT("Cr");
        
        private final String symbol;
        
        EntryType(String symbol) {
            this.symbol = symbol;
        }
        
        public String getSymbol() {
            return symbol;
        }
    }
}










