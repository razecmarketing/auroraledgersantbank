package com.aurora.ledger.infrastructure.config;

import com.aurora.ledger.application.transaction.handler.DepositCommandHandler;
import com.aurora.ledger.application.transaction.handler.PayBillCommandHandler;
import com.aurora.ledger.application.transaction.query.BalanceQueryHandler;
import com.aurora.ledger.infrastructure.bus.CommandBus;
import com.aurora.ledger.infrastructure.bus.QueryBus;
import com.aurora.ledger.infrastructure.event.EventBus;
import com.aurora.ledger.infrastructure.event.handler.BillPaidEventHandler;
import com.aurora.ledger.infrastructure.event.handler.MoneyDepositedEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CQRS Configuration - The Orchestration of Computational Responsibility
 * "The art of programming is the skill of controlling complexity." - Edsger W. Dijkstra
 * 
 * ARCHITECTURAL PHILOSOPHY (Greg Young + Martin Fowler):
 * This configuration represents the pinnacle of software architecture evolution -
 * the recognition that reads and writes have fundamentally different characteristics
 * and should be optimized independently. CQRS isn't just a pattern; it's a solution
 * to the impedance mismatch between how we model business logic and how we optimize
 * for different access patterns.
 * 
 * COMMAND-QUERY RESPONSIBILITY SEGREGATION MASTERY:
 * - Commands: State-changing operations optimized for consistency and business rules
 * - Queries: State-reading operations optimized for performance and user experience  
 * - Events: The immutable truth that bridges both sides through eventual consistency
 * 
 * DEPENDENCY INJECTION WISDOM (Fowler + Uncle Bob):
 * Rather than scattered handler registrations throughout the codebase, this configuration
 * centralizes the wiring of our CQRS infrastructure. Following the Single Responsibility
 * Principle, each handler focuses on one specific business capability.
 * 
 * EVENT-DRIVEN ARCHITECTURE (Leslie Lamport + Distributed Systems):
 * Events represent facts that have occurred - they cannot be denied or changed.
 * This immutability provides the foundation for:
 * - Audit trails for financial compliance
 * - Event replay for system recovery
 * - Temporal queries for business intelligence
 * - Distributed consistency across microservices
 * 
 * MODULAR DESIGN (David Parnas Information Hiding):
 * Each handler encapsulates specific business logic behind well-defined interfaces.
 * Changes to command processing logic don't affect query optimization, and vice versa.
 * This separation enables independent scaling and evolution of system components.
 * 
 * INVERSION OF CONTROL (Uncle Bob Dependency Inversion):
 * High-level business logic doesn't depend on low-level infrastructure concerns.
 * The command bus, query bus, and event bus are abstractions that allow us to
 * swap implementations (in-memory, distributed, async) without changing business code.
 * 
 * SYSTEMS THINKING (Jeff Dean + Kay Object-Oriented Philosophy):
 * This configuration creates a message-passing system where objects collaborate
 * through well-defined protocols. Each handler is an autonomous computational
 * entity that responds to specific message types.
 * 
 * @author Aurora Ledger Engineering Team
 * @pattern CQRS + Event Sourcing + Dependency Injection + Command Pattern
 * @scalability Horizontal scaling through independent read/write optimization
 * @maintainability Clear separation of concerns enables independent evolution
 */
@Configuration
public class CqrsConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(CqrsConfiguration.class);
    
    /**
     * Register all CQRS handlers on application startup
     * Ensures proper routing of commands, queries, and events
     */
    @Bean
    public CommandLineRunner registerCqrsHandlers(CommandBus commandBus, 
                                                 QueryBus queryBus,
                                                 EventBus eventBus,
                                                 DepositCommandHandler depositHandler,
                                                 PayBillCommandHandler billHandler,
                                                 BalanceQueryHandler balanceHandler,
                                                 MoneyDepositedEventHandler depositEventHandler,
                                                 BillPaidEventHandler billEventHandler) {
        
        return args -> {
            logger.info("Registering CQRS handlers...");
            
            // Register Command Handlers
            commandBus.register(depositHandler);
            commandBus.register(billHandler);
            logger.info("Command handlers registered: {} handlers", 2);
            
            // Register Query Handlers  
            queryBus.register(balanceHandler);
            logger.info("Query handlers registered: {} handlers", 1);
            
            // Register Event Handlers
            eventBus.subscribe(depositEventHandler.getEventType(), depositEventHandler);
            eventBus.subscribe(billEventHandler.getEventType(), billEventHandler);
            logger.info("Event handlers registered: {} handlers", 2);
            
            logger.info("CQRS system fully initialized - Commands, Queries, and Events ready!");
        };
    }
}
