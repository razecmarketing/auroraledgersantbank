package com.aurora.ledger.application.transaction.query;

import com.aurora.ledger.application.query.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Balance Query Handler  CQRS Read Side
 * Leverages projection cache for fast queries.
 */
@Component
public class BalanceQueryHandler implements QueryHandler<BalanceQuery, BalanceQueryResult> {

    private static final Logger logger = LoggerFactory.getLogger(BalanceQueryHandler.class);

    private final BalanceSnapshotBuilder snapshotBuilder;
    private final BalanceProjectionCache projectionCache;

    public BalanceQueryHandler(BalanceSnapshotBuilder snapshotBuilder,
                               BalanceProjectionCache projectionCache) {
        this.snapshotBuilder = snapshotBuilder;
        this.projectionCache = projectionCache;
    }

    @Override
    public BalanceQueryResult handle(BalanceQuery query) {
        logger.debug("Processing balance query: {}", query);
        Supplier<BalanceQueryResult> loader = () -> snapshotBuilder.buildFullSnapshot(query.getUserLogin());
        BalanceQueryResult result = projectionCache.getSnapshot(query.getUserLogin(), query.isIncludeHistory(), loader);
        logger.debug("Balance query processed: user={}, balance={}", query.getUserLogin(), result.getTotalBalance());
        return result;
    }

    @Override
    public Class<BalanceQuery> getQueryType() {
        return BalanceQuery.class;
    }
}











