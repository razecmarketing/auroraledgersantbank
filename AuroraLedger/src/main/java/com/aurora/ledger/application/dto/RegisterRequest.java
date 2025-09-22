package com.aurora.ledger.application.dto;

import lombok.Builder;
import lombok.Value;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * RegisterRequest DTO
 * Input boundary for user registration
 * 
 * Following Clean Architecture principles:
 *  Registrationspecific validation
 *  Immutable securityfocused design
 *  LGPD compliant structure
 * 

 * @compliance LGPD + Banking Security Standards
 */
@Value
@Builder
public class RegisterRequest {
    
    @NotBlank(message = "Username is required")
    String username;
    
    @NotBlank(message = "Password is required")
    String password;
    
    @NotBlank(message = "Email is required")
    String email;
    
    @NotBlank(message = "Customer CPF is required")
    @Pattern(regexp = "\\d{11}", message = "CPF must contain exactly 11 digits")
    String customerCpf;
}









