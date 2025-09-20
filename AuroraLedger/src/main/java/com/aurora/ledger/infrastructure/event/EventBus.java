package com.aurora.ledger.infrastructure.event;

import com.aurora.ledger.domain.shared.DomainEvent;

/**
 * Event Bus Interface
 * Following Event-Driven Architecture principles
 * Decouples Command Side (Write) from Query Side (Read)
 */
public interface EventBus {
    
    /**
     * Publishes domain event to all subscribers
     * Used by Command Handlers to notify Query Side of changes
     * 
     * @param event Domain event to publish
     */
    void publish(DomainEvent event);
    
    /**
     * Publishes multiple events in order
     * Maintains event ordering for consistency
     * 
     * @param events Events to publish in sequence
     */
    void publishAll(java.util.List<DomainEvent> events);
    
    /**
     * Subscribes to specific event types
     * Query Side handlers register to rebuild projections
     * 
     * @param eventType Type of event to listen for
     * @param handler Handler to process the event
     */
    <T extends DomainEvent> void subscribe(Class<T> eventType, EventHandler<T> handler);
    
    /**
     * Unsubscribes handler from event type
     * Used for handler lifecycle management
     * 
     * @param eventType Event type to unsubscribe from
     * @param handler Handler to remove
     */
    <T extends DomainEvent> void unsubscribe(Class<T> eventType, EventHandler<T> handler);
}
