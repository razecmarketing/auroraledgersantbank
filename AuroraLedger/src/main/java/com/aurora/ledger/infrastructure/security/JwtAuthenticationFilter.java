package com.aurora.ledger.infrastructure.security;

import com.aurora.ledger.application.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * Intercepts requests to validate JWT tokens and set authentication context
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenManager jwtTokenManager;
    private final UserService userService;
    
    public JwtAuthenticationFilter(JwtTokenManager jwtTokenManager, UserService userService) {
        this.jwtTokenManager = jwtTokenManager;
        this.userService = userService;
    }
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain chain) throws ServletException, IOException {
        
        final String requestTokenHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwtToken = null;
        
        // Extract JWT token from Authorization header
        if (requestTokenHeader != null) {
            jwtToken = jwtTokenManager.extractTokenFromHeader(requestTokenHeader);
            if (jwtToken != null) {
                try {
                    username = jwtTokenManager.getUsernameFromToken(jwtToken);
                } catch (Exception e) {
                    logger.warn("Unable to get JWT Token username", e);
                }
            } else {
                logger.warn("JWT Token does not begin with Bearer String");
            }
        }
        
        // Validate token and set authentication context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userService.findByLogin(username);
                
                if (jwtTokenManager.validateToken(jwtToken, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                logger.warn("Authentication failed for user: " + username, e);
            }
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip JWT filter for public endpoints
        return path.startsWith("/api/auth/") || 
               path.startsWith("/api/cezi-cola/") ||
               path.startsWith("/actuator/health") ||
               path.startsWith("/h2-console/");
    }
}
