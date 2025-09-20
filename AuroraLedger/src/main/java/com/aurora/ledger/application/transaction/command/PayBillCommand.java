package com.aurora.ledger.application.transaction.command;

import com.aurora.ledger.application.command.AbstractUserCommand;

import java.math.BigDecimal;

/**
 * Pay Bill Command - CQRS Write Side
 * Command to pay bills with negativation and 1.02% interest logic
 * Following CQRS principle: Commands change state, never return data
 */
public class PayBillCommand extends AbstractUserCommand {
    
    private final String billDescription;
    
    public PayBillCommand(String userLogin, BigDecimal amount, String billDescription) {
        super(userLogin, amount);
        this.billDescription = billDescription;
    }
    
    @Override
    protected void validateSpecific() {
        if (billDescription == null || billDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Bill description cannot be null or empty");
        }
    }
    
    @Override
    protected String getAmountErrorMessage() {
        return "Bill payment amount must be greater than zero";
    }
    
    public String getBillDescription() {
        return billDescription;
    }
    
    @Override
    public String toString() {
        return String.format("PayBillCommand{commandId='%s', user='%s', amount=%s, bill='%s', correlation='%s'}", 
                           getCommandId(), getUserLogin(), getAmount(), billDescription, getCorrelationId());
    }
}
