package com.aurora.ledger.application.account;

import com.aurora.ledger.domain.account.Account;
import com.aurora.ledger.domain.account.AccountRepository;
import com.aurora.ledger.domain.common.Money;
import com.aurora.ledger.domain.events.AccountCreatedEvent;
import com.aurora.ledger.domain.events.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.Optional;

/**
 * AccountApplicationService  Orchestrates business use cases
 * Implements Clean Architecture Application Layer principles
 * 
 * This is where the MAGIC happens:
 * 1. Domain logic execution
 * 2. Event publishing for audit
 * 3. Transaction management
 * 4. Crosscutting concerns (logging, security)
 * 
 * 
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountApplicationService {
    
    private final AccountRepository accountRepository;
    private final DomainEventPublisher eventPublisher;
    
    /**
     * Creates a new banking account with full audit trail
     * This single method demonstrates:
     *  Domaindriven design
     *  Event sourcing
     *  Transaction safety
     *  Regulatory compliance
     */
    @Transactional
    public Account createAccount(CreateAccountCommand command) {
        log.info("Creating account for customer CPF: {}, type: {}, correlation: {}", 
                maskCpf(command.getCustomerCpf()), 
                command.getAccountType(),
                command.getCorrelationId());
        
        // 1. Create Money value object with banking precision
        Money initialDeposit = Money.of(
            command.getInitialDepositAmount(), 
            Currency.getInstance(command.getCurrencyCode())
        );
        
        // 2. Execute domain business rules
        Account newAccount = Account.create(
            command.getCustomerCpf(),
            command.getAccountType(),
            initialDeposit
        );
        
        // 3. Persist the aggregate
        Account savedAccount = accountRepository.save(newAccount);
        
        // 4. Publish domain event for Event Sourcing
        AccountCreatedEvent event = AccountCreatedEvent.from(
            savedAccount,
            command.getCorrelationId(),
            command.getRequestedByUserId()
        );
        
        eventPublisher.publish(event);
        
        log.info("Account created successfully: {} for customer: {}", 
                savedAccount.getAccountNumber(),
                maskCpf(savedAccount.getCustomerCpf()));
        
        return savedAccount;
    }
    
    /**
     * Retrieves an account by number with masking and input validation.
     */
    @Transactional(readOnly = true)
    public Optional<Account> findByAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number is required");
        }

        return accountRepository.findByAccountNumber(accountNumber.trim());
    }
    /**
     * GDPR/LGPD compliance  never log full CPF
     */
    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }
        return "***.***.***" + cpf.substring(cpf.length() - 2);
    }
}













