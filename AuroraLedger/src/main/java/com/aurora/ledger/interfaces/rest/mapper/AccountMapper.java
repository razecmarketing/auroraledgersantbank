package com.aurora.ledger.interfaces.rest.mapper;

import com.aurora.ledger.domain.account.Account;
import com.aurora.ledger.domain.account.AccountId;
import com.aurora.ledger.domain.common.Money;
import com.aurora.ledger.interfaces.rest.dto.AccountResponse;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account) {
        if (account == null) {
            return null;
        }

        return AccountResponse.builder()
            .accountId(resolveAccountId(account.getAccountId()))
            .accountType(account.getAccountType() != null ? account.getAccountType().name() : null)
            .balance(resolveBalanceAmount(account.getBalance()))
            .currency(resolveCurrency(account.getBalance()))
            .status(account.getStatus() != null ? account.getStatus().name() : null)
            .maskedCustomerCpf(maskCpf(account.getCustomerCpf()))
            .accountNumber(account.getAccountNumber())
            .createdAt(account.getCreatedAt())
            .updatedAt(account.getUpdatedAt())
            .build();
    }

    private String resolveAccountId(AccountId accountId) {
        return accountId != null ? accountId.getValue() : null;
    }

    private java.math.BigDecimal resolveBalanceAmount(Money balance) {
        return balance != null ? balance.getAmount() : null;
    }

    private String resolveCurrency(Money balance) {
        if (balance == null || balance.getCurrency() == null) {
            return null;
        }
        return balance.getCurrency().getCurrencyCode();
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }
        return "***.***.***" + cpf.substring(cpf.length() - 2);
    }
}










