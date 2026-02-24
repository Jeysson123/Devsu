package com.devsu.backend.application.bus;

import java.util.HashMap;
import java.util.Map;

/**
 * QueryBus manages registration and dispatching of query handlers.
 */
public class QueryBus {

    private final Map<Class<?>, IQueryHandler<?, ?>> handlers = new HashMap<>();

    public <Q, R> void register(Class<Q> queryType, IQueryHandler<Q, R> handler) {
        handlers.put(queryType, handler);
    }

    @SuppressWarnings("unchecked")
    public <Q, R> R dispatch(Q query) {
        IQueryHandler<Q, R> handler = (IQueryHandler<Q, R>) handlers.get(query.getClass());
        if (handler == null) {
            throw new RuntimeException("No handler registered for " + query.getClass());
        }
        return handler.handle(query);
    }
}