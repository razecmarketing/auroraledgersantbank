package com.aurora.ledger.infrastructure.event.handler;

import com.aurora.ledger.application.transaction.query.BalanceProjectionCache;
import com.aurora.ledger.application.transaction.query.BalanceSnapshotBuilder;
import com.aurora.ledger.domain.transaction.events.MoneyDepositedEvent;
import com.aurora.ledger.infrastructure.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Money Deposited Event Handler
 * Handles side effects and read model updates when money is deposited
 */
@Component
public class MoneyDepositedEventHandler implements EventHandler<MoneyDepositedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(MoneyDepositedEventHandler.class);

    private final BalanceProjectionCache projectionCache;
    private final BalanceSnapshotBuilder snapshotBuilder;

    public MoneyDepositedEventHandler(BalanceProjectionCache projectionCache,
                                      BalanceSnapshotBuilder snapshotBuilder) {
        this.projectionCache = projectionCache;
        this.snapshotBuilder = snapshotBuilder;
    }

    @Override
    public void handle(MoneyDepositedEvent event) {
        logger.info("Processing MoneyDepositedEvent: user={}, amount={}, balance={}",
                   event.getUserLogin(), event.getAmount(), event.getBalanceAfter());

        if (event.hadNegativeBalance() && event.getInterestPaid().compareTo(java.math.BigDecimal.ZERO) > 0) {
            logger.info("Interest applied during deposit: user={}, interest={}",
                       event.getUserLogin(), event.getInterestPaid());
        }

        projectionCache.refreshSnapshot(event.getUserLogin(), () -> snapshotBuilder.buildFullSnapshot(event.getUserLogin()));

        logger.debug("MoneyDepositedEvent processed successfully: correlation={}",
                    event.getCorrelationId());
    }

    @Override
    public Class<MoneyDepositedEvent> getEventType() {
        return MoneyDepositedEvent.class;
    }
}










