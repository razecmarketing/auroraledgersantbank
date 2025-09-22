package com.aurora.ledger.application.cqrs;

import java.util.concurrent.CompletableFuture;


/**
 * Foundation interfaces and base classes for implementing the CQRS pattern.
 * This provides the core components needed to separate read and write operations.
 */
public class CqrsFoundation {

    // Command interfaces
    public interface Command {}

    public interface CommandHandler<C extends Command, R> {
        CompletableFuture<R> handle(C command);
    }

    public interface CommandBus {
        <R> CompletableFuture<R> dispatch(Command command);
    }

    // Query interfaces
    public interface Query {}

    public interface QueryHandler<Q extends Query, R> {
        CompletableFuture<R> handle(Q query);
    }

    public interface QueryBus {
        <R> CompletableFuture<R> dispatch(Query query);
    }

    // Base implementations
    public abstract static class AbstractCommandBus implements CommandBus {
        // Common command bus functionality
    }

    public abstract static class AbstractQueryBus implements QueryBus {
        // Common query bus functionality
    }
}










