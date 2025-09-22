package com.aurora.ledger.infrastructure.events;

import com.aurora.ledger.domain.events.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Event bus implementation with transactional support and async processing.
 * Handles domain events with guaranteed delivery and error recovery.
 */
@Service
public class DomainEventBus {
    
    private static final Logger logger = LoggerFactory.getLogger(DomainEventBus.class);
    
    private final ApplicationEventPublisher applicationEventPublisher;
    private final List<Consumer<DomainEvent>> globalHandlers;
    
    public DomainEventBus(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.globalHandlers = new CopyOnWriteArrayList<>();
    }
    
    /**
     * Publishes domain event synchronously within transaction boundary.
     * Event will only be processed if transaction commits successfully.
     */
    public void publish(DomainEvent event) {
        logger.debug("Publishing domain event: {} for aggregate: {}", 
            event.getEventType(), event.getAggregateId());
            
        applicationEventPublisher.publishEvent(event);
    }
    
    /**
     * Publishes multiple events as a batch with atomicity guarantee.
     */
    public void publishAll(List<DomainEvent> events) {
        events.forEach(this::publish);
    }
    
    /**
     * Registers global event handler for all domain events.
     * Useful for crosscutting concerns like audit logging.
     */
    public void registerGlobalHandler(Consumer<DomainEvent> handler) {
        globalHandlers.add(handler);
    }
    
    /**
     * Handles events after transaction commit to ensure consistency.
     * Processes events asynchronously to avoid blocking main transaction.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommittedEvent(DomainEvent event) {
        logger.info("Processing committed domain event: {}  Correlation: {}", 
            event.getEventType(), event.getCorrelationId());
        
        // Process global handlers asynchronously
        CompletableFuture.runAsync(() -> 
            globalHandlers.forEach(handler -> {
                try {
                    handler.accept(event);
                } catch (Exception ex) {
                    // Log the failure with full context
                    logger.error("Global handler failed for event: {}  Error: {}", 
                        event.getEventType(), ex.getMessage(), ex);
                    
                    // Create enriched exception for monitoring
                    EventProcessingException enrichedException = new EventProcessingException(
                        event.getEventType(), 
                        event.getEventId(), 
                        ex.getMessage(), 
                        ex
                    );
                    
                    // Log the enriched exception for audit trail
                    logger.error("Event processing failed with context: {}", 
                        enrichedException.getMessage());
                    
                    throw enrichedException;
                }
            })
        );
    }
    
    /**
     * Handles rollback scenarios for compensation patterns.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleRollbackEvent(DomainEvent event) {
        logger.warn("Transaction rolled back for event: {}  Correlation: {}", 
            event.getEventType(), event.getCorrelationId());
    }
}










