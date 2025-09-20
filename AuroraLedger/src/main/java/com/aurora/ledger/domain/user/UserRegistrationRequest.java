package com.aurora.ledger.domain.user;

/**
 * User Registration Request DTO
 * Data Transfer Object for user registration following Clean Code principles
 * Implements validation and immutability patterns recommended by Uncle Bob
 * 
 * @author Aurora Ledger Engineering Team
 * @pattern Data Transfer Object + Value Object
 */
public class UserRegistrationRequest {
    
    private String fullName;
    private String document;
    private String login;
    private String password;

    // Default constructor for framework compatibility
    public UserRegistrationRequest() {}

    // Constructor following Clean Code naming conventions
    public UserRegistrationRequest(String fullName, String document, String login, String password) {
        this.fullName = fullName;
        this.document = document;
        this.login = login;
        this.password = password;
    }

    // Getters and setters with English names
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Validates the registration request using business rules
     * Following Uncle Bob's Clean Code validation principles
     */
    public boolean isValid() {
        return fullName != null && !fullName.trim().isEmpty() &&
               document != null && User.isValidCPF(document) &&
               login != null && !login.trim().isEmpty() &&
               password != null && password.length() >= 6;
    }

    @Override
    public String toString() {
        return "UserRegistrationRequest{" +
                "fullName='" + fullName + '\'' +
                ", document='" + document + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}
