package com.aurora.ledger.domain.events;

import java.time.LocalDateTime;

/**
 * Base Domain Event for banking system event sourcing.
 * Provides audit trail and traceability for all business operations.
 */
public abstract class DomainEvent {
    
    private final String eventId;
    private final LocalDateTime occurredAt;
    private final String correlationId;
    private final String userId;
    private final Long version;
    
    protected DomainEvent(String eventId, String correlationId, String userId, Long version) {
        this.eventId = eventId;
        this.occurredAt = LocalDateTime.now();
        this.correlationId = correlationId;
        this.userId = userId;
        this.version = version;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public abstract String getEventType();
    
    public abstract String getAggregateId();
}










