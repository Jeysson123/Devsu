package com.devsu.backend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/** DTO representing a single account transaction report with client, account, and movement details. */
@Getter
@Builder
@AllArgsConstructor
public class AccountReport {
    private LocalDateTime date;
    private String clientName;
    private String accountNumber;
    private String accountType;
    private Double initialBalance;
    private Boolean accountStatus;
    private Double movementAmount;
    private Double availableBalance;
}