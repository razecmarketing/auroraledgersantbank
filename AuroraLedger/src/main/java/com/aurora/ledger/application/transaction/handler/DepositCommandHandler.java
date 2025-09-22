package com.aurora.ledger.application.transaction.handler;

import com.aurora.ledger.application.command.CommandHandler;
import com.aurora.ledger.application.transaction.command.DepositCommand;
import com.aurora.ledger.domain.balance.UserBalance;
import com.aurora.ledger.domain.shared.DomainEvent;
import com.aurora.ledger.domain.transaction.TransactionHistory;
import com.aurora.ledger.domain.transaction.events.MoneyDepositedEvent;
import com.aurora.ledger.infrastructure.repository.TransactionHistoryRepository;
import com.aurora.ledger.infrastructure.repository.UserBalanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Deposit Command Handler  CQRS Write Side
 * Handles money deposit operations with full business logic
 * Implements 1.02% interest calculation for negative balances
 */
@Component
public class DepositCommandHandler implements CommandHandler<DepositCommand> {
    
    private static final Logger logger = LoggerFactory.getLogger(DepositCommandHandler.class);
    
    private final UserBalanceRepository balanceRepository;
    private final TransactionHistoryRepository transactionRepository;
    
    public DepositCommandHandler(UserBalanceRepository balanceRepository,
                               TransactionHistoryRepository transactionRepository) {
        this.balanceRepository = balanceRepository;
        this.transactionRepository = transactionRepository;
    }
    
    @Override
    @Transactional
    public List<DomainEvent> handle(DepositCommand command) {
        logger.info("Processing deposit command: {}", command);
        
        List<DomainEvent> events = new ArrayList<>();
        
        // Get or create user balance with pessimistic lock for concurrency safety
        UserBalance userBalance = balanceRepository.findByUserLoginWithLock(command.getUserLogin())
                .orElseGet(() -> {
                    UserBalance newBalance = UserBalance.createFor(command.getUserLogin());
                    return balanceRepository.save(newBalance);
                });
        
        // Capture state before operation
        boolean hadNegativeBalance = userBalance.isNegative();
        BigDecimal negativeBalanceBeforeDeposit = userBalance.getNegativeBalance();
        
        // Execute business logic: deposit with interest calculation
        userBalance.deposit(command.getAmount());
        
        // Calculate interest paid (if any)
        BigDecimal interestPaid = BigDecimal.ZERO;
        if (hadNegativeBalance && !userBalance.isNegative()) {
            // Interest was applied and negative balance was cleared
            BigDecimal interestRate = new BigDecimal("1.02");
            BigDecimal negativeWithInterest = negativeBalanceBeforeDeposit.multiply(interestRate);
            interestPaid = negativeWithInterest.subtract(negativeBalanceBeforeDeposit);
        }
        
        // Save updated balance
        UserBalance savedBalance = balanceRepository.save(userBalance);
        
        // Create transaction history record
        TransactionHistory transaction = TransactionHistory.deposit(
                command.getUserLogin(),
                command.getAmount(),
                command.getDescription(),
                command.getCorrelationId(),
                savedBalance.getEffectiveBalance()
        );
        transactionRepository.save(transaction);
        
        // Generate domain event
        MoneyDepositedEvent.Details details = MoneyDepositedEvent.Details.builder(
                command.getAmount(),
                savedBalance.getEffectiveBalance())
                .description(command.getDescription())
                .correlationId(command.getCorrelationId())
                .hadNegativeBalance(hadNegativeBalance)
                .interestPaid(interestPaid)
                .build();
        MoneyDepositedEvent event = new MoneyDepositedEvent(
                command.getUserLogin(),
                details,
                savedBalance.getVersion()
        );
        events.add(event);
        
        logger.info("Deposit processed successfully: user={}, amount={}, newBalance={}, interestPaid={}", 
                   command.getUserLogin(), command.getAmount(), 
                   savedBalance.getEffectiveBalance(), interestPaid);
        
        return events;
    }
    
    @Override
    public Class<DepositCommand> getCommandType() {
        return DepositCommand.class;
    }
}











