package com.aurora.ledger.application.command;

import com.aurora.ledger.domain.shared.DomainEvent;

import java.util.List;

/**
 * CommandHandler Interface - CQRS Command Handler
 * Processes commands that modify system state
 * Following Clean Architecture and Domain-Driven Design principles
 */
public interface CommandHandler<T> {
    
    /**
     * Handle command execution and return domain events
     * Commands should be idempotent and produce events
     */
    List<DomainEvent> handle(T command);
    
    /**
     * Get the command type this handler processes
     */
    Class<T> getCommandType();
}
