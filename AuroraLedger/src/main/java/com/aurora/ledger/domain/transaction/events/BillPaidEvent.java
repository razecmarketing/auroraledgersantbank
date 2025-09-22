package com.aurora.ledger.domain.transaction.events;

import com.aurora.ledger.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Bill Paid Event
 * Domain Event emitted when user pays a bill
 * Captures negativation scenarios and business logic
 */
public class BillPaidEvent extends DomainEvent {

    private final Details details;

    public BillPaidEvent(String userLogin, Details details, Long aggregateVersion) {
        super(userLogin, Objects.requireNonNullElse(aggregateVersion, 0L));
        this.details = Objects.requireNonNull(details, "details");
    }

    @Override
    public String getEventType() {
        return "BillPaid";
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

    public String getBillDescription() {
        return details.billDescription();
    }

    public BigDecimal getBalanceAfter() {
        return details.balanceAfter();
    }

    public String getCorrelationId() {
        return details.correlationId();
    }

    public boolean isAccountWentNegative() {
        return details.accountWentNegative();
    }

    public BigDecimal getBalanceBeforePayment() {
        return details.balanceBeforePayment();
    }

    /**
     * Immutable payload describing the bill payment event.
     */
    public static final class Details {
        private final BigDecimal amount;
        private final String billDescription;
        private final BigDecimal balanceAfter;
        private final String correlationId;
        private final boolean accountWentNegative;
        private final BigDecimal balanceBeforePayment;

        private Details(Builder builder) {
            this.amount = Objects.requireNonNull(builder.amount, "amount");
            this.billDescription = builder.billDescription == null ? "" : builder.billDescription;
            this.balanceAfter = Objects.requireNonNull(builder.balanceAfter, "balanceAfter");
            this.correlationId = builder.correlationId;
            this.accountWentNegative = builder.accountWentNegative;
            this.balanceBeforePayment = builder.balanceBeforePayment == null ? BigDecimal.ZERO : builder.balanceBeforePayment;
        }

        public static Builder builder(BigDecimal amount, BigDecimal balanceAfter) {
            return new Builder(amount, balanceAfter);
        }

        public BigDecimal amount() {
            return amount;
        }

        public String billDescription() {
            return billDescription;
        }

        public BigDecimal balanceAfter() {
            return balanceAfter;
        }

        public String correlationId() {
            return correlationId;
        }

        public boolean accountWentNegative() {
            return accountWentNegative;
        }

        public BigDecimal balanceBeforePayment() {
            return balanceBeforePayment;
        }

        public static final class Builder {
            private final BigDecimal amount;
            private final BigDecimal balanceAfter;
            private String billDescription;
            private String correlationId;
            private boolean accountWentNegative;
            private BigDecimal balanceBeforePayment;

            private Builder(BigDecimal amount, BigDecimal balanceAfter) {
                this.amount = Objects.requireNonNull(amount, "amount");
                this.balanceAfter = Objects.requireNonNull(balanceAfter, "balanceAfter");
            }

            public Builder billDescription(String billDescription) {
                this.billDescription = billDescription;
                return this;
            }

            public Builder correlationId(String correlationId) {
                this.correlationId = correlationId;
                return this;
            }

            public Builder accountWentNegative(boolean accountWentNegative) {
                this.accountWentNegative = accountWentNegative;
                return this;
            }

            public Builder balanceBeforePayment(BigDecimal balanceBeforePayment) {
                this.balanceBeforePayment = balanceBeforePayment;
                return this;
            }

            public Details build() {
                return new Details(this);
            }
        }
    }
}










