package com.aurora.ledger.application.transaction.service;

import com.aurora.ledger.application.transaction.command.DepositCommand;
import com.aurora.ledger.application.transaction.command.PayBillCommand;
import com.aurora.ledger.application.transaction.query.BalanceQuery;
import com.aurora.ledger.application.transaction.query.BalanceQueryResult;
import com.aurora.ledger.infrastructure.bus.CommandBus;
import com.aurora.ledger.infrastructure.bus.QueryBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Transaction Service  CQRS Orchestrator
 * Coordinates banking operations using Command and Query buses
 * Following Clean Architecture principles
 */
@Service
public class TransactionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    
    private final CommandBus commandBus;
    private final QueryBus queryBus;
    
    public TransactionService(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    /**
     * Deposit money into user account
     * Command side operation  modifies state
     */
    public void depositMoney(String userLogin, BigDecimal amount, String description) {
        logger.info("Processing deposit request: user={}, amount={}", userLogin, amount);
        
        DepositCommand command = new DepositCommand(userLogin, amount, description);
        commandBus.dispatch(command);
        
        logger.info("Deposit command dispatched: {}", command.getCommandId());
    }
    
    /**
     * Pay bill from user account
     * Command side operation  modifies state, allows negativation
     */
    public void payBill(String userLogin, BigDecimal amount, String billDescription) {
        logger.info("Processing bill payment request: user={}, amount={}, bill={}", 
                   userLogin, amount, billDescription);
        
        PayBillCommand command = new PayBillCommand(userLogin, amount, billDescription);
        commandBus.dispatch(command);
        
        logger.info("Bill payment command dispatched: {}", command.getCommandId());
    }
    
    /**
     * Get user balance with transaction history
     * Query side operation  returns data, never modifies state
     */
    public BalanceQueryResult getBalanceWithHistory(String userLogin) {
        logger.debug("Processing balance query with history: user={}", userLogin);
        
        BalanceQuery query = new BalanceQuery(userLogin, true);
        BalanceQueryResult result = queryBus.dispatch(query);
        
        logger.debug("Balance query completed: user={}, balance={}", 
                    userLogin, result.getTotalBalance());
        
        return result;
    }
    
    /**
     * Get user balance only (no history)
     * Query side operation  optimized for quick balance checks
     */
    public BalanceQueryResult getBalance(String userLogin) {
        logger.debug("Processing balance query: user={}", userLogin);
        
        BalanceQuery query = new BalanceQuery(userLogin, false);
        BalanceQueryResult result = queryBus.dispatch(query);
        
        logger.debug("Balance query completed: user={}, balance={}", 
                    userLogin, result.getTotalBalance());
        
        return result;
    }
    
    /**
     * Get user balance summary (simplified version)
     * Query side operation  returns balance summary for dashboard
     */
    public BalanceQueryResult getBalanceSummary(String userLogin) {
        logger.debug("Processing balance summary query: user={}", userLogin);
        
        BalanceQuery query = new BalanceQuery(userLogin, false);
        BalanceQueryResult result = queryBus.dispatch(query);
        
        logger.debug("Balance summary query completed: user={}, balance={}", 
                    userLogin, result.getTotalBalance());
        
        return result;
    }
}










