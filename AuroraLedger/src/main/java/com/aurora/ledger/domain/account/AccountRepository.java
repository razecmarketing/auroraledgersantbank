package com.aurora.ledger.domain.account;

import java.util.List;
import java.util.Optional;



public interface AccountRepository {

    /**
     * Saves an account aggregate.
     *
     * @param account The account to save.
     * @return The saved account.
     */
    Account save(Account account);

    /**
     * Finds an account by its unique identifier.
     *
     * @param accountId The unique identifier of the account.
     * @return an {@link Optional} containing the account if found, otherwise empty.
     */
    Optional<Account> findById(AccountId accountId);

    /**
     * Finds an account by its account number.
     *
     * @param accountNumber The account number.
     * @return an {@link Optional} containing the account if found, otherwise empty.
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Checks if an account exists with the given account number.
     *
     * @param accountNumber The account number to check.
     * @return {@code true} if an account exists, {@code false} otherwise.
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * Finds all accounts for a specific customer by their CPF.
     *
     * @param customerCpf The customer's CPF.
     * @return A {@link List} of accounts for the customer; an empty list if none are found.
     */
    List<Account> findByCustomerCpf(String customerCpf);
}










