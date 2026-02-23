package com.devsu.backend.application.bus;

public interface IQueryHandler<Q, R> {
    R handle(Q query);
}