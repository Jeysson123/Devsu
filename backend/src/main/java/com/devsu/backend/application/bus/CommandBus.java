package com.devsu.backend.application.bus;

import java.util.HashMap;
import java.util.Map;

public class CommandBus {

    private final Map<Class<?>, ICommandHandler<?, ?>> handlers = new HashMap<>();

    public <C, R> void register(Class<C> commandType, ICommandHandler<C, R> handler) {
        handlers.put(commandType, handler);
    }

    @SuppressWarnings("unchecked")
    public <C, R> R dispatch(C command) {
        ICommandHandler<C, R> handler = (ICommandHandler<C, R>) handlers.get(command.getClass());
        if (handler == null) {
            throw new RuntimeException("No handler registered for " + command.getClass());
        }
        return handler.handle(command);
    }
}