package com.aurora.ledger.application.query;

/**
 * QueryHandler Interface  CQRS Query Handler  
 * Processes queries that read system state without modifications
 * Following CQRS pattern for read/write separation
 */
public interface QueryHandler<Q, R> {
    
    /**
     * Handle query execution and return result
     * Queries should be readonly and cacheable
     */
    R handle(Q query);
    
    /**
     * Get the query type this handler processes
     */
    Class<Q> getQueryType();
}










