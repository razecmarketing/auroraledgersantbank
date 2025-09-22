package com.aurora.ledger.infrastructure.event;

import com.aurora.ledger.domain.shared.DomainEvent;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * InMemory Event Bus Implementation
 * Production systems should use Kafka, RabbitMQ, or similar
 * Supports both synchronous and asynchronous event processing
 */
@Component
public class InMemoryEventBus implements EventBus {
    
    private static final Logger logger = LoggerFactory.getLogger(InMemoryEventBus.class);
    
    private final Map<Class<? extends DomainEvent>, Set<EventHandler<? extends DomainEvent>>> subscribers;
    private final Executor asyncExecutor;
    
    public InMemoryEventBus() {
        this.subscribers = new ConcurrentHashMap<>();
        this.asyncExecutor = Executors.newCachedThreadPool(); // Compatible with Java 17
    }
    
    @Override
    public void publish(DomainEvent event) {
        logger.debug("Publishing event: {}", event);
        
        Class<? extends DomainEvent> eventType = event.getClass();
        Set<EventHandler<? extends DomainEvent>> handlers = subscribers.get(eventType);
        
        if (handlers != null && !handlers.isEmpty()) {
            // Sort handlers by priority
            List<EventHandler<? extends DomainEvent>> sortedHandlers = new ArrayList<>(handlers);
            sortedHandlers.sort(Comparator.comparing(EventHandler::getPriority));
            
            for (EventHandler<? extends DomainEvent> handler : sortedHandlers) {
                processEvent(event, handler);
            }
        } else {
            logger.debug("No handlers registered for event type: {}", eventType.getSimpleName());
        }
    }
    
    @Override
    public void publishAll(List<DomainEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        
        logger.debug("Publishing {} events in sequence", events.size());
        events.forEach(this::publish);
    }
    
    @Override
    public <T extends DomainEvent> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        logger.debug("Subscribing handler {} to event type {}", 
                    handler.getClass().getSimpleName(), eventType.getSimpleName());
        
        subscribers.computeIfAbsent(eventType, k -> ConcurrentHashMap.newKeySet())
                   .add(handler);
    }
    
    @Override
    public <T extends DomainEvent> void unsubscribe(Class<T> eventType, EventHandler<T> handler) {
        logger.debug("Unsubscribing handler {} from event type {}", 
                    handler.getClass().getSimpleName(), eventType.getSimpleName());
        
        Set<EventHandler<? extends DomainEvent>> handlers = subscribers.get(eventType);
        if (handlers != null) {
            handlers.remove(handler);
            
            if (handlers.isEmpty()) {
                subscribers.remove(eventType);
            }
        }
    }
    
    /**
     * Processes event with appropriate handler (sync or async)
     */
    @SuppressWarnings("unchecked")
    private void processEvent(DomainEvent event, EventHandler<? extends DomainEvent> handler) {
        try {
            if (handler.isAsynchronous()) {
                // Asynchronous processing for noncritical handlers
                asyncExecutor.execute(() -> {
                    try {
                        ((EventHandler<DomainEvent>) handler).handle(event);
                        logger.debug("Async event processed: {} by {}", 
                                   event.getEventType(), handler.getClass().getSimpleName());
                    } catch (Exception e) {
                        logger.error("Async event processing failed: {} by {}", 
                                   event.getEventType(), handler.getClass().getSimpleName(), e);
                    }
                });
            } else {
                // Synchronous processing for critical handlers
                ((EventHandler<DomainEvent>) handler).handle(event);
                logger.debug("Sync event processed: {} by {}", 
                           event.getEventType(), handler.getClass().getSimpleName());
            }
        } catch (Exception e) {
            logger.error("Event processing failed: {} by {}", 
                       event.getEventType(), handler.getClass().getSimpleName(), e);
            // In production: implement dead letter queue or retry mechanism
        }
    }
    
    /**
     * Get statistics about registered handlers (for monitoring)
     */
    public Map<String, Integer> getSubscriberStats() {
        Map<String, Integer> stats = new HashMap<>();
        subscribers.forEach((eventType, handlers) ->
            stats.put(eventType.getSimpleName(), handlers.size()));
        return stats;
    }
}










