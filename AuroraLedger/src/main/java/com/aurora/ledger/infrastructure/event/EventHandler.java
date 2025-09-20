package com.aurora.ledger.infrastructure.event;

import com.aurora.ledger.domain.shared.DomainEvent;

/**
 * Event Handler Interface
 * Processes domain events for Query Side projections and reactions
 * Following Event-Driven Architecture and eventual consistency principles
 */
public interface EventHandler<T extends DomainEvent> {
    
    /**
     * Handles domain event to update projections or trigger reactions
     * Must be idempotent - handling same event multiple times should be safe
     * 
     * @param event Domain event to process
     */
    void handle(T event);
    
    /**
     * Event type this handler processes
     */
    Class<T> getEventType();
    
    /**
     * Handler priority for ordering when multiple handlers exist
     * Lower numbers = higher priority
     */
    default int getPriority() {
        return 100;
    }
    
    /**
     * Whether this handler can process events asynchronously
     * Critical handlers (like fraud detection) should return false
     */
    default boolean isAsynchronous() {
        return true;
    }
}
