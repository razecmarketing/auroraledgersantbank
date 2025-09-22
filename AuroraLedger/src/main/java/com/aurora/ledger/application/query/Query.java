package com.aurora.ledger.application.query;

/**
 * Query Interface
 * Following Bertrand Meyer's CommandQuery Separation
 * Queries return data, never modify state
 */
public interface Query<T> {
    
    /**
     * Query identifier for caching and performance monitoring
     */
    String getQueryId();
    
    /**
     * Query parameters for result filtering and pagination
     */
    QueryParameters getParameters();
    
    /**
     * Expected result type  helps with type safety and serialization
     */
    Class<T> getResultType();
}










