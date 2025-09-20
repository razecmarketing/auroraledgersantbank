package com.aurora.ledger.domain.user;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * User Domain Entity - Banking Customer
 * Represents a registered banking customer with authentication capabilities
 * Following Uncle Bob's Clean Code principles and DDD patterns
 * 
 * @author Aurora Ledger Engineering Team
 * @pattern Domain Entity + Aggregate Root
 * @invariants User must have valid CPF, unique login, encrypted password
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;
    
    @Column(name = "document", nullable = false, unique = true, length = 11)
    private String document;
    
    @Column(name = "login", nullable = false, unique = true, length = 50)
    private String login;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "active", nullable = false)
    private boolean active = true;

    // Default constructor
    protected User() {}

    // Constructor for user registration
    public User(String fullName, String document, String login, String password) {
        this.fullName = fullName;
        this.document = document;
        this.login = login;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    /**
     * Validates Brazilian CPF document number
     * Implements the official CPF validation algorithm
     */
    public static boolean isValidCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return false;
        }
        
        // Check if all digits are the same
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        try {
            // Calculate first check digit
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int firstCheckDigit = 11 - (sum % 11);
            if (firstCheckDigit >= 10) {
                firstCheckDigit = 0;
            }
            
            // Calculate second check digit
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int secondCheckDigit = 11 - (sum % 11);
            if (secondCheckDigit >= 10) {
                secondCheckDigit = 0;
            }
            
            // Validate check digits
            return firstCheckDigit == Character.getNumericValue(cpf.charAt(9)) &&
                   secondCheckDigit == Character.getNumericValue(cpf.charAt(10));
                   
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Updates last login timestamp
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    /**
     * Deactivates user account
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Activates user account
     */
    public void activate() {
        this.active = true;
    }

    // UserDetails implementation for Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Account expires if inactive for more than 365 days
        return active && (lastLogin == null || 
               lastLogin.isAfter(LocalDateTime.now().minusDays(365)));
    }

    @Override
    public boolean isAccountNonLocked() {
        // Account is locked if explicitly deactivated
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Credentials expire if password is older than 90 days (simplified check)
        return active && createdAt.isAfter(LocalDateTime.now().minusDays(90));
    }

    @Override
    public boolean isEnabled() {
        // Account is enabled based on active flag and creation time validation
        return active && createdAt != null;
    }

    // Getters following Clean Code naming conventions
    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDocument() {
        return document;
    }
    public String getLogin() {
        return getUsername();
    }
    /**
     * Returns login formatted with banking domain suffix for presentation purposes.
     */
    public String getLoginWithDomain() {
        return login + "@aurora-banking.com";
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public boolean isActive() {
        // Business logic - returns active status with null safety
        return Boolean.TRUE.equals(active);
    }

    // Business methods following Clean Code principles
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", document='" + document + '\'' +
                ", login='" + login + '\'' +
                ", createdAt=" + createdAt +
                ", active=" + active +
                '}';
    }
}


