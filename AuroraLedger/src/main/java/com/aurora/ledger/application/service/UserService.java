package com.aurora.ledger.application.service;

import com.aurora.ledger.domain.user.User;
import com.aurora.ledger.domain.user.UserRegistrationRequest;
import com.aurora.ledger.infrastructure.repository.UserRepository;
import com.aurora.ledger.infrastructure.validation.ContinuousQualityValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Service
 * Handles user registration, authentication and management
 */
@Service
@Transactional
public class UserService {
    
    private static final String SERVICE_NAME = "UserService";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found or inactive";
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ContinuousQualityValidator qualityValidator;
    
    public UserService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      ContinuousQualityValidator qualityValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.qualityValidator = qualityValidator;
    }
    
    /**
     * Register a new user with CPF validation
     */
    public User registerUser(UserRegistrationRequest request) {
        qualityValidator.validateCodeQuality(SERVICE_NAME, "registerUser");
        
        // Validate request
        if (!request.isValid()) {
            throw new IllegalArgumentException("Invalid registration data");
        }
        
        // Validate CPF
        if (!User.isValidCPF(request.getDocument())) {
            throw new IllegalArgumentException("Invalid CPF format");
        }
        
        // Check if login already exists
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new IllegalArgumentException("Login already exists");
        }
        
        // Check if CPF already exists
        if (userRepository.existsByDocument(request.getDocument())) {
            throw new IllegalArgumentException("CPF already registered");
        }
        
        // Create new user with encrypted password
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(
            request.getFullName(),
            request.getDocument(),
            request.getLogin(),
            encodedPassword
        );
        
        User savedUser = userRepository.save(user);
        qualityValidator.validateCodeQuality(SERVICE_NAME, "userRegistered");
        
        return savedUser;
    }
    
    /**
     * Find user by login
     */
    @Transactional(readOnly = true)
    public User findByLogin(String login) {
        qualityValidator.validateCodeQuality(SERVICE_NAME, "findByLogin");
        return userRepository.findByLoginAndActiveTrue(login)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_MESSAGE));
    }
    
    /**
     * Update user last login timestamp
     */
    public void updateLastLogin(String login) {
        qualityValidator.validateCodeQuality(SERVICE_NAME, "updateLastLogin");
        User user = userRepository.findByLoginAndActiveTrue(login)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_MESSAGE));
        user.updateLastLogin();
        userRepository.save(user);
    }
    
    /**
     * Validate user credentials
     */
    @Transactional(readOnly = true)
    public boolean validateCredentials(String login, String rawPassword) {
        qualityValidator.validateCodeQuality(SERVICE_NAME, "validateCredentials");
        
        try {
            User user = userRepository.findByLoginAndActiveTrue(login).orElse(null);
            if (user == null) {
                return false;
            }
            
            return passwordEncoder.matches(rawPassword, user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Deactivate user account
     */
    public void deactivateUser(String login) {
        qualityValidator.validateCodeQuality(SERVICE_NAME, "deactivateUser");
        User user = userRepository.findByLoginAndActiveTrue(login)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_MESSAGE));
        user.deactivate();
        userRepository.save(user);
    }
}
