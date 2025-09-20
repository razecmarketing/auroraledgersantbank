package com.aurora.ledger.application.transaction.query;

import com.aurora.ledger.domain.balance.UserBalance;
import com.aurora.ledger.domain.transaction.TransactionHistory;
import com.aurora.ledger.domain.transaction.TransactionType;
import com.aurora.ledger.infrastructure.repository.TransactionHistoryRepository;
import com.aurora.ledger.infrastructure.repository.UserBalanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Builds balance snapshots aggregating data from the write model.
 */
@Component
public class BalanceSnapshotBuilder {

    private static final Logger logger = LoggerFactory.getLogger(BalanceSnapshotBuilder.class);

    private final UserBalanceRepository balanceRepository;
    private final TransactionHistoryRepository transactionRepository;

    public BalanceSnapshotBuilder(UserBalanceRepository balanceRepository,
                                  TransactionHistoryRepository transactionRepository) {
        this.balanceRepository = balanceRepository;
        this.transactionRepository = transactionRepository;
    }

    public BalanceQueryResult buildFullSnapshot(String userLogin) {
        logger.debug("Building balance snapshot for user={}", userLogin);

        UserBalance userBalance = balanceRepository.findByUserLogin(userLogin)
            .orElseGet(() -> UserBalance.createFor(userLogin));

        BigDecimal effectiveBalance = userBalance.getEffectiveBalance();
        boolean isNegative = userBalance.isNegative();

        List<TransactionHistory> transactions = transactionRepository
            .findByUserLoginOrderByTransactionDateDesc(userLogin);

        List<BalanceQueryResult.TransactionHistoryItem> historyItems = transactions.stream()
            .map(this::convertToHistoryItem)
            .toList();

        return new BalanceQueryResult(effectiveBalance, historyItems, isNegative, userBalance.getLastUpdated());
    }

    private BalanceQueryResult.TransactionHistoryItem convertToHistoryItem(TransactionHistory transaction) {
        String type = convertTransactionTypeToString(transaction.getTransactionType());
        return new BalanceQueryResult.TransactionHistoryItem(
            type,
            transaction.getAmount(),
            transaction.getTransactionDate()
        );
    }

    private String convertTransactionTypeToString(TransactionType transactionType) {
        return switch (transactionType) {
            case DEPOSIT -> "deposito";
            case PAYMENT, WITHDRAWAL -> "saque";
            default -> transactionType.name().toLowerCase();
        };
    }
}
