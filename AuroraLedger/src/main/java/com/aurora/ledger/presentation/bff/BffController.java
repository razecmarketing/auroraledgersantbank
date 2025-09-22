package com.aurora.ledger.presentation.bff;

import com.aurora.ledger.application.service.UserService;
import com.aurora.ledger.application.transaction.service.TransactionService;
import com.aurora.ledger.domain.user.User;
import com.aurora.ledger.domain.user.UserRegistrationRequest;
import com.aurora.ledger.infrastructure.security.JwtTokenManager;
import com.aurora.ledger.infrastructure.validation.ContinuousQualityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bff")
public class BffController {

    private static final Logger logger = LoggerFactory.getLogger(BffController.class);

    private final UserService userService;
    private final JwtTokenManager jwtTokenManager;
    private final AuthenticationManager authenticationManager;
    private final TransactionService transactionService;
    private final ContinuousQualityValidator qualityValidator;

    public BffController(UserService userService,
                         JwtTokenManager jwtTokenManager,
                         AuthenticationManager authenticationManager,
                         TransactionService transactionService,
                         ContinuousQualityValidator qualityValidator) {
        this.userService = userService;
        this.jwtTokenManager = jwtTokenManager;
        this.authenticationManager = authenticationManager;
        this.transactionService = transactionService;
        this.qualityValidator = qualityValidator;
    }

    //  Auth endpoints (BFF) 
    @PostMapping("/auth/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        qualityValidator.validateCodeQuality("BffController", "registerUser");
        try {
            User user = userService.registerUser(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("userId", user.getId());
            response.put("login", user.getLogin());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Error during registration", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Server error during registration");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        qualityValidator.validateCodeQuality("BffController", "login");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getLogin(),
                            loginRequest.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                String token = jwtTokenManager.generateToken(loginRequest.getLogin());
                userService.updateLastLogin(loginRequest.getLogin());

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("token", token);
                response.put("tokenType", "Bearer");
                response.put("login", loginRequest.getLogin());

                return ResponseEntity.ok(response);
            } else {
                throw new AuthenticationException("Authentication failed") {};
            }
        } catch (AuthenticationException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid login credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error during login", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Internal server error during login");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    //  Transaction endpoints (BFF) 
    @PostMapping("/transactions/deposit")
    public ResponseEntity<Map<String, Object>> deposit(@RequestBody DepositRequest request) {
        String userLogin = currentUserLogin();
        transactionService.depositMoney(userLogin, request.amount, request.description);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "Deposit accepted");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/transactions/pay")
    public ResponseEntity<Map<String, Object>> pay(@RequestBody PayRequest request) {
        String userLogin = currentUserLogin();
        transactionService.payBill(userLogin, request.amount, request.description);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "Payment accepted");
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/transactions/balancerequiredformat")
    public ResponseEntity<Object> getBalanceWithHistory() {
        String userLogin = currentUserLogin();
        Object result = transactionService.getBalanceWithHistory(userLogin);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/transactions/summary")
    public ResponseEntity<Object> getBalanceSummary() {
        String userLogin = currentUserLogin();
        Object result = transactionService.getBalance(userLogin);
        return ResponseEntity.ok(result);
    }

    private String currentUserLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return "anonymous";
        return auth.getName();
    }

    //  DTOs 
    public static class LoginRequest {
        private String login;
        private String password;

        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class DepositRequest {
        public BigDecimal amount;
        public String description;
    }

    public static class PayRequest {
        public BigDecimal amount;
        public String description;
    }
}










