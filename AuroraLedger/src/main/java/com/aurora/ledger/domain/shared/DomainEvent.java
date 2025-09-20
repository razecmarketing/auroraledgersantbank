package com.aurora.ledger.domain.shared;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain Event Base Class
 * Following Bertrand Meyer's Command-Query Separation + Greg Young's CQRS principles
 * Immutable event that represents something that happened in the domain
 */
public abstract class DomainEvent {
    
    private final UUID eventId;
    private final LocalDateTime occurredOn;
    private final String aggregateId;
    private final Long aggregateVersion;
    private final String eventType;
    
    protected DomainEvent(String aggregateId, Long aggregateVersion) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = LocalDateTime.now();
        this.aggregateId = aggregateId;
        this.aggregateVersion = aggregateVersion;
        this.eventType = this.getClass().getSimpleName();
    }
    
    // Queries only - immutable by design (Meyer's principle)
    public UUID getEventId() { return eventId; }
    public LocalDateTime getOccurredOn() { return occurredOn; }
    public String getAggregateId() { return aggregateId; }
    public Long getAggregateVersion() { return aggregateVersion; }
    public String getEventType() { return eventType; }
    
    /**
     * Event payload for serialization and projection building
     * Each concrete event must provide its own payload
     */
    public abstract Object getEventPayload();
    
    /**
     * Event stream position for ordering and replaying
     */
    public String getStreamPosition() {
        return String.format("%s-%d", aggregateId, aggregateVersion);
    }
    
    @Override
    public String toString() {
        return String.format("Event[%s]{id=%s, aggregate=%s, version=%d, time=%s}", 
                eventType, eventId, aggregateId, aggregateVersion, occurredOn);
    }
}
