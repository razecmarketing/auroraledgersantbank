package com.aurora.ledger.infrastructure.events;

/**
 * Exception thrown when domain event processing fails.
 * Provides specific context for event handling failures.
 */
public class EventProcessingException extends RuntimeException {
    
    private final String eventType;
    private final String eventId;
    
    public EventProcessingException(String eventType, String eventId, String message, Throwable cause) {
        super(String.format("Failed to process event %s [%s]: %s", eventType, eventId, message), cause);
        this.eventType = eventType;
        this.eventId = eventId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public String getEventId() {
        return eventId;
    }
}
