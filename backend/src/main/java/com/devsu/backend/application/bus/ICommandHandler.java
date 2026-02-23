package com.devsu.backend.application.bus;

public interface ICommandHandler<A, R> {
    R handle(A action);
}