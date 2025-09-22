package com.aurora.ledger.interfaces.rest;

/**
 * Exception thrown when REST operations fail at controller level.
 * Provides HTTPspecific context and correlation tracking.
 */
public class RestOperationException extends RuntimeException {
    
    private final String operation;
    private final String correlationId;
    private final int httpStatus;
    
    public RestOperationException(String operation, String correlationId, 
                                int httpStatus, String message, Throwable cause) {
        super(String.format("REST operation '%s' failed [correlation: %s]: %s", 
            operation, correlationId, message), cause);
        this.operation = operation;
        this.correlationId = correlationId;
        this.httpStatus = httpStatus;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    public int getHttpStatus() {
        return httpStatus;
    }
}










