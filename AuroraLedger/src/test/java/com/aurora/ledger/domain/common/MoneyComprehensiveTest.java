package com.aurora.ledger.domain.common;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive Money Domain Test Suite
 * 
 * Coverage Target: 100% Money value object
 * Tests all monetary operations, validations, and edge cases
 * Validates financial precision and currency handling
 * 
 * Test Categories:
 * - Money creation and factory methods
 * - Arithmetic operations with precision
 * - Comparison operations and contracts
 * - Currency validation and mixing
 * - Equality and hash code contracts
 * - Immutability guarantees
 * - String representation
 * - Edge cases and boundary conditions
 * 
 * Execution: mvn test -Dtest=MoneyComprehensiveTest
 */
@DisplayName("Money Domain - Comprehensive Test Suite")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MoneyComprehensiveTest {
    
    private static final Currency BRL = Currency.getInstance("BRL");
    private static final Currency USD = Currency.getInstance("USD");
    
    @Nested
    @DisplayName("Money Construction and Factory Methods")
    @Order(1)
    class MoneyConstructionTests {
        
        @Test
        @DisplayName("Should create Money with BigDecimal and Currency")
        void shouldCreateMoneyWithBigDecimalAndCurrency() {
            // Given
            BigDecimal amount = new BigDecimal("100.50");
            
            // When
            Money money = Money.of(amount, BRL);
            
            // Then
            assertThat(money).isNotNull();
            assertThat(money.getAmount()).isEqualTo(new BigDecimal("100.50"));
            assertThat(money.getCurrency()).isEqualTo(BRL);
        }
        
        @Test
        @DisplayName("Should create Money with string amount and currency code")
        void shouldCreateMoneyWithStringAmountAndCurrencyCode() {
            // When
            Money money = Money.of("250.75", "BRL");
            
            // Then
            assertThat(money).isNotNull();
            assertThat(money.getAmount()).isEqualTo(new BigDecimal("250.75"));
            assertThat(money.getCurrency()).isEqualTo(BRL);
        }
        
        @Test
        @DisplayName("Should create BRL Money using convenience method")
        void shouldCreateBrlMoneyUsingConvenienceMethod() {
            // When
            Money money = Money.brl("99.99");
            
            // Then
            assertThat(money).isNotNull();
            assertThat(money.getAmount()).isEqualTo(new BigDecimal("99.99"));
            assertThat(money.getCurrency()).isEqualTo(BRL);
        }
        
        @Test
        @DisplayName("Should create USD Money using convenience method")
        void shouldCreateUsdMoneyUsingConvenienceMethod() {
            // When
            Money money = Money.usd("150.25");
            
            // Then
            assertThat(money).isNotNull();
            assertThat(money.getAmount()).isEqualTo(new BigDecimal("150.25"));
            assertThat(money.getCurrency()).isEqualTo(USD);
        }
        
        @Test
        @DisplayName("Should create zero Money")
        void shouldCreateZeroMoney() {
            // When
            Money money = Money.zero(BRL);
            
            // Then
            assertThat(money).isNotNull();
            assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(money.getCurrency()).isEqualTo(BRL);
            assertThat(money.isZero()).isTrue();
        }
        
        @Test
        @DisplayName("Should reject null amount")
        void shouldRejectNullAmount() {
            // When & Then
            assertThatThrownBy(() -> Money.of(null, BRL))
                .isInstanceOf(NullPointerException.class);
        }
        
        @Test
        @DisplayName("Should reject null currency")
        void shouldRejectNullCurrency() {
            // When & Then
            assertThatThrownBy(() -> Money.of(new BigDecimal("100.00"), null))
                .isInstanceOf(NullPointerException.class);
        }
    }
    
    @Nested
    @DisplayName("Arithmetic Operations")
    @Order(2)
    class ArithmeticOperationsTests {
        
        @Test
        @DisplayName("Should add Money with same currency")
        void shouldAddMoneyWithSameCurrency() {
            // Given
            Money money1 = Money.brl("100.50");
            Money money2 = Money.brl("50.25");
            
            // When
            Money result = money1.add(money2);
            
            // Then
            assertThat(result.getAmount()).isEqualTo(new BigDecimal("150.75"));
            assertThat(result.getCurrency()).isEqualTo(BRL);
        }
        
        @Test
        @DisplayName("Should subtract Money with same currency")
        void shouldSubtractMoneyWithSameCurrency() {
            // Given
            Money money1 = Money.brl("100.50");
            Money money2 = Money.brl("30.25");
            
            // When
            Money result = money1.subtract(money2);
            
            // Then
            assertThat(result.getAmount()).isEqualTo(new BigDecimal("70.25"));
            assertThat(result.getCurrency()).isEqualTo(BRL);
        }
        
        @Test
        @DisplayName("Should multiply Money by BigDecimal")
        void shouldMultiplyMoneyByBigDecimal() {
            // Given
            Money money = Money.brl("50.00");
            BigDecimal multiplier = new BigDecimal("2.5");
            
            // When
            Money result = money.multiply(multiplier);
            
            // Then
            assertThat(result.getAmount()).isEqualTo(new BigDecimal("125.00"));
            assertThat(result.getCurrency()).isEqualTo(BRL);
        }
        
        @Test
        @DisplayName("Should divide Money by BigDecimal")
        void shouldDivideMoneyByBigDecimal() {
            // Given
            Money money = Money.brl("100.00");
            BigDecimal divisor = new BigDecimal("4");
            
            // When
            Money result = money.divide(divisor);
            
            // Then
            assertThat(result.getAmount()).isEqualTo(new BigDecimal("25.00"));
            assertThat(result.getCurrency()).isEqualTo(BRL);
        }
        
        @Test
        @DisplayName("Should reject addition with different currencies")
        void shouldRejectAdditionWithDifferentCurrencies() {
            // Given
            Money brlMoney = Money.brl("100.00");
            Money usdMoney = Money.usd("100.00");
            
            // When & Then
            assertThatThrownBy(() -> brlMoney.add(usdMoney))
                .isInstanceOf(IllegalArgumentException.class);
        }
        
        @Test
        @DisplayName("Should reject multiplication by null")
        void shouldRejectMultiplicationByNull() {
            // Given
            Money money = Money.brl("100.00");
            
            // When & Then
            assertThatThrownBy(() -> money.multiply(null))
                .isInstanceOf(NullPointerException.class);
        }
        
        @Test
        @DisplayName("Should reject division by zero")
        void shouldRejectDivisionByZero() {
            // Given
            Money money = Money.brl("100.00");
            
            // When & Then
            assertThatThrownBy(() -> money.divide(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
    
    @Nested
    @DisplayName("Comparison and Validation Operations")
    @Order(3)
    class ComparisonOperationsTests {
        
        @Test
        @DisplayName("Should identify positive Money")
        void shouldIdentifyPositiveMoney() {
            // Given
            Money money = Money.brl("100.00");
            
            // When & Then
            assertThat(money.isPositive()).isTrue();
            assertThat(money.isNegative()).isFalse();
            assertThat(money.isZero()).isFalse();
        }
        
        @Test
        @DisplayName("Should identify negative Money")
        void shouldIdentifyNegativeMoney() {
            // Given
            Money money = Money.brl("-50.00");
            
            // When & Then
            assertThat(money.isNegative()).isTrue();
            assertThat(money.isPositive()).isFalse();
            assertThat(money.isZero()).isFalse();
        }
        
        @Test
        @DisplayName("Should identify zero Money")
        void shouldIdentifyZeroMoney() {
            // Given
            Money money = Money.zero(BRL);
            
            // When & Then
            assertThat(money.isZero()).isTrue();
            assertThat(money.isPositive()).isFalse();
            assertThat(money.isNegative()).isFalse();
        }
        
        @Test
        @DisplayName("Should compare Money amounts correctly")
        void shouldCompareMoneyAmountsCorrectly() {
            // Given
            Money smaller = Money.brl("50.00");
            Money larger = Money.brl("100.00");
            Money equal = Money.brl("50.00");
            
            // When & Then
            assertThat(smaller.isGreaterThan(larger)).isFalse();
            assertThat(larger.isGreaterThan(smaller)).isTrue();
            assertThat(smaller.isGreaterThan(equal)).isFalse();
            
            assertThat(smaller.isGreaterThanOrEqual(larger)).isFalse();
            assertThat(larger.isGreaterThanOrEqual(smaller)).isTrue();
            assertThat(smaller.isGreaterThanOrEqual(equal)).isTrue();
        }
        
        @Test
        @DisplayName("Should reject comparison with different currencies")
        void shouldRejectComparisonWithDifferentCurrencies() {
            // Given
            Money brlMoney = Money.brl("100.00");
            Money usdMoney = Money.usd("100.00");
            
            // When & Then
            assertThatThrownBy(() -> brlMoney.isGreaterThan(usdMoney))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
    
    @Nested
    @DisplayName("Equality and Hash Code Contract")
    @Order(4)
    class EqualityAndHashCodeTests {
        
        @Test
        @DisplayName("Should implement reflexive equality")
        void shouldImplementReflexiveEquality() {
            // Given
            Money money = Money.brl("100.00");
            
            // When & Then
            assertThat(money).isEqualTo(money);
        }
        
        @Test
        @DisplayName("Should implement symmetric equality")
        void shouldImplementSymmetricEquality() {
            // Given
            Money money1 = Money.brl("100.00");
            Money money2 = Money.brl("100.00");
            
            // When & Then
            assertThat(money1).isEqualTo(money2);
            assertThat(money2).isEqualTo(money1);
        }
        
        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            Money money = Money.brl("100.00");
            
            // When & Then
            assertThat(money).isNotEqualTo(null);
        }
        
        @Test
        @DisplayName("Should not be equal with different amounts")
        void shouldNotBeEqualWithDifferentAmounts() {
            // Given
            Money money1 = Money.brl("100.00");
            Money money2 = Money.brl("200.00");
            
            // When & Then
            assertThat(money1).isNotEqualTo(money2);
        }
        
        @Test
        @DisplayName("Should not be equal with different currencies")
        void shouldNotBeEqualWithDifferentCurrencies() {
            // Given
            Money brlMoney = Money.brl("100.00");
            Money usdMoney = Money.usd("100.00");
            
            // When & Then
            assertThat(brlMoney).isNotEqualTo(usdMoney);
        }
        
        @Test
        @DisplayName("Should have consistent hash code")
        void shouldHaveConsistentHashCode() {
            // Given
            Money money = Money.brl("100.00");
            
            // When
            int hashCode1 = money.hashCode();
            int hashCode2 = money.hashCode();
            
            // Then
            assertThat(hashCode1).isEqualTo(hashCode2);
        }
        
        @Test
        @DisplayName("Should have equal hash codes for equal objects")
        void shouldHaveEqualHashCodesForEqualObjects() {
            // Given
            Money money1 = Money.brl("100.00");
            Money money2 = Money.brl("100.00");
            
            // When & Then
            assertThat(money1.hashCode()).isEqualTo(money2.hashCode());
        }
    }
    
    @Nested
    @DisplayName("String Representation")
    @Order(5)
    class StringRepresentationTests {
        
        @Test
        @DisplayName("Should format toString correctly")
        void shouldFormatToStringCorrectly() {
            // Given
            Money money = Money.brl("123.45");
            
            // When
            String result = money.toString();
            
            // Then
            assertThat(result).isEqualTo("BRL 123.45");
        }
        
        @Test
        @DisplayName("Should format zero amount correctly")
        void shouldFormatZeroAmountCorrectly() {
            // Given
            Money money = Money.zero(BRL);
            
            // When
            String result = money.toString();
            
            // Then
            assertThat(result).isEqualTo("BRL 0.00");
        }
        
        @Test
        @DisplayName("Should format negative amount correctly")
        void shouldFormatNegativeAmountCorrectly() {
            // Given
            Money money = Money.brl("-50.75");
            
            // When
            String result = money.toString();
            
            // Then
            assertThat(result).isEqualTo("BRL -50.75");
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Boundary Conditions")
    @Order(6)
    class EdgeCasesTests {
        
        @Test
        @DisplayName("Should handle very large amounts")
        void shouldHandleVeryLargeAmounts() {
            // Given
            BigDecimal largeAmount = new BigDecimal("999999999999.99");
            
            // When
            Money money = Money.of(largeAmount, BRL);
            
            // Then
            assertThat(money.getAmount()).isEqualTo(largeAmount);
            assertThat(money.getCurrency()).isEqualTo(BRL);
        }
        
        @Test
        @DisplayName("Should handle very small amounts")
        void shouldHandleVerySmallAmounts() {
            // Given
            BigDecimal smallAmount = new BigDecimal("0.01");
            
            // When
            Money money = Money.of(smallAmount, BRL);
            
            // Then
            assertThat(money.getAmount()).isEqualTo(smallAmount);
            assertThat(money.getCurrency()).isEqualTo(BRL);
        }
        
        @ParameterizedTest
        @ValueSource(strings = {"0.00", "1.00", "10.00", "100.00", "1000.00", "9999.99"})
        @DisplayName("Should handle various amount formats")
        void shouldHandleVariousAmountFormats(String amountStr) {
            // When
            Money money = Money.brl(amountStr);
            
            // Then
            assertThat(money.getAmount()).isEqualTo(new BigDecimal(amountStr));
            assertThat(money.getCurrency()).isEqualTo(BRL);
        }
        
        @Test
        @DisplayName("Should handle arithmetic with precision rounding")
        void shouldHandleArithmeticWithPrecisionRounding() {
            // Given
            Money money1 = Money.brl("10.00");
            Money money2 = Money.brl("3.00");
            
            // When - Division that requires rounding
            Money result = money1.divide(money2.getAmount());
            
            // Then - Should round to 2 decimal places
            assertThat(result.getAmount()).isEqualTo(new BigDecimal("3.33"));
        }
        
        @Test
        @DisplayName("Should handle multiple currency instances")
        void shouldHandleMultipleCurrencyInstances() {
            // Given
            Currency brl1 = Currency.getInstance("BRL");
            Currency brl2 = Currency.getInstance("BRL");
            
            // When
            Money money1 = Money.of(new BigDecimal("100.00"), brl1);
            Money money2 = Money.of(new BigDecimal("50.00"), brl2);
            
            // Then - Should work with same currency from different instances
            assertThatNoException().isThrownBy(() -> money1.add(money2));
        }
    }
}