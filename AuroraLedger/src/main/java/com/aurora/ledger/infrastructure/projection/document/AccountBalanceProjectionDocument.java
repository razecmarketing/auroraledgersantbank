package com.aurora.ledger.infrastructure.projection.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "account_balance_projection")
public class AccountBalanceProjectionDocument {

    @Id
    private String userLogin;
    private String totalBalance;
    private boolean negative;
    private LocalDateTime lastUpdated;
    private List<HistoryItemDocument> history;

    public AccountBalanceProjectionDocument() {
    }

    public AccountBalanceProjectionDocument(String userLogin, String totalBalance, boolean negative,
                                             LocalDateTime lastUpdated, List<HistoryItemDocument> history) {
        this.userLogin = userLogin;
        this.totalBalance = totalBalance;
        this.negative = negative;
        this.lastUpdated = lastUpdated;
        this.history = history;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getTotalBalance() {
        return totalBalance;
    }

    public boolean isNegative() {
        return negative;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public List<HistoryItemDocument> getHistory() {
        return history;
    }

    public static class HistoryItemDocument {
        private String type;
        private String amount;
        private String date;

        public HistoryItemDocument() {
        }

        public HistoryItemDocument(String type, String amount, String date) {
            this.type = type;
            this.amount = amount;
            this.date = date;
        }

        public String getType() {
            return type;
        }

        public String getAmount() {
            return amount;
        }

        public String getDate() {
            return date;
        }
    }
}










