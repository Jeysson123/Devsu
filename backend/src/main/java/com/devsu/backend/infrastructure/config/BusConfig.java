package com.devsu.backend.infrastructure.config;

import com.devsu.backend.application.bus.CommandBus;
import com.devsu.backend.application.bus.GenericAction;
import com.devsu.backend.application.bus.QueryBus;
import com.devsu.backend.application.command.GenericCommandHandler;
import com.devsu.backend.application.query.GenericQueryHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BusConfig {

    @Bean
    public CommandBus commandBus(GenericCommandHandler genericCommandHandler) {
        CommandBus bus = new CommandBus();
        bus.register(GenericAction.class, genericCommandHandler);
        return bus;
    }

    @Bean
    public QueryBus queryBus(GenericQueryHandler genericQueryHandler) {
        QueryBus bus = new QueryBus();
        bus.register(GenericAction.class, genericQueryHandler);
        return bus;
    }
}