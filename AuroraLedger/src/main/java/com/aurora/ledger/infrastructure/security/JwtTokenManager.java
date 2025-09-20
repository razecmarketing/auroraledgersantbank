package com.aurora.ledger.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

/**
 * JWT Token Manager
 * Handles JWT token generation, validation and extraction
 */
@Component
public class JwtTokenManager {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenManager.class);
    private static final long JWT_TOKEN_VALIDITY = 24L * 60 * 60; // 24 hours in seconds
    private static final String TOKEN_TYPE = "Bearer ";
    private static final String DEFAULT_SECRET_BASE64 = "BGXimV7fBu1SkeHnh4+4DGSP73bBCQ1RfpOj3/vpFgldayY9+wHIXgYJP/+Iyu5F8tuIi5AlRlXboRKbfCxJvw==";
    private static final int MIN_SECRET_LENGTH_BYTES = 64; // HS512 requires >= 512 bits

    @Value("${jwt.secret:}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        byte[] keyBytes = resolveSecretBytes();

        if (keyBytes.length < MIN_SECRET_LENGTH_BYTES) {
            throw new IllegalStateException("JWT secret must be at least 512 bits (64 bytes) long. Configure property 'jwt.secret' with a Base64-encoded key.");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] resolveSecretBytes() {
        String secret = (jwtSecret == null || jwtSecret.isBlank()) ? DEFAULT_SECRET_BASE64 : jwtSecret.trim();

        try {
            byte[] decoded = Base64.getDecoder().decode(secret);
            if (jwtSecret == null || jwtSecret.isBlank()) {
                logger.warn("Property 'jwt.secret' not defined. Falling back to embedded development key. Configure a dedicated Base64 secret for production.");
            }
            return decoded;
        } catch (IllegalArgumentException ex) {
            logger.warn("Property 'jwt.secret' is not Base64 encoded. Falling back to UTF-8 bytes. Provide a Base64 encoded secret to avoid interpretation issues.");
            return secret.getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * Generate JWT token for authenticated user
     */
    public String generateToken(String username) {
        return createToken(username);
    }

    /**
     * Create JWT token with claims and expiration
     */
    private String createToken(String subject) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract username from JWT token
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from JWT token
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim from JWT token
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from JWT token
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if JWT token is expired
     */
    public boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Validate JWT token against username and expiration
     */
    public boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (username.equals(tokenUsername) && !isTokenExpired(token));
    }

    /**
     * Extract token from Authorization header
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(TOKEN_TYPE)) {
            return authHeader.substring(TOKEN_TYPE.length());
        }
        return null;
    }

    /**
     * Create Authorization header value
     */
    public String createAuthorizationHeader(String token) {
        return TOKEN_TYPE + token;
    }
}
