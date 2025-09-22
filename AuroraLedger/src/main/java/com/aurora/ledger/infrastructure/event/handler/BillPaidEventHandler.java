package com.aurora.ledger.infrastructure.event.handler;

import com.aurora.ledger.application.transaction.query.BalanceProjectionCache;
import com.aurora.ledger.application.transaction.query.BalanceSnapshotBuilder;
import com.aurora.ledger.domain.transaction.events.BillPaidEvent;
import com.aurora.ledger.infrastructure.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Bill Paid Event Handler
 * Handles side effects and read model updates when bills are paid
 * Captures negativation scenarios for business intelligence
 */
@Component
public class BillPaidEventHandler implements EventHandler<BillPaidEvent> {

    private static final Logger logger = LoggerFactory.getLogger(BillPaidEventHandler.class);

    private final BalanceProjectionCache projectionCache;
    private final BalanceSnapshotBuilder snapshotBuilder;

    public BillPaidEventHandler(BalanceProjectionCache projectionCache,
                                BalanceSnapshotBuilder snapshotBuilder) {
        this.projectionCache = projectionCache;
        this.snapshotBuilder = snapshotBuilder;
    }

    @Override
    public void handle(BillPaidEvent event) {
        logger.info("Processing BillPaidEvent: user={}, amount={}, bill={}, balance={}",
                   event.getUserLogin(), event.getAmount(),
                   event.getBillDescription(), event.getBalanceAfter());

        if (event.isAccountWentNegative()) {
            logger.warn("Account negativation detected: user={}, bill={}, balanceBefore={}, balanceAfter={}",
                       event.getUserLogin(), event.getBillDescription(),
                       event.getBalanceBeforePayment(), event.getBalanceAfter());
        }

        projectionCache.refreshSnapshot(event.getUserLogin(), () -> snapshotBuilder.buildFullSnapshot(event.getUserLogin()));

        logger.debug("BillPaidEvent processed successfully: correlation={}",
                    event.getCorrelationId());
    }

    @Override
    public Class<BillPaidEvent> getEventType() {
        return BillPaidEvent.class;
    }
}










