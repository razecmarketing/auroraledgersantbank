package com.aurora.ledger.infrastructure.repository;

import com.aurora.ledger.domain.account.Account;
import com.aurora.ledger.domain.account.AccountId;
import com.aurora.ledger.domain.account.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InMemoryAccountRepository
 * Simple threadsafe inmemory implementation for AccountRepository.
 *
 * This adapter allows the application to boot and exercise domain logic
 * without a persistent storage for accounts. Suitable for dev/demo/tests.
 */
@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<AccountId, Account> byId = new ConcurrentHashMap<>();
    private final Map<String, AccountId> byNumber = new ConcurrentHashMap<>();
    private final Map<String, Set<AccountId>> byCpf = new ConcurrentHashMap<>();

    @Override
    public Account save(Account account) {
        Objects.requireNonNull(account, "account");
        // Ensure account number index
        AccountId existingId = byNumber.get(account.getAccountNumber());
        if (existingId != null && !existingId.equals(account.getAccountId())) {
            throw new IllegalStateException("Account number already in use: " + account.getAccountNumber());
        }
        byId.put(account.getAccountId(), account);
        byNumber.put(account.getAccountNumber(), account.getAccountId());
        byCpf.computeIfAbsent(account.getCustomerCpf(), k -> ConcurrentHashMap.newKeySet())
             .add(account.getAccountId());
        return account;
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
        if (accountId == null) return Optional.empty();
        return Optional.ofNullable(byId.get(accountId));
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        if (accountNumber == null) return Optional.empty();
        AccountId id = byNumber.get(accountNumber);
        return id == null ? Optional.empty() : Optional.ofNullable(byId.get(id));
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        if (accountNumber == null) return false;
        return byNumber.containsKey(accountNumber);
    }

    @Override
    public List<Account> findByCustomerCpf(String customerCpf) {
        if (customerCpf == null) return List.of();
        Set<AccountId> ids = byCpf.getOrDefault(customerCpf, Set.of());
        if (ids.isEmpty()) return List.of();
    return ids.stream()
        .map(byId::get)
        .filter(Objects::nonNull)
        .toList();
    }
}











