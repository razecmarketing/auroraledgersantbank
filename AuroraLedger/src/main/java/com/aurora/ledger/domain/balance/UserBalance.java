package com.aurora.ledger.domain.balance;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * User Balance Entity
 * Domain aggregate for user account balance
 * Implements business rules for balance management and negativation
 */
@Entity
@Table(name = "user_balance")
public class UserBalance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_login", nullable = false, unique = true, length = 50)
    private String userLogin;
    
    @Column(name = "current_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentBalance;
    
    @Column(name = "negative_balance", precision = 15, scale = 2)
    private BigDecimal negativeBalance;
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    @Column(name = "last_negative_date")
    private LocalDateTime lastNegativeDate;
    
    @Version
    private Long version; // Optimistic locking
    
    // Default constructor for JPA
    protected UserBalance() {}
    
    // Constructor for new account
    public UserBalance(String userLogin) {
        this.userLogin = userLogin;
        this.currentBalance = BigDecimal.ZERO;
        this.negativeBalance = BigDecimal.ZERO;
        this.lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Deposit money with interest calculation for negative balances
     * Implements 1.02% interest rule from requirements
     */
    public void deposit(BigDecimal amount) {
        validateAmount(amount);
        
        if (isNegative()) {
            // Apply 1.02% interest to negative balance
            BigDecimal interestRate = new BigDecimal("1.02");
            BigDecimal negativeWithInterest = negativeBalance.multiply(interestRate);
            
            // Pay off negative balance first
            BigDecimal remainingAmount = amount.subtract(negativeWithInterest);
            
            if (remainingAmount.compareTo(BigDecimal.ZERO) >= 0) {
                // Deposit covers negative balance + interest
                this.negativeBalance = BigDecimal.ZERO;
                this.currentBalance = remainingAmount;
                this.lastNegativeDate = null;
            } else {
                // Deposit doesn't cover full negative balance
                this.negativeBalance = negativeWithInterest.subtract(amount);
                this.currentBalance = BigDecimal.ZERO;
            }
        } else {
            // Normal deposit
            this.currentBalance = this.currentBalance.add(amount);
        }
        
        this.lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Pay bill - allows negativation if insufficient balance
     */
    public void payBill(BigDecimal amount) {
        validateAmount(amount);
        
        if (currentBalance.compareTo(amount) >= 0) {
            // Sufficient balance
            this.currentBalance = this.currentBalance.subtract(amount);
        } else {
            // Insufficient balance - go negative
            BigDecimal shortfall = amount.subtract(currentBalance);
            this.currentBalance = BigDecimal.ZERO;
            this.negativeBalance = this.negativeBalance.add(shortfall);
            this.lastNegativeDate = LocalDateTime.now();
        }
        
        this.lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Get effective balance (positive balance minus negative balance)
     */
    public BigDecimal getEffectiveBalance() {
        if (isNegative()) {
            return negativeBalance.negate(); // Return negative value
        }
        return currentBalance;
    }
    
    /**
     * Check if account is in negative state
     */
    public boolean isNegative() {
        return negativeBalance.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Factory method to create new balance for user
     */
    public static UserBalance createFor(String userLogin) {
        return new UserBalance(userLogin);
    }
    
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (amount.scale() > 2) {
            throw new IllegalArgumentException("Amount cannot have more than 2 decimal places");
        }
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getUserLogin() {
        return userLogin;
    }
    
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
    
    public BigDecimal getNegativeBalance() {
        return negativeBalance;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public LocalDateTime getLastNegativeDate() {
        return lastNegativeDate;
    }
    
    public Long getVersion() {
        return version;
    }
    
    @Override
    public String toString() {
        return String.format("UserBalance{user='%s', balance=%s, negative=%s, effective=%s}", 
                           userLogin, currentBalance, negativeBalance, getEffectiveBalance());
    }
}
