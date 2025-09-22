package com.aurora.ledger.application.dto;

import lombok.Builder;
import lombok.Value;

import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest DTO
 * Input boundary for user authentication
 * 
 * Following Clean Architecture principles:
 *  Authenticationspecific validation
 *  Immutable securityfocused design
 *  PCI DSS compliant structure
 * 
 * @compliance PCI DSS + Banking Security Standards
 */
@Value
@Builder
public class LoginRequest {
    
    @NotBlank(message = "Username is required")
    String username;
    
    @NotBlank(message = "Password is required")
    String password;
}









