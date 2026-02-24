package com.devsu.backend.web.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Centralizes all application messages and JWT configuration loaded from properties. */
@Component
@Getter
public class MessageProvider {

    // Clientes
    @Value("${message.client.created}")
    private String clientCreated;

    @Value("${message.client.update}")
    private String clientUpdated;

    @Value("${message.client.deleted}")
    private String clientDeleted;

    @Value("${message.client.error}")
    private String clientError;

    @Value("${message.client.retrieved}")
    private String clientRetrieved;

    @Value("${message.client.notFound}")
    private String clientNotFound;

    // Cuentas (Accounts)
    @Value("${message.account.created}")
    private String accountCreated;

    @Value("${message.account.update}")
    private String accountUpdated;

    @Value("${message.account.deleted}")
    private String accountDeleted;

    @Value("${message.account.error}")
    private String accountError;

    @Value("${message.account.retrieved}")
    private String accountRetrieved;

    @Value("${message.account.notFound}")
    private String accountNotFound;

    // Movimientos (Movements)
    @Value("${message.movement.created}")
    private String movementCreated;

    @Value("${message.movement.update}")
    private String movementUpdated;

    @Value("${message.movement.deleted}")
    private String movementDeleted;

    @Value("${message.movement.error}")
    private String movementError;

    @Value("${message.movement.retrieved}")
    private String movementRetrieved;

    @Value("${message.movement.notFound}")
    private String movementNotFound;

    @Value("${message.movement.insufficientBalance}")
    private String insufficientBalance;

    @Value("${message.movement.exceedsLimit}")
    private String exceedsLimit;

    @Value("${message.movement.dailyLimitExceeded}")
    private String dailyLimitExceeded;

    @Value("${daily.limit}")
    private Double dailyLimit;

    // Seguridad (JWT)
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.client-id}")
    private String authClientId;

    @Value("${jwt.password}")
    private String authPassword;

}