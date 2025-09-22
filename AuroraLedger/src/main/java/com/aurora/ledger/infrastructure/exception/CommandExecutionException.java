package com.aurora.ledger.infrastructure.exception;

/**
 * Command Execution Exception
 * Thrown when command processing fails in the CQRS Write Side
 */
public class CommandExecutionException extends RuntimeException {
    
    private final String commandId;
    private final String commandType;
    
    public CommandExecutionException(String commandId, String commandType, String message, Throwable cause) {
        super(String.format("Command execution failed  ID: %s, Type: %s, Error: %s", 
                          commandId, commandType, message), cause);
        this.commandId = commandId;
        this.commandType = commandType;
    }
    
    public String getCommandId() {
        return commandId;
    }
    
    public String getCommandType() {
        return commandType;
    }
}










