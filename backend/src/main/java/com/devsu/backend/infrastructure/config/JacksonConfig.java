package com.devsu.backend.infrastructure.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JacksonConfig registers Hibernate6Module for JSON serialization with Hibernate entities.
 */
@Configuration
public class JacksonConfig {
    @Bean
    public Hibernate6Module hibernateModule() {

        return new Hibernate6Module();
    }
}