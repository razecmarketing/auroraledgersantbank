package com.aurora.ledger.presentation.controller;

import com.aurora.ledger.application.service.UserService;
import com.aurora.ledger.domain.user.User;
import com.aurora.ledger.domain.user.UserRegistrationRequest;
import com.aurora.ledger.infrastructure.security.JwtTokenManager;
import com.aurora.ledger.infrastructure.validation.ContinuousQualityValidator;
import com.aurora.ledger.interfaces.rest.constants.ApiResponseConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.HashMap;

/**
 * Authentication Controller  User Interface Layer
 * 
 * Handles user registration and authentication operations following Clean Architecture principles.
 * Implements Uncle Bob's Dependency Rule: dependencies point inward toward business logic.
 * 
 * ARCHITECTURAL PATTERNS APPLIED:
 *  Clean Architecture (Uncle Bob): Controllers are adapters in the interface layer
 *  Command Query Separation (Bertrand Meyer): Separate write operations (register) from read operations
 *  DomainDriven Design (Eric Evans): Uses domain objects and ubiquitous language
 * 
 * SECURITY PRINCIPLES (Liskov Substitution + Information Hiding):
 *  JWT token generation abstracted through interface
 *  Password handling follows cryptographic best practices
 *  Input validation prevents injection attacks
 * 

 * @pattern Clean Architecture Interface Adapter
 * @layer Presentation/Interface Layer
 * @dependencies Domain Layer (User entities), Application Layer (UserService)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String LOGIN_KEY = "login";
    private static final String SUCCESS_KEY = "success";
    private static final String MESSAGE_KEY = "message";
    
    private static final String CONTROLLER_NAME = "AuthController";
    
    private final UserService userService;
    private final JwtTokenManager jwtTokenManager;
    private final AuthenticationManager authenticationManager;
    private final ContinuousQualityValidator qualityValidator;
    
    public AuthController(UserService userService,
                         JwtTokenManager jwtTokenManager,
                         AuthenticationManager authenticationManager,
                         ContinuousQualityValidator qualityValidator) {
        this.userService = userService;
        this.jwtTokenManager = jwtTokenManager;
        this.authenticationManager = authenticationManager;
        this.qualityValidator = qualityValidator;
    }
    
    /**
     * Register new user with CPF validation
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        qualityValidator.validateCodeQuality(CONTROLLER_NAME, "registerUser");
        
        try {
            User user = userService.registerUser(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put(ApiResponseConstants.SUCCESS, true);
            response.put(ApiResponseConstants.MESSAGE, ApiResponseConstants.REGISTRATION_SUCCESS);
            response.put("userId", user.getId());
            response.put(LOGIN_KEY, user.getLogin());
            
            qualityValidator.validateCodeQuality(CONTROLLER_NAME, "registrationSuccess");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ApiResponseConstants.SUCCESS, false);
            errorResponse.put(ApiResponseConstants.MESSAGE, e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ApiResponseConstants.SUCCESS, false);
            errorResponse.put(ApiResponseConstants.MESSAGE, ApiResponseConstants.SERVER_ERROR_REGISTRATION);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * User login with JWT token generation
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
    qualityValidator.validateCodeQuality(CONTROLLER_NAME, LOGIN_KEY);
        
        try {
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getLogin(),
                    loginRequest.getPassword()
                )
            );
            
            if (authentication.isAuthenticated()) {
                // Generate JWT token
                String token = jwtTokenManager.generateToken(loginRequest.getLogin());
                
                // Update last login timestamp
                userService.updateLastLogin(loginRequest.getLogin());
                
                Map<String, Object> response = new HashMap<>();
                response.put(SUCCESS_KEY, true);
                response.put(MESSAGE_KEY, "Login successful");
                response.put("token", token);
                response.put("tokenType", "Bearer");
                response.put(LOGIN_KEY, loginRequest.getLogin());
                
                qualityValidator.validateCodeQuality(CONTROLLER_NAME, "loginSuccess");
                return ResponseEntity.ok(response);
            } else {
                throw new AuthenticationException("Authentication failed") {};
            }
            
        } catch (AuthenticationException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(SUCCESS_KEY, false);
            errorResponse.put(MESSAGE_KEY, "Invalid login credentials");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(SUCCESS_KEY, false);
            errorResponse.put(MESSAGE_KEY, "Internal server error during login");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Validate JWT token endpoint
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        qualityValidator.validateCodeQuality(CONTROLLER_NAME, "validateToken");
        
        try {
            String token = jwtTokenManager.extractTokenFromHeader(authHeader);
            if (token != null) {
                String username = jwtTokenManager.getUsernameFromToken(token);
                boolean isValid = jwtTokenManager.validateToken(token, username);
                
                Map<String, Object> response = new HashMap<>();
                response.put(SUCCESS_KEY, true);
                response.put("valid", isValid);
                response.put("username", username);
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put(SUCCESS_KEY, false);
                errorResponse.put(MESSAGE_KEY, "Invalid token format");
                
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(SUCCESS_KEY, false);
            errorResponse.put(MESSAGE_KEY, "Token validation failed");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    /**
     * Login Request DTO with English naming conventions
     */
    public static class LoginRequest {
        private String login;
        private String password; // Changed from 'senha' to 'password'
        
        public LoginRequest() {}
        
        public LoginRequest(String login, String password) {
            this.login = login;
            this.password = password;
        }
        
        public String getLogin() {
            return login;
        }
        
        public void setLogin(String login) {
            this.login = login;
        }
        
        public String getPassword() { // Changed from getSenha
            return password;
        }
        
        public void setPassword(String password) { // Changed from setSenha
            this.password = password;
        }
    }
}










