package com.devsu.backend.domain.factory;

import com.devsu.backend.infrastructure.persistence.Account;
import com.devsu.backend.infrastructure.persistence.Client;

public final class AccountFactory {

    private AccountFactory() {
    }

    // He añadido el parámetro 'Client client'
    public static Account create(
            String accountNumber,
            String accountType,
            Double initialBalance,
            Boolean status,
            Client client) { // <--- AÑADIDO

        return Account.builder()
                .accountNumber(accountNumber)
                .accountType(accountType)
                .initialBalance(initialBalance)
                .status(status)
                .client(client) // <--- AÑADIDO
                .build();
    }
}