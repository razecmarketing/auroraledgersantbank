package com.aurora.ledger.application.transaction.query;

import com.aurora.ledger.application.query.Query;
import com.aurora.ledger.application.query.QueryParameters;

/**
 * Balance Query  CQRS Read Side
 * Query to retrieve user balance information
 * Following Bertrand Meyer principle: Queries return data, never modify state
 */
public class BalanceQuery implements Query<BalanceQueryResult> {
    
    private final String queryId;
    private final String userLogin;
    private final boolean includeHistory;
    private final QueryParameters parameters;
    
    public BalanceQuery(String userLogin, boolean includeHistory) {
        this.queryId = java.util.UUID.randomUUID().toString();
        this.userLogin = userLogin;
        this.includeHistory = includeHistory;
        this.parameters = new QueryParameters();
    }
    
    public BalanceQuery(String userLogin) {
        this(userLogin, false);
    }
    
    @Override
    public String getQueryId() {
        return queryId;
    }
    
    @Override
    public QueryParameters getParameters() {
        return parameters;
    }
    
    @Override
    public Class<BalanceQueryResult> getResultType() {
        return BalanceQueryResult.class;
    }
    
    public String getUserLogin() {
        return userLogin;
    }
    
    public boolean isIncludeHistory() {
        return includeHistory;
    }
    
    @Override
    public String toString() {
        return String.format("BalanceQuery{queryId='%s', user='%s', includeHistory=%b}", 
                           queryId, userLogin, includeHistory);
    }
}










