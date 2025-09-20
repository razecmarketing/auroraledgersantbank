package com.aurora.ledger.infrastructure.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT Authentication Entry Point
 * Handles unauthorized access attempts
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String jsonResponse = """
            {
                "success": false,
                "message": "Unauthorized access - valid JWT token required",
                "error": "UNAUTHORIZED",
                "status": 401
            }
            """;
            
        response.getWriter().write(jsonResponse);
    }
}
