package com.aurora.ledger.infrastructure.bus;

import com.aurora.ledger.application.query.Query;
import com.aurora.ledger.application.query.QueryHandler;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Query Bus Implementation
 * Following CQRS Read Side - routes queries to appropriate handlers
 * Pure functions - no side effects, only data retrieval
 */
@Component
public class QueryBus {
    
    private static final Logger logger = LoggerFactory.getLogger(QueryBus.class);
    
    private final Map<Class<? extends Query<?>>, QueryHandler<? extends Query<?>, ?>> handlers;
    
    public QueryBus() {
        this.handlers = new ConcurrentHashMap<>();
    }
    
    /**
     * Executes query and returns result
     * Following Bertrand Meyer's Query principle - no side effects
     */
    @SuppressWarnings("unchecked")
    public <R> R dispatch(Query<R> query) {
        logger.debug("Dispatching query: {} with parameters: {}", 
                    query.getClass().getSimpleName(), query.getParameters());
        
        // Find appropriate handler
        QueryHandler<Query<R>, R> handler = (QueryHandler<Query<R>, R>) handlers.get(query.getClass());
        
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for query type: " + 
                                             query.getClass().getSimpleName());
        }
        
        try {
            // Execute query and return result
            R result = handler.handle(query);
            logger.debug("Query executed successfully: {}", query.getQueryId());
            return result;
            
        } catch (Exception e) {
            // Rethrow with rich context; avoid double-logging here
            throw new QueryExecutionException(
                "Failed to execute query '" + query.getClass().getSimpleName() + "' with id=" + query.getQueryId(), e
            );
        }
    }
    
    /**
     * Registers query handler
     */
    public <Q extends Query<R>, R> void register(QueryHandler<Q, R> handler) {
        Class<Q> queryType = handler.getQueryType();
        
        if (handlers.containsKey(queryType)) {
            throw new IllegalStateException("Handler already registered for query type: " + 
                                          queryType.getSimpleName());
        }
        
        handlers.put(queryType, handler);
        logger.info("Registered query handler: {} for {}", 
                   handler.getClass().getSimpleName(), queryType.getSimpleName());
    }
    
    /**
     * Unregisters query handler
     */
    public <Q extends Query<?>> void unregister(Class<Q> queryType) {
        QueryHandler<? extends Query<?>, ?> removed = handlers.remove(queryType);
        if (removed != null) {
            logger.info("Unregistered query handler for: {}", queryType.getSimpleName());
        }
    }
    
    /**
     * Gets registered query types (for monitoring)
     */
    public java.util.Set<Class<?>> getRegisteredQueryTypes() {
        return new java.util.HashSet<>(handlers.keySet());
    }
    
    /**
     * Custom exception for query execution failures
     */
    public static class QueryExecutionException extends RuntimeException {
        public QueryExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
