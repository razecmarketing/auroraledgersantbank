package com.aurora.ledger.application.transaction.query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Balance Query Result - CQRS Read Model
 * Contains user balance information and transaction history following Clean Architecture patterns.
 * Implements Uncle Bob's Clean Code principles with English naming conventions.
 * Following Fowler's patterns for Data Transfer Objects and Query Objects.
 * 
 * JSON Response Format (English):
 * {
 *   "totalBalance": "123.45",
 *   "transactionHistory": [
 *     {
 *       "type": "deposit",
 *       "amount": "100.00", 
 *       "date": "19-09-2025 14:30:15"
 *     }
 *   ]
 * }
 * 
 * @author Aurora Ledger Engineering Team
 * @pattern Query Object + Data Transfer Object
 * @layer Application Layer - CQRS Read Side
 */
public class BalanceQueryResult {
    
    private final String totalBalance;
    private final List<TransactionHistoryItem> transactionHistory;
    private final boolean isNegative;
    private final LocalDateTime lastUpdated;
    
    public BalanceQueryResult(BigDecimal balance, List<TransactionHistoryItem> history, 
                             boolean isNegative, LocalDateTime lastUpdated) {
        this.totalBalance = balance.toString();
        this.transactionHistory = history;
        this.isNegative = isNegative;
        this.lastUpdated = lastUpdated;
    }
    
    // Factory method for creating result with balance only
    public static BalanceQueryResult withBalanceOnly(BigDecimal balance, boolean isNegative, 
                                                   LocalDateTime lastUpdated) {
        return new BalanceQueryResult(balance, List.of(), isNegative, lastUpdated);
    }
    
    // Getters for JSON serialization following English conventions
    public String getTotalBalance() {
        return totalBalance;
    }
    
    public List<TransactionHistoryItem> getTransactionHistory() {
        return transactionHistory;
    }
    
    public boolean isNegative() {
        return isNegative;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    /**
     * Transaction History Item for JSON response
     * Following Clean Code principles with English naming conventions
     * Implements Uncle Bob's Value Object pattern
     */
    public static class TransactionHistoryItem {
        private final String type;
        private final String amount;
        private final String date;
        
        public TransactionHistoryItem(String type, BigDecimal amount, LocalDateTime dateTime) {
            this.type = type;
            this.amount = amount.toString();
            this.date = dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        }
        
        public String getType() {
            return type;
        }
        
        public String getAmount() {
            return amount;
        }
        
        public String getDate() {
            return date;
        }
    }
    
    @Override
    public String toString() {
        return String.format("BalanceQueryResult{totalBalance='%s', transactionHistory=%d items, negative=%b}", 
                           totalBalance, transactionHistory.size(), isNegative);
    }
}
