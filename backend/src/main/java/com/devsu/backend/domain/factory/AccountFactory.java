package com.devsu.backend.domain.factory;

import com.devsu.backend.infrastructure.persistence.Account;
import com.devsu.backend.infrastructure.persistence.Client;

/**
 * AccountFactory creates Account instances with specified properties.
 */
public final class AccountFactory {

    private AccountFactory() {
    }

    public static Account create(
            String accountNumber,
            String accountType,
            Double initialBalance,
            Boolean status,
            Client client) {

        return Account.builder()
                .accountNumber(accountNumber)
                .accountType(accountType)
                .initialBalance(initialBalance)
                .status(status)
                .client(client)
                .build();
    }
}