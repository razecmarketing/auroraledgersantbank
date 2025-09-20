package com.aurora.ledger.domain.transaction.events;

import com.aurora.ledger.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Money Deposited Event
 * Domain Event emitted when user deposits money
 * Following Greg Young's Event Sourcing principles
 */
public class MoneyDepositedEvent extends DomainEvent {

    private final Details details;

    public MoneyDepositedEvent(String userLogin, Details details, Long aggregateVersion) {
        super(userLogin, Objects.requireNonNullElse(aggregateVersion, 0L));
        this.details = Objects.requireNonNull(details, "details");
    }

    @Override
    public String getEventType() {
        return "MoneyDeposited";
    }

    @Override
    public Object getEventPayload() {
        return getDetails();
    }

    public Details getDetails() {
        return getDetails();
    }

    public String getUserLogin() {
        return getAggregateId();
    }

    public BigDecimal getAmount() {
        return details.amount();
    }

    public String getDescription() {
        return details.description();
    }

    public BigDecimal getBalanceAfter() {
        return details.balanceAfter();
    }

    public String getCorrelationId() {
        return details.correlationId();
    }

    public boolean hadNegativeBalance() {
        return details.hadNegativeBalance();
    }

    public BigDecimal getInterestPaid() {
        return details.interestPaid();
    }

    /**
     * Immutable payload describing the deposit event.
     */
    public static final class Details {
        private final BigDecimal amount;
        private final String description;
        private final BigDecimal balanceAfter;
        private final String correlationId;
        private final boolean hadNegativeBalance;
        private final BigDecimal interestPaid;

        private Details(Builder builder) {
            this.amount = Objects.requireNonNull(builder.amount, "amount");
            this.description = builder.description == null ? "" : builder.description;
            this.balanceAfter = Objects.requireNonNull(builder.balanceAfter, "balanceAfter");
            this.correlationId = builder.correlationId;
            this.hadNegativeBalance = builder.hadNegativeBalance;
            this.interestPaid = builder.interestPaid == null ? BigDecimal.ZERO : builder.interestPaid;
        }

        public static Builder builder(BigDecimal amount, BigDecimal balanceAfter) {
            return new Builder(amount, balanceAfter);
        }

        public BigDecimal amount() {
            return amount;
        }

        public String description() {
            return description;
        }

        public BigDecimal balanceAfter() {
            return balanceAfter;
        }

        public String correlationId() {
            return correlationId;
        }

        public boolean hadNegativeBalance() {
            return hadNegativeBalance;
        }

        public BigDecimal interestPaid() {
            return interestPaid;
        }

        public static final class Builder {
            private final BigDecimal amount;
            private final BigDecimal balanceAfter;
            private String description;
            private String correlationId;
            private boolean hadNegativeBalance;
            private BigDecimal interestPaid;

            private Builder(BigDecimal amount, BigDecimal balanceAfter) {
                this.amount = Objects.requireNonNull(amount, "amount");
                this.balanceAfter = Objects.requireNonNull(balanceAfter, "balanceAfter");
            }

            public Builder description(String description) {
                this.description = description;
                return this;
            }

            public Builder correlationId(String correlationId) {
                this.correlationId = correlationId;
                return this;
            }

            public Builder hadNegativeBalance(boolean hadNegativeBalance) {
                this.hadNegativeBalance = hadNegativeBalance;
                return this;
            }

            public Builder interestPaid(BigDecimal interestPaid) {
                this.interestPaid = interestPaid;
                return this;
            }

            public Details build() {
                return new Details(this);
            }
        }
    }
}
