package com.devsu.backend.application.bus;

/**
 * ICommandHandler defines a handler for processing commands of type A and returning R.
 */
public interface ICommandHandler<A, R> {
    R handle(A action);
}