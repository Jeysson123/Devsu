package com.devsu.backend.domain.factory;

import com.devsu.backend.application.bus.ActionType;
import com.devsu.backend.application.bus.EntityType;
import com.devsu.backend.application.bus.GenericAction;

public final class ActionFactory {

    private ActionFactory() {}

    public static GenericAction createClientAction(ActionType action, Object payload) {
        return new GenericAction(EntityType.CLIENT, action, payload);
    }

    public static GenericAction createAccountAction(ActionType action, Object payload) {
        return new GenericAction(EntityType.ACCOUNT, action, payload);
    }

    public static GenericAction createMovementAction(ActionType action, Object payload) {
        return new GenericAction(EntityType.MOVEMENT, action, payload);
    }

    public static GenericAction createReportAction(ActionType action, Object payload) {
        return new GenericAction(EntityType.REPORT, action, payload);
    }

    public static GenericAction createExportAction(Object payload) {
        return new GenericAction(EntityType.REPORT, ActionType.EXECUTE, payload);
    }
}