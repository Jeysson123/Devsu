package com.devsu.backend.domain.service;

import com.devsu.backend.infrastructure.persistence.Account;
import com.devsu.backend.infrastructure.persistence.Movement;
import com.devsu.backend.web.config.MessageProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

/**
 * AccountValidatorService validates account operations and calculates new balances.
 */
@Service
@RequiredArgsConstructor
public class AccountValidatorService {

    private final MessageProvider messageProvider;

    public Double calculateNewBalance(Account account, Movement movement) {
        String type = movement.getMovementType();
        Double currentBalance = movement.getBalance();
        Double amount = movement.getAmount();

        if ("Credito".equalsIgnoreCase(type)) {
            return currentBalance + amount;
        } else if ("Debito".equalsIgnoreCase(type)) {
            if (currentBalance == 0) {
                throw new IllegalStateException(messageProvider.getInsufficientBalance());
            }
            else if (currentBalance < amount) {
                throw new IllegalStateException(messageProvider.getExceedsLimit());
            }
            return currentBalance - amount;
        }
        return currentBalance;
    }

    public void validateDailyLimit(Account account, Double amount, String type) {
        if ("Debito".equalsIgnoreCase(type)) {
            Double dailyDebits = account.getMovements().stream()
                    .filter(m -> "Debito".equalsIgnoreCase(m.getMovementType()))
                    .filter(m -> m.getDate().toLocalDate().isEqual(LocalDate.now()))
                    .mapToDouble(Movement::getAmount)
                    .sum();

            if ((dailyDebits + amount) > messageProvider.getDailyLimit()) {
                throw new IllegalStateException(messageProvider.getDailyLimitExceeded());
            }
        }
    }
}