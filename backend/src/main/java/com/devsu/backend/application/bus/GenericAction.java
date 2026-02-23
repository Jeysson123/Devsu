package com.devsu.backend.application.bus;

public record GenericAction(EntityType entityType, ActionType action, Object payload) { }