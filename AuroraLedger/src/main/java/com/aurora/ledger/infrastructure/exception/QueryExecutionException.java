package com.aurora.ledger.infrastructure.exception;

/**
 * Query Execution Exception
 * Thrown when query processing fails in the CQRS Query Bus
 */
public class QueryExecutionException extends RuntimeException {
    
    public QueryExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public QueryExecutionException(String message) {
        super(message);
    }
}










