package com.aurora.ledger.infrastructure.projection.cache;

import com.aurora.ledger.application.transaction.query.BalanceProjectionCache;
import com.aurora.ledger.application.transaction.query.BalanceQueryResult;
import com.aurora.ledger.infrastructure.projection.document.AccountBalanceProjectionDocument;
import com.aurora.ledger.infrastructure.projection.repository.AccountBalanceProjectionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class MongoRedisBalanceProjectionCache implements BalanceProjectionCache {

    private static final Logger logger = LoggerFactory.getLogger(MongoRedisBalanceProjectionCache.class);
    private static final String KEY_TEMPLATE = "aurora:balance:%s";
    private static final DateTimeFormatter HISTORY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final AccountBalanceProjectionRepository projectionRepository;

    public MongoRedisBalanceProjectionCache(StringRedisTemplate redisTemplate,
                                            ObjectMapper objectMapper,
                                            AccountBalanceProjectionRepository projectionRepository) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.projectionRepository = projectionRepository;
    }

    @Override
    public BalanceQueryResult getSnapshot(String userLogin, boolean includeHistory, Supplier<BalanceQueryResult> loader) {
        try {
            Optional<BalanceQueryResult> cached = readFromRedis(userLogin, includeHistory);
            if (cached.isPresent()) {
                return cached.get();
            }
        } catch (Exception ex) {
            logger.warn("Failed to read cached snapshot for user={}: {}", userLogin, ex.getMessage());
        }

        BalanceQueryResult snapshot = loader.get();
        storeSnapshot(userLogin, snapshot);
        return includeHistory ? snapshot : toSummary(snapshot);
    }

    @Override
    public void refreshSnapshot(String userLogin, Supplier<BalanceQueryResult> loader) {
        BalanceQueryResult snapshot = loader.get();
        storeSnapshot(userLogin, snapshot);
    }

    private Optional<BalanceQueryResult> readFromRedis(String userLogin, boolean includeHistory) {
        String key = String.format(KEY_TEMPLATE, userLogin);
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }
        try {
            CachedBalanceSnapshot cached = objectMapper.readValue(json, CachedBalanceSnapshot.class);
            return Optional.of(fromCachedSnapshot(cached, includeHistory));
        } catch (Exception e) {
            logger.warn("Unable to deserialize cached snapshot for user={}: {}", userLogin, e.getMessage());
            return Optional.empty();
        }
    }

    private void storeSnapshot(String userLogin, BalanceQueryResult snapshot) {
        try {
            CachedBalanceSnapshot cached = CachedBalanceSnapshot.from(userLogin, snapshot);
            String json = objectMapper.writeValueAsString(cached);
            redisTemplate.opsForValue().set(String.format(KEY_TEMPLATE, userLogin), json);
            projectionRepository.save(cached.toDocument());
        } catch (Exception e) {
            logger.warn("Failed to store snapshot for user={}: {}", userLogin, e.getMessage());
        }
    }

    private BalanceQueryResult fromCachedSnapshot(CachedBalanceSnapshot cached, boolean includeHistory) {
        BigDecimal balance = new BigDecimal(cached.getTotalBalance());
        if (!includeHistory) {
            return BalanceQueryResult.withBalanceOnly(balance, cached.isNegative(), cached.getLastUpdated());
        }
        List<BalanceQueryResult.TransactionHistoryItem> historyItems = cached.getHistory().stream()
            .map(item -> new BalanceQueryResult.TransactionHistoryItem(
                item.getType(),
                new BigDecimal(item.getAmount()),
                LocalDateTime.parse(item.getDate(), HISTORY_DATE_FORMAT)
            ))
            .toList();
        return new BalanceQueryResult(balance, historyItems, cached.isNegative(), cached.getLastUpdated());
    }

    private BalanceQueryResult toSummary(BalanceQueryResult fullSnapshot) {
        return BalanceQueryResult.withBalanceOnly(
            new BigDecimal(fullSnapshot.getTotalBalance()),
            fullSnapshot.isNegative(),
            fullSnapshot.getLastUpdated()
        );
    }

    private static class CachedBalanceSnapshot {
        private String userLogin;
        private String totalBalance;
        private boolean negative;
        private LocalDateTime lastUpdated;
        private List<CachedHistoryItem> history;

        @SuppressWarnings("unused")
    public CachedBalanceSnapshot() {
        }

        public static CachedBalanceSnapshot from(String userLogin, BalanceQueryResult snapshot) {
            CachedBalanceSnapshot cached = new CachedBalanceSnapshot();
            cached.userLogin = userLogin;
            cached.totalBalance = snapshot.getTotalBalance();
            cached.negative = snapshot.isNegative();
            cached.lastUpdated = snapshot.getLastUpdated();
            cached.history = snapshot.getTransactionHistory().stream()
                .map(item -> new CachedHistoryItem(item.getType(), item.getAmount(), item.getDate()))
                .toList();
            return cached;
        }

        public AccountBalanceProjectionDocument toDocument() {
            List<AccountBalanceProjectionDocument.HistoryItemDocument> historyDocs = history.stream()
                .map(item -> new AccountBalanceProjectionDocument.HistoryItemDocument(item.type, item.amount, item.date))
                .toList();
            return new AccountBalanceProjectionDocument(userLogin, totalBalance, negative, lastUpdated, historyDocs);
        }

        @SuppressWarnings("unused")
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

        public List<CachedHistoryItem> getHistory() {
            return history;
        }
    }

    private static class CachedHistoryItem {
        private String type;
        private String amount;
        private String date;

        @SuppressWarnings("unused")
        public CachedHistoryItem() {
        }

        public CachedHistoryItem(String type, String amount, String date) {
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

