package com.aurora.ledger.application.transaction.query;

import java.util.function.Supplier;

/**
 * Abstraction for caching and projecting balance snapshots into read models.
 */
public interface BalanceProjectionCache {

    BalanceQueryResult getSnapshot(String userLogin, boolean includeHistory, Supplier<BalanceQueryResult> loader);

    void refreshSnapshot(String userLogin, Supplier<BalanceQueryResult> loader);
}










