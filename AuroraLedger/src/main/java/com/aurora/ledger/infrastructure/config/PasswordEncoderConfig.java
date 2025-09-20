package com.aurora.ledger.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password Encoder Configuration
 * Separate configuration to avoid circular dependencies
 * 
 * This configuration is separated from SecurityConfig to prevent circular dependency:
 * SecurityConfig -> JwtAuthenticationFilter -> UserService -> PasswordEncoder -> SecurityConfig
 * 
 * @author Aurora Ledger Engineering Team
 */
@Configuration
public class PasswordEncoderConfig {
    
    /**
     * BCrypt password encoder with cost factor 12 for secure password hashing
     * 
     * BCrypt Cost Factor 12:
     * - Provides strong protection against brute force attacks
     * - Balances security with performance (approx 300ms per hash)
     * - Meets current security standards for financial applications
     * - Future-proof against hardware improvements
     * 
     * @return BCryptPasswordEncoder instance with cost factor 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
