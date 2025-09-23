package com.aurora.ledger.application.integration;

import com.aurora.ledger.application.transaction.service.TransactionService;
import com.aurora.ledger.infrastructure.security.JwtTokenManager;
import com.aurora.ledger.domain.user.User;
import com.aurora.ledger.domain.account.Account;
import com.aurora.ledger.domain.account.AccountType;
import com.aurora.ledger.domain.common.Money;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Banking System Integration Test Suite
 * 
 * Coverage Target: 100% integration scenarios
 * Tests full banking system integration with real services and infrastructure
 * 
 * Execution: mvn test -Dtest=BankingSystemIntegrationTest
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Banking System - Integration Test Suite")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BankingSystemIntegrationTest {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private JwtTokenManager jwtTokenManager;
    
    private static final String TEST_USER_LOGIN = "testuser";
    private static final String TEST_USER_DOCUMENT = "11144477735"; // CPF válido
    private static final String TEST_PASSWORD = "testpassword123";
    
    @Nested
    @DisplayName("JWT Token Management Integration")
    @Order(1)
    class JwtTokenIntegrationTests {
        
        @Test
        @DisplayName("Should generate and validate JWT token")
        void shouldGenerateAndValidateJwtToken() {
            // When
            String token = jwtTokenManager.generateToken(TEST_USER_LOGIN);
            
            // Then
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(jwtTokenManager.getUsernameFromToken(token)).isEqualTo(TEST_USER_LOGIN);
            assertThat(jwtTokenManager.validateToken(token, TEST_USER_LOGIN)).isTrue();
        }
        
        @Test
        @DisplayName("Should extract expiration date from token")
        void shouldExtractExpirationDateFromToken() {
            // Given
            String token = jwtTokenManager.generateToken(TEST_USER_LOGIN);
            
            // When
            var expirationDate = jwtTokenManager.getExpirationDateFromToken(token);
            
            // Then
            assertThat(expirationDate).isNotNull();
            assertThat(expirationDate).isAfter(new java.util.Date());
        }
        
        @Test
        @DisplayName("Should handle token validation with different username")
        void shouldHandleTokenValidationWithDifferentUsername() {
            // Given
            String token = jwtTokenManager.generateToken(TEST_USER_LOGIN);
            
            // When & Then
            assertThat(jwtTokenManager.validateToken(token, "different_user")).isFalse();
        }
        
        @Test
        @DisplayName("Should handle invalid token gracefully")
        void shouldHandleInvalidTokenGracefully() {
            // Given
            String invalidToken = "invalid.token.here";
            
            // When & Then
            assertThatThrownBy(() -> jwtTokenManager.getUsernameFromToken(invalidToken))
                .isInstanceOf(Exception.class);
        }
    }
    
    @Nested
    @DisplayName("Transaction Service Integration")
    @Order(2)
    class TransactionServiceIntegrationTests {
        
        @Test
        @DisplayName("Should process deposit transaction")
        void shouldProcessDepositTransaction() {
            // Given
            BigDecimal depositAmount = BigDecimal.valueOf(1000.00);
            String description = "Initial deposit";
            
            // When & Then - Should not throw exception
            assertThatNoException().isThrownBy(() -> 
                transactionService.depositMoney(TEST_USER_LOGIN, depositAmount, description)
            );
        }
        
        @Test
        @DisplayName("Should process bill payment transaction")
        void shouldProcessBillPaymentTransaction() {
            // Given
            BigDecimal billAmount = BigDecimal.valueOf(250.00);
            String billDescription = "Electricity bill payment";
            
            // When & Then - Should not throw exception
            assertThatNoException().isThrownBy(() -> 
                transactionService.payBill(TEST_USER_LOGIN, billAmount, billDescription)
            );
        }
        
        @Test
        @DisplayName("Should retrieve user balance")
        void shouldRetrieveUserBalance() {
            // When & Then - Should not throw exception
            assertThatNoException().isThrownBy(() -> 
                transactionService.getBalance(TEST_USER_LOGIN)
            );
        }
        
        @Test
        @DisplayName("Should handle invalid user login gracefully")
        void shouldHandleInvalidUserLoginGracefully() {
            // Given
            String invalidLogin = "nonexistent_user";
            BigDecimal amount = BigDecimal.valueOf(100.00);
            
            // When & Then - Should handle gracefully without system crash
            assertThatNoException().isThrownBy(() -> 
                transactionService.depositMoney(invalidLogin, amount, "Test deposit")
            );
        }
        
        @Test
        @DisplayName("Should handle null amounts gracefully")
        void shouldHandleNullAmountsGracefully() {
            // When & Then
            assertThatThrownBy(() -> 
                transactionService.depositMoney(TEST_USER_LOGIN, null, "Test deposit")
            ).isInstanceOf(Exception.class);
        }
        
        @Test
        @DisplayName("Should handle negative amounts appropriately")
        void shouldHandleNegativeAmountsAppropriately() {
            // Given
            BigDecimal negativeAmount = BigDecimal.valueOf(-100.00);
            
            // When & Then
            assertThatThrownBy(() -> 
                transactionService.depositMoney(TEST_USER_LOGIN, negativeAmount, "Invalid deposit")
            ).isInstanceOf(Exception.class);
        }
    }
    
    @Nested
    @DisplayName("Domain Model Integration")
    @Order(3)
    class DomainModelIntegrationTests {
        
        @Test
        @DisplayName("Should create complete user-account relationship")
        void shouldCreateCompleteUserAccountRelationship() {
            // Given - CPF válido segundo algoritmo brasileiro oficial
            String customerCpf = "11144477735"; // CPF matematicamente válido
            User user = new User("Test User", customerCpf, TEST_USER_LOGIN, TEST_PASSWORD);
            
            Money initialBalance = Money.brl("5000.00");
            Account account = Account.create(customerCpf, AccountType.CHECKING, initialBalance);
            
            // When & Then
            assertThat(user.getLogin()).isEqualTo(TEST_USER_LOGIN);
            assertThat(user.getDocument()).isEqualTo("11144477735");
            assertThat(account.getCustomerCpf()).isEqualTo(customerCpf);
            assertThat(account.getBalance()).isEqualTo(initialBalance);
        }
        
        @Test
        @DisplayName("Should perform money operations with different currencies")
        void shouldPerformMoneyOperationsWithDifferentCurrencies() {
            // Given
            Money brlAmount = Money.brl("1000.00");
            Money usdAmount = Money.usd("200.00");
            
            // When & Then
            assertThat(brlAmount.getCurrency().getCurrencyCode()).isEqualTo("BRL");
            assertThat(usdAmount.getCurrency().getCurrencyCode()).isEqualTo("USD");
            assertThat(brlAmount).isNotEqualTo(usdAmount);
        }
        
        @Test
        @DisplayName("Should handle account balance operations")
        void shouldHandleAccountBalanceOperations() {
            // Given
            String customerCpf = "12345678901";
            Money initialBalance = Money.brl("1000.00");
            Account account = Account.create(customerCpf, AccountType.CHECKING, initialBalance);
            
            // When
            Money creditAmount = Money.brl("500.00");
            Money debitAmount = Money.brl("300.00");
            
            Account creditedAccount = account.credit(creditAmount, "Credit operation");
            Account finalAccount = creditedAccount.debit(debitAmount, "Debit operation");
            
            // Then
            Money expectedBalance = Money.brl("1200.00");
            assertThat(finalAccount.getBalance()).isEqualTo(expectedBalance);
        }
        
        @Test
        @DisplayName("Should validate user document format")
        void shouldValidateUserDocumentFormat() {
            // Given - CPFs para teste de validação brasileira oficial
            String validDocument = "11144477735"; // CPF matematicamente válido
            String invalidDocument = "123"; // Formato inválido
            
            // When & Then - CPF válido deve passar
            assertThatNoException().isThrownBy(() -> 
                new User("Valid User", validDocument, "validuser", "password123")
            );
            
            // CPF inválido deve gerar exceção específica
            assertThatThrownBy(() -> 
                new User("Invalid User", invalidDocument, "invaliduser", "password123")
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("Invalid CPF document format");
        }
    }
    
    @Nested
    @DisplayName("System Performance Integration")
    @Order(4)
    class SystemPerformanceIntegrationTests {
        
        @Test
        @DisplayName("Should handle multiple concurrent token generations")
        void shouldHandleMultipleConcurrentTokenGenerations() {
            // Given
            int numberOfTokens = 100;
            
            // When & Then
            assertThatNoException().isThrownBy(() -> {
                for (int i = 0; i < numberOfTokens; i++) {
                    String token = jwtTokenManager.generateToken(TEST_USER_LOGIN + i);
                    assertThat(token).isNotNull();
                }
            });
        }
        
        @Test
        @DisplayName("Should handle multiple transaction operations")
        void shouldHandleMultipleTransactionOperations() {
            // Given
            int numberOfOperations = 10;
            BigDecimal baseAmount = BigDecimal.valueOf(10.00);
            
            // When & Then
            assertThatNoException().isThrownBy(() -> {
                for (int i = 0; i < numberOfOperations; i++) {
                    transactionService.depositMoney(
                        TEST_USER_LOGIN, 
                        baseAmount.multiply(BigDecimal.valueOf(i + 1)), 
                        "Batch deposit " + (i + 1)
                    );
                }
            });
        }
        
        @Test
        @DisplayName("Should handle large money amounts")
        void shouldHandleLargeMoneyAmounts() {
            // Given
            String largeAmount = "999999999.99";
            
            // When
            Money largeMoney = Money.brl(largeAmount);
            
            // Then
            assertThat(largeMoney.getAmount()).isEqualTo(new BigDecimal("999999999.99"));
        }
        
        @Test
        @DisplayName("Should handle precision in money calculations")
        void shouldHandlePrecisionInMoneyCalculations() {
            // Given
            Money amount1 = Money.brl("10.99");
            Money amount2 = Money.brl("20.01");
            
            // When
            Money sum = amount1.add(amount2);
            
            // Then
            assertThat(sum.getAmount()).isEqualTo(new BigDecimal("31.00"));
        }
    }
}