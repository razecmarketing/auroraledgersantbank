package com.aurora.ledger.application.transaction.command;

import com.aurora.ledger.application.command.AbstractUserCommand;

import java.math.BigDecimal;

/**
 * Deposit Command  The Immutable Intent of Financial State Change
 * "The purpose of abstraction is not to be vague, but to create a new semantic level 
 * in which one can be absolutely precise."  Edsger W. Dijkstra
 * 
 * COMMAND PATTERN MASTERY (Gang of Four + Bertrand Meyer):
 * This command encapsulates a financial business intention as an immutable object.
 * Following Meyer's CommandQuery Separation principle, this command modifies state
 * but never returns business data  its success is measured through event emission.
 * 
 * DOMAIN-DRIVEN DESIGN EXCELLENCE:
 * The DepositCommand represents the ubiquitous language of banking:
 *  Not just "add money"  but a formal financial transaction with audit trail
 *  Amount precision follows banking standards (BigDecimal for monetary calculations)
 *  Correlation IDs enable transaction tracing across distributed systems
 * 
 * TEMPORAL ORDERING & CONSISTENCY:
 * Each command carries its creation timestamp, establishing happenedbefore relationships
 * critical for event replay and distributed system consistency. The commandId provides
 * idempotency guarantees  essential for reliable financial operations.
 * 
 * ALGEBRAIC STRUCTURE (Donald Knuth):
 * Deposit operations form a monoid under composition:
 *  Identity element: deposit of zero amount
 *  Associativity: (deposit A + deposit B) + deposit C = deposit A + (deposit B + deposit C)
 *  Closure: composition of deposits yields another valid deposit
 * 
 * INVARIANT PRESERVATION (Barbara Liskov + LSP):
 * Preconditions: User exists, amount > 0, sufficient system resources
 * Postconditions: Balance increased, event emitted, audit trail updated
 * Invariant: Total system money supply remains conserved
 * 
 * INFORMATION HIDING (David Parnas):
 * Internal command structure hidden from clients  they interact through
 * welldefined interfaces that remain stable even as implementation evolves.
 * 

 * @pattern Command + Value Object + Domain Model
 * @immutability All fields final, no setters, threadsafe by construction
 * @precision BigDecimal ensures no rounding errors in financial calculations
 */
public class DepositCommand extends AbstractUserCommand {
    
    private final String description;
    
    public DepositCommand(String userLogin, BigDecimal amount, String description) {
        super(userLogin, amount);
        this.description = description;
    }
    
    @Override
    protected void validateSpecific() {
        // No additional validation needed for deposits beyond the base validation
    }
    
    @Override
    protected String getAmountErrorMessage() {
        return "Deposit amount must be greater than zero";
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return String.format("DepositCommand{commandId='%s', user='%s', amount=%s, correlation='%s'}", 
                           getCommandId(), getUserLogin(), getAmount(), getCorrelationId());
    }
}










