package com.aurora.ledger.infrastructure.aspects;

/**
 * Exception thrown when audit-wrapped operations fail.
 * Provides contextual information for debugging and monitoring.
 */
public class AuditedOperationException extends RuntimeException {
    
    private final String className;
    private final String methodName;
    private final long executionTimeMs;
    
    public AuditedOperationException(String className, String methodName, 
                                   long executionTimeMs, String message, Throwable cause) {
        super(String.format("Operation failed in %s.%s (%dms): %s", 
            className, methodName, executionTimeMs, message), cause);
        this.className = className;
        this.methodName = methodName;
        this.executionTimeMs = executionTimeMs;
    }
    
    public String getClassName() {
        return className;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
}
