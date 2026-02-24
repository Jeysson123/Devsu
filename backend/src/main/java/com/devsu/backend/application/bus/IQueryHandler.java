package com.devsu.backend.application.bus;

/**
 * IQueryHandler defines a handler for processing queries of type Q and returning R.
 */
public interface IQueryHandler<Q, R> {
    R handle(Q query);
}