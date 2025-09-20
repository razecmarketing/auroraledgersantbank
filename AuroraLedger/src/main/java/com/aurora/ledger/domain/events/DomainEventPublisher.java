package com.aurora.ledger.domain.events;

/**
 * DomainEventPublisher - Interface for publishing domain events
 * Enables Event Sourcing and CQRS patterns
 * 
 * 
 */
public interface DomainEventPublisher {
    
    /**
     * Publishes domain event to event store and message broker
     * For audit trail and downstream systems integration
     */
    void publish(DomainEvent event);
    
    /**
     * Publishes multiple events in a single transaction
     * Maintains consistency across aggregate boundaries
     */
    void publishAll(java.util.List<DomainEvent> events);
}
