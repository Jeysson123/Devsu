package com.devsu.backend.application.bus;

/**
 * GenericAction represents an action on an entity with its payload.
 */
public record GenericAction(EntityType entityType, ActionType action, Object payload) { }