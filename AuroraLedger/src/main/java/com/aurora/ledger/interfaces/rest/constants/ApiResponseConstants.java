package com.aurora.ledger.interfaces.rest.constants;

/**
 * API Response Constants
 * Centralized constants for REST API responses to avoid duplication
 * and ensure consistency across controllers
 */
public final class ApiResponseConstants {
    
    private ApiResponseConstants() {
        // Utility class - prevent instantiation
    }
    
    // Response field names
    public static final String SUCCESS = "success";
    public static final String MESSAGE = "message";
    public static final String ERROR = "error";
    public static final String TIMESTAMP = "timestamp";
    public static final String DATA = "data";
    
    // Success messages
    public static final String DEPOSIT_SUCCESS = "Deposit processed successfully";
    public static final String PAYMENT_SUCCESS = "Bill payment processed successfully";
    public static final String REGISTRATION_SUCCESS = "User registered successfully";
    public static final String LOGIN_SUCCESS = "Login successful";
    
    // Error messages
    public static final String DEPOSIT_FAILED = "Deposit failed: ";
    public static final String PAYMENT_FAILED = "Payment failed: ";
    public static final String HISTORY_FAILED = "Failed to retrieve transaction history";
    public static final String SUMMARY_FAILED = "Failed to retrieve account summary";
    public static final String BALANCE_FAILED = "Failed to retrieve balance";
    public static final String INVALID_CREDENTIALS = "Invalid login credentials";
    public static final String INVALID_TOKEN = "Invalid token format";
    public static final String TOKEN_VALIDATION_FAILED = "Token validation failed";
    public static final String SERVER_ERROR_REGISTRATION = "Internal server error during registration";
    public static final String SERVER_ERROR_LOGIN = "Internal server error during login";
    
    // Error codes
    public static final String DEPOSIT_ERROR = "DEPOSIT_ERROR";
    public static final String PAYMENT_ERROR = "PAYMENT_ERROR";
    public static final String HISTORY_QUERY_ERROR = "HISTORY_QUERY_ERROR";
    public static final String SUMMARY_QUERY_ERROR = "SUMMARY_QUERY_ERROR";
    public static final String BALANCE_QUERY_ERROR = "BALANCE_QUERY_ERROR";
}
