package com.devsu.backend.domain.factory;

import com.devsu.backend.infrastructure.persistence.Account;
import com.devsu.backend.infrastructure.persistence.Movement;

import java.time.LocalDateTime;

public final class MovementFactory {

    private MovementFactory() {
    }

    public static Movement create(
            Account account,
            String movementType,
            Double amount,
            Double balance) {

        return Movement.builder()
                .account(account)
                .movementType(movementType)
                .amount(amount)
                .balance(balance)
                .date(LocalDateTime.now())
                .build();
    }
}