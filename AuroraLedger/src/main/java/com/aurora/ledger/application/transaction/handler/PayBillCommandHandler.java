package com.aurora.ledger.application.transaction.handler;

import com.aurora.ledger.application.command.CommandHandler;
import com.aurora.ledger.application.transaction.command.PayBillCommand;
import com.aurora.ledger.domain.balance.UserBalance;
import com.aurora.ledger.domain.shared.DomainEvent;
import com.aurora.ledger.domain.transaction.TransactionHistory;
import com.aurora.ledger.domain.transaction.events.BillPaidEvent;
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
 * Pay Bill Command Handler - CQRS Write Side
 * Handles bill payment operations with negativation logic
 * Allows users to go negative when insufficient balance
 */
@Component
public class PayBillCommandHandler implements CommandHandler<PayBillCommand> {
    
    private static final Logger logger = LoggerFactory.getLogger(PayBillCommandHandler.class);
    
    private final UserBalanceRepository balanceRepository;
    private final TransactionHistoryRepository transactionRepository;
    
    public PayBillCommandHandler(UserBalanceRepository balanceRepository,
                               TransactionHistoryRepository transactionRepository) {
        this.balanceRepository = balanceRepository;
        this.transactionRepository = transactionRepository;
    }
    
    @Override
    @Transactional
    public List<DomainEvent> handle(PayBillCommand command) {
        logger.info("Processing bill payment command: {}", command);
        
        List<DomainEvent> events = new ArrayList<>();
        
        // Get or create user balance with pessimistic lock
        UserBalance userBalance = balanceRepository.findByUserLoginWithLock(command.getUserLogin())
                .orElseGet(() -> {
                    UserBalance newBalance = UserBalance.createFor(command.getUserLogin());
                    return balanceRepository.save(newBalance);
                });
        
        // Capture state before operation
        BigDecimal balanceBeforePayment = userBalance.getEffectiveBalance();
        boolean wasNegative = userBalance.isNegative();
        
        // Execute business logic: pay bill (allows negativation)
        userBalance.payBill(command.getAmount());
        
        // Check if account went negative during this operation
        boolean accountWentNegative = !wasNegative && userBalance.isNegative();
        
        // Save updated balance
        UserBalance savedBalance = balanceRepository.save(userBalance);
        
        // Create transaction history record
        TransactionHistory transaction = TransactionHistory.billPayment(
                command.getUserLogin(),
                command.getAmount(),
                command.getBillDescription(),
                command.getCorrelationId(),
                savedBalance.getEffectiveBalance()
        );
        transactionRepository.save(transaction);
        
        // Generate domain event
        BillPaidEvent.Details details = BillPaidEvent.Details.builder(
                command.getAmount(),
                savedBalance.getEffectiveBalance())
                .billDescription(command.getBillDescription())
                .correlationId(command.getCorrelationId())
                .accountWentNegative(accountWentNegative)
                .balanceBeforePayment(balanceBeforePayment)
                .build();
        BillPaidEvent event = new BillPaidEvent(
                command.getUserLogin(),
                details,
                savedBalance.getVersion()
        );
        events.add(event);
        
        if (accountWentNegative) {
            logger.warn("Account went negative during bill payment: user={}, bill={}, amount={}, newBalance={}", 
                       command.getUserLogin(), command.getBillDescription(), 
                       command.getAmount(), savedBalance.getEffectiveBalance());
        } else {
            logger.info("Bill payment processed successfully: user={}, bill={}, amount={}, newBalance={}", 
                       command.getUserLogin(), command.getBillDescription(), 
                       command.getAmount(), savedBalance.getEffectiveBalance());
        }
        
        return events;
    }
    
    @Override
    public Class<PayBillCommand> getCommandType() {
        return PayBillCommand.class;
    }
}

