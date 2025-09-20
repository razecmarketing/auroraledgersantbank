package com.aurora.ledger.domain;

import com.aurora.ledger.domain.account.Account;
import com.aurora.ledger.domain.account.AccountId;
import com.aurora.ledger.domain.account.AccountType;
import com.aurora.ledger.domain.common.Money;
import com.aurora.ledger.domain.transaction.DoubleEntryTransaction;
import com.aurora.ledger.domain.transaction.TransactionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.assertj.core.api.Assertions.*;

/**
 * Banking Domain Demo Test - Showcases double-entry bookkeeping and domain rules
 * This test demonstrates the core banking functionality that will impress Santander
 * 
 * 
 */
class BankingDomainDemoTest {
    
    private final Currency brl = Currency.getInstance("BRL");
    
    @Test
    @DisplayName("Should create account with initial deposit following banking rules")
    void shouldCreateAccountWithInitialDeposit() {
        // Given
        String customerCpf = "12345678901";
        Money initialDeposit = Money.brl("1000.00");
        
        // When
        Account account = Account.create(customerCpf, AccountType.CHECKING, initialDeposit);
        
        // Then
        assertThat(account.getCustomerCpf()).isEqualTo(customerCpf);
        assertThat(account.getAccountType()).isEqualTo(AccountType.CHECKING);
        assertThat(account.getBalance()).isEqualTo(initialDeposit);
        assertThat(account.getAccountNumber()).isNotNull();
        assertThat(account.getAccountId()).isNotNull();
        assertThat(account.getVersion()).isEqualTo(0L);
    }
    
    @Test
    @DisplayName("Should enforce minimum deposit rules per account type")
    void shouldEnforceMinimumDepositRules() {
        // Given
        String customerCpf = "12345678901";
        Money insufficientAmount = Money.brl("25.00"); // Less than required 50 for checking
        
        // When & Then
        assertThatThrownBy(() -> 
            Account.create(customerCpf, AccountType.CHECKING, insufficientAmount))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Minimum deposit");
    }
    
    @Test
    @DisplayName("Should create double-entry transfer transaction with balanced entries")
    void shouldCreateDoubleEntryTransferTransaction() {
        // Given - Two accounts
        AccountId fromAccount = AccountId.of("account-123");
        AccountId toAccount = AccountId.of("account-456");
        Money transferAmount = Money.brl("500.00");
        String description = "Transfer to friend";
        String correlationId = "corr-123";
        
        // When - Create transfer transaction
        DoubleEntryTransaction transaction = DoubleEntryTransaction.createTransfer(
            fromAccount, toAccount, transferAmount, description, correlationId);
        
        // Then - Verify double-entry rules
        assertThat(transaction.getTransactionId()).isNotNull();
        assertThat(transaction.getDescription()).isEqualTo(description);
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(transaction.getCorrelationId()).isEqualTo(correlationId);
        
        // Verify entries balance (fundamental double-entry rule)
        assertThat(transaction.getEntries()).hasSize(2);
        
        Money totalDebits = transaction.getEntries().stream()
            .filter(entry -> entry.isDebit())
            .map(entry -> entry.getAmount())
            .reduce(Money.zero(brl), Money::add);
            
        Money totalCredits = transaction.getEntries().stream()
            .filter(entry -> entry.isCredit())
            .map(entry -> entry.getAmount())
            .reduce(Money.zero(brl), Money::add);
        
        assertThat(totalDebits).isEqualTo(totalCredits); // CRITICAL: Debits = Credits
        assertThat(transaction.getTotalAmount()).isEqualTo(transferAmount);
    }
    
    @Test
    @DisplayName("Should create deposit transaction with system contra-account")
    void shouldCreateDepositTransaction() {
        // Given
        AccountId customerAccount = AccountId.of("customer-123");
        Money depositAmount = Money.brl("200.00");
        String correlationId = "deposit-corr-456";
        
        // When
        DoubleEntryTransaction transaction = DoubleEntryTransaction.createDeposit(
            customerAccount, depositAmount, "Cash deposit", correlationId);
        
        // Then
        assertThat(transaction.getEntries()).hasSize(2);
        
        // Verify system cash account is debited
        boolean hasSystemCashDebit = transaction.getEntries().stream()
            .anyMatch(entry -> entry.getAccountId().getValue().equals("SYSTEM_CASH") 
                && entry.isDebit());
        assertThat(hasSystemCashDebit).isTrue();
        
        // Verify customer account is credited
        boolean hasCustomerCredit = transaction.getEntries().stream()
            .anyMatch(entry -> entry.getAccountId().equals(customerAccount) 
                && entry.isCredit());
        assertThat(hasCustomerCredit).isTrue();
    }
    
