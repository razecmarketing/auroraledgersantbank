package com.aurora.ledger.domain.transaction;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction History Entity
 * Domain entity for banking transaction records
 * Stores complete audit trail for all financial operations
 */
@Entity
@Table(name = "transaction_history")
public class TransactionHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_login", nullable = false, length = 50)
    private String userLogin;
    
    @Column(name = "transaction_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    
    @Column(name = "correlation_id", length = 36)
    private String correlationId;
    
    @Column(name = "balance_after", precision = 15, scale = 2)
    private BigDecimal balanceAfter;
    
    // Default constructor for JPA
    protected TransactionHistory() {}
    
    // Constructor for domain creation
    public TransactionHistory(String userLogin, TransactionType transactionType, 
                            BigDecimal amount, String description, String correlationId, 
                            BigDecimal balanceAfter) {
        this.userLogin = userLogin;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.transactionDate = LocalDateTime.now();
        this.correlationId = correlationId;
        this.balanceAfter = balanceAfter;
    }
    
    // Factory methods for business operations
    public static TransactionHistory deposit(String userLogin, BigDecimal amount, 
                                           String description, String correlationId, 
                                           BigDecimal balanceAfter) {
        return new TransactionHistory(userLogin, TransactionType.DEPOSIT, amount, 
                                    description, correlationId, balanceAfter);
    }
    
    public static TransactionHistory billPayment(String userLogin, BigDecimal amount, 
                                               String description, String correlationId, 
                                               BigDecimal balanceAfter) {
        return new TransactionHistory(userLogin, TransactionType.PAYMENT, amount, 
                                    description, correlationId, balanceAfter);
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getUserLogin() {
        return userLogin;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }
    
    @Override
    public String toString() {
        return String.format("Transaction{id=%d, user='%s', type=%s, amount=%s, date=%s}", 
                           id, userLogin, transactionType, amount, transactionDate);
    }
}










