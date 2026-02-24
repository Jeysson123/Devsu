package com.devsu.backend.domain.factory;

import com.devsu.backend.infrastructure.persistence.Client;

/**
 * ClientFactory creates Client instances with specified properties.
 */
public final class ClientFactory {

    private ClientFactory() {
    }

    public static Client create(
            String name,
            String gender,
            Integer age,
            String identification,
            String address,
            String phone,
            String clientId,
            String password,
            Boolean status) {

        return Client.builder()
                .name(name)
                .gender(gender)
                .age(age)
                .identification(identification)
                .address(address)
                .phone(phone)
                .clientId(clientId)
                .password(password)
                .status(status)
                .build();
    }
}