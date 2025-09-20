package com.aurora.ledger.application.command;

/**
 * Command Base Interface
 * Following Greg Young's CQRS - Commands represent intent to change state
 * Command-Query Separation: Commands modify state, never return data
 */
public interface Command {
    
    /**
     * Command identifier for tracking and correlation
     */
    String getCommandId();
    
    /**
     * Timestamp when command was created
     */
    java.time.LocalDateTime getCreatedAt();
    
    /**
     * User or system that issued the command
     */
    String getIssuedBy();
    
    /**
     * Command validation - ensures command can be executed
     * Fails fast principle: validate before attempting execution
     */
    void validate();
}
