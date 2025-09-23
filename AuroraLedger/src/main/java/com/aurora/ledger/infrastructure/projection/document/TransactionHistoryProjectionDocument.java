package com.aurora.ledger.infrastructure.projection.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction History Projection Document for MongoDB
 * 
 * Optimized read model for banking transaction history queries
 * Provides fast access to customer transaction records for:
 * - Account statements
 * - Transaction search and filtering
 * - Audit trail reporting
 * - Regulatory compliance queries
 * 
 * @architecture CQRS Read Model
 * @compliance PCI DSS + SOX + LGPD
 */
@Document(collection = "transaction_history_projections")
public class TransactionHistoryProjectionDocument {

    @Id
    private String id;
    
    @Field("event_id")
    private String eventId;
    
    @Field("account_id")
    private String accountId;
    
    @Field("transaction_type")
    private String transactionType;
    
    @Field("amount")
    private BigDecimal amount;
    
    @Field("description")
    private String description;
    
    @Field("transaction_date")
    private LocalDateTime transactionDate;
    
    @Field("correlation_id")
    private String correlationId;
    
    @Field("user_id")
    private String userId;
    
    @Field("created_at")
    private LocalDateTime createdAt;

    public TransactionHistoryProjectionDocument() {
        this.createdAt = LocalDateTime.now();
    }

    public TransactionHistoryProjectionDocument(String eventId, String accountId, 
            String transactionType, BigDecimal amount, String description, 
            LocalDateTime transactionDate, String correlationId, String userId) {
        this.eventId = eventId;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
        this.correlationId = correlationId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}