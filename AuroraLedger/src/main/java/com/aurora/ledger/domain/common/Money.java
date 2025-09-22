package com.aurora.ledger.domain.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Immutable monetary value with currency precision and operations.
 * Implements proper decimal handling for financial calculations.
 */
public final class Money {
    
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    
    private final BigDecimal amount;
    private final Currency currency;
    
    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount.setScale(SCALE, ROUNDING);
        this.currency = currency;
    }
    
    public static Money of(BigDecimal amount, Currency currency) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        return new Money(amount, currency);
    }
    
    public static Money of(String amount, String currencyCode) {
        return of(new BigDecimal(amount), Currency.getInstance(currencyCode));
    }
    
    public static Money brl(String amount) {
        return of(amount, "BRL");
    }
    
    public static Money usd(String amount) {
        return of(amount, "USD");
    }
    
    public static Money zero(Currency currency) {
        return of(BigDecimal.ZERO, currency);
    }
    
    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }
    
    public Money multiply(BigDecimal multiplier) {
        Objects.requireNonNull(multiplier, "Multiplier cannot be null");
        return new Money(this.amount.multiply(multiplier), this.currency);
    }
    
    public Money divide(BigDecimal divisor) {
        Objects.requireNonNull(divisor, "Divisor cannot be null");
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return new Money(this.amount.divide(divisor, SCALE, ROUNDING), this.currency);
    }
    
    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }
    
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }
    
    public boolean isGreaterThanOrEqual(Money other) {
        requireSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public Currency getCurrency() {
        return currency;
    }
    
    private void requireSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                String.format("Currency mismatch: %s vs %s", 
                    this.currency.getCurrencyCode(), 
                    other.currency.getCurrencyCode())
            );
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Money money = (Money) obj;
        return Objects.equals(amount, money.amount) && 
               Objects.equals(currency, money.currency);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
    
    @Override
    public String toString() {
        return String.format("%s %s", currency.getCurrencyCode(), amount);
    }
}










