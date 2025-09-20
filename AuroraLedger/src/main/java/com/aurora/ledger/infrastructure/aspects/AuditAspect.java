package com.aurora.ledger.infrastructure.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Performance and audit aspect for banking operations.
 * Provides transparent method execution monitoring and correlation tracking.
 */
@Aspect
@Component
public class AuditAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);
    private static final String CORRELATION_ID = "correlationId";
    private static final String EXECUTION_TIME = "executionTime";
    
    @Around("execution(* com.aurora.ledger.application..*(..))")
    public Object auditApplicationServices(ProceedingJoinPoint joinPoint) throws Throwable {
        return executeWithAudit(joinPoint, "APPLICATION");
    }
    
    @Around("execution(* com.aurora.ledger.domain..*Repository.*(..))")
    public Object auditRepositoryOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        return executeWithAudit(joinPoint, "REPOSITORY");
    }
    
    @Around("execution(* com.aurora.ledger.interfaces.rest..*(..))")
    public Object auditRestControllers(ProceedingJoinPoint joinPoint) throws Throwable {
        return executeWithAudit(joinPoint, "REST");
    }
    
    private Object executeWithAudit(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String correlationId = getOrCreateCorrelationId();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        long startTime = System.currentTimeMillis();
        
        MDC.put(CORRELATION_ID, correlationId);
        MDC.put("layer", layer);
        MDC.put("method", methodName);
        MDC.put("class", className);
        
        try {
            logger.info("Starting execution: {}.{}", className, methodName);
            
            Object result = joinPoint.proceed();
            
            long executionTime = System.currentTimeMillis() - startTime;
            MDC.put(EXECUTION_TIME, String.valueOf(executionTime));
            
            logger.info("Completed execution: {}.{} in {}ms", 
                className, methodName, executionTime);
            
            return result;
            
        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            MDC.put(EXECUTION_TIME, String.valueOf(executionTime));
            
            // Log with full context for audit purposes  
            logger.error("Failed execution: {}.{} in {}ms - Error: {}", 
                className, methodName, executionTime, ex.getMessage(), ex);
            
            // Re-throw with enriched context for upstream handling
            AuditedOperationException enrichedException = new AuditedOperationException(
                className, methodName, executionTime, ex.getMessage(), ex
            );
            
            // Additional logging for monitoring systems
            logger.error("Throwing enriched exception for monitoring: {}", 
                enrichedException.getMessage());
                
            throw enrichedException;
            
        } finally {
            MDC.clear();
        }
    }
    
    private String getOrCreateCorrelationId() {
        String existingId = MDC.get(CORRELATION_ID);
        if (existingId != null) {
            return existingId;
        }
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
