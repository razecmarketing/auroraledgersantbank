package com.aurora.ledger.infrastructure.bus;

import com.aurora.ledger.application.command.Command;
import com.aurora.ledger.application.command.CommandHandler;
import com.aurora.ledger.domain.shared.DomainEvent;
import com.aurora.ledger.infrastructure.event.EventBus;
import com.aurora.ledger.infrastructure.exception.CommandExecutionException;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Command Bus Implementation
 * Following CQRS Write Side  routes commands to appropriate handlers
 * Publishes generated events to Event Bus for Query Side consumption
 */
@Component
public class CommandBus {
    
    private static final Logger logger = LoggerFactory.getLogger(CommandBus.class);
    
    private final Map<Class<? extends Command>, CommandHandler<? extends Command>> handlers;
    private final EventBus eventBus;
    
    public CommandBus(EventBus eventBus) {
        this.handlers = new ConcurrentHashMap<>();
        this.eventBus = eventBus;
    }
    
    /**
     * Executes command and publishes resulting events
     * Following Greg Young's CQRS pattern
     */
    @SuppressWarnings("unchecked")
    public void dispatch(Command command) {
        logger.info("Dispatching command: {}", command);
        
        // Validate command before processing
        command.validate();
        
        // Find appropriate handler
        Class<? extends Command> commandType = command.getClass();
        CommandHandler<Command> handler = (CommandHandler<Command>) handlers.get(commandType);
        
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for command type: " + 
                                             commandType.getSimpleName());
        }
        
        try {
            // Execute command and get generated events
            List<DomainEvent> events = handler.handle(command);
            
            // Publish events to Event Bus for Query Side
            if (events != null && !events.isEmpty()) {
                eventBus.publishAll(events);
                logger.info("Command executed successfully: {} generated {} events", 
                           command.getCommandId(), events.size());
            } else {
                logger.info("Command executed successfully: {} (no events generated)", 
                           command.getCommandId());
            }
            
        } catch (Exception e) {
            // Rethrow with contextual information; upstream can log as needed
            throw new CommandExecutionException(command.getCommandId(), 
                                              commandType.getSimpleName(), 
                                              e.getMessage(), e);
        }
    }
    
    /**
     * Registers command handler
     */
    public <T extends Command> void register(CommandHandler<T> handler) {
        Class<T> commandType = handler.getCommandType();
        
        if (handlers.containsKey(commandType)) {
            throw new IllegalStateException("Handler already registered for command type: " + 
                                          commandType.getSimpleName());
        }
        
        handlers.put(commandType, handler);
        logger.info("Registered command handler: {} for {}", 
                   handler.getClass().getSimpleName(), commandType.getSimpleName());
    }
    
    /**
     * Unregisters command handler
     */
    public <T extends Command> void unregister(Class<T> commandType) {
        CommandHandler<? extends Command> removed = handlers.remove(commandType);
        if (removed != null) {
            logger.info("Unregistered command handler for: {}", commandType.getSimpleName());
        }
    }
    
    /**
     * Gets registered command types (for monitoring)
     */
    public java.util.Set<Class<? extends Command>> getRegisteredCommandTypes() {
        return handlers.keySet();
    }
}