    @Test
    @DisplayName("Should enforce account debit limits and overdraft rules")
    void shouldEnforceDebitLimitsAndOverdraftRules() {
        // Given - Account with balance
        Account account = Account.create("12345678901", AccountType.CHECKING, Money.brl("500.00"));
        Money excessiveAmount = Money.brl("2000.00"); // More than balance + overdraft limit
        
        // When & Then
        assertThat(account.canDebit(excessiveAmount)).isFalse();
        
        assertThatThrownBy(() -> account.debit(excessiveAmount, "Excessive withdrawal"))
            .isInstanceOf(com.aurora.ledger.domain.account.InsufficientFundsException.class)
            .hasMessageContaining("Insufficient funds");
    }
    
    @Test
    @DisplayName("Should allow overdraft within limits for checking accounts")
    void shouldAllowOverdraftWithinLimits() {
        // Given - Checking account with overdraft capability
        Account account = Account.create("12345678901", AccountType.CHECKING, Money.brl("100.00"));
        Money overdraftAmount = Money.brl("800.00"); // Within 1000 overdraft limit
        
        // When
        Account updatedAccount = account.debit(overdraftAmount, "Authorized overdraft");
        
        // Then
        Money expectedBalance = Money.brl("-700.00"); // 100 - 800 = -700
        assertThat(updatedAccount.getBalance()).isEqualTo(expectedBalance);
        assertThat(updatedAccount.getVersion()).isEqualTo(1L); // Version incremented
    }
    
    @Test
    @DisplayName("Should create transaction reversal with swapped entries")
    void shouldCreateTransactionReversal() {
        // Given - Completed transfer transaction
        AccountId fromAccount = AccountId.of("account-123");
        AccountId toAccount = AccountId.of("account-456");
        Money amount = Money.brl("300.00");
        
        DoubleEntryTransaction originalTx = DoubleEntryTransaction.createTransfer(
            fromAccount, toAccount, amount, "Original transfer", "corr-1")
            .withStatus(TransactionStatus.COMPLETED);
        
        // When - Create reversal
        DoubleEntryTransaction reversalTx = originalTx.createReversal(
            "Customer requested reversal", "corr-2");
        
        // Then
        assertThat(reversalTx.getStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(reversalTx.getDescription()).contains("Reversal of");
        assertThat(reversalTx.getDescription()).contains(originalTx.getTransactionId().toString());
        
        // Verify entries are swapped (original debits become credits and vice versa)
        assertThat(reversalTx.getEntries()).hasSize(2);
        
        // Find what was originally debited from 'fromAccount'
        boolean fromAccountNowCredited = reversalTx.getEntries().stream()
            .anyMatch(entry -> entry.getAccountId().equals(fromAccount) && entry.isCredit());
        assertThat(fromAccountNowCredited).isTrue();
        
        // Find what was originally credited to 'toAccount'  
        boolean toAccountNowDebited = reversalTx.getEntries().stream()
            .anyMatch(entry -> entry.getAccountId().equals(toAccount) && entry.isDebit());
        assertThat(toAccountNowDebited).isTrue();
    }
    
    @Test
    @DisplayName("Should enforce double-entry rule validation")
    void shouldEnforceDoubleEntryRuleValidation() {
        // Given - Unbalanced entries (this should fail)
        AccountId account1 = AccountId.of("acc-1");
        AccountId account2 = AccountId.of("acc-2");
        
        // When & Then - Attempt to create unbalanced transaction
        assertThatThrownBy(() -> 
            DoubleEntryTransaction.builder()
                .type(com.aurora.ledger.domain.transaction.TransactionType.TRANSFER)
                .description("Unbalanced transaction")
                .addDebit(account1, Money.brl("100.00"), "Debit")
                .addCredit(account2, Money.brl("150.00"), "Credit") // Different amount!
                .status(TransactionStatus.PENDING)
                .version(0L)
                .build())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Double-entry rule violated");
    }
    
    @Test
    @DisplayName("Should demonstrate Money value object precision and currency safety")
    void shouldDemonstrateMoneyValueObjectPrecisionAndCurrencySafety() {
        // Given
        Money brl100 = Money.brl("100.00");
        Money brl50 = Money.brl("50.50");
        Money usd100 = Money.usd("100.00");
        
        // When & Then - Currency safety
        assertThatThrownBy(() -> brl100.add(usd100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Currency mismatch");
        
        // When & Then - Precision handling
        Money sum = brl100.add(brl50);
        assertThat(sum.toString()).isEqualTo("BRL 150.50");
        
        // When & Then - Immutability
        Money original = Money.brl("100.00");
        Money doubled = original.multiply(java.math.BigDecimal.valueOf(2));
        assertThat(original.getAmount()).isEqualTo(java.math.BigDecimal.valueOf(100.00).setScale(2));
        assertThat(doubled.getAmount()).isEqualTo(java.math.BigDecimal.valueOf(200.00).setScale(2));
    }
}
