package com.aurora.ledger.application.dto;

import lombok.Builder;
import lombok.Value;

/**
 * JwtResponse DTO
 * Output boundary for authentication operations
 * 
 * Following Clean Architecture principles:
 *  JWT token encapsulation
 *  Immutable response structure
 *  Securityfocused design
 * 

 * @compliance JWT + Banking Security Standards
 */
@Value
@Builder
public class JwtResponse {
    
    String token;
    String type;
    String login;
}









