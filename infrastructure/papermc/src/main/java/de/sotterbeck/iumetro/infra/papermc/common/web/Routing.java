package de.sotterbeck.iumetro.infra.papermc.common.web;

import com.google.inject.Injector;

import jakarta.inject.Inject;

public abstract class Routing<T> {

    @Inject
    private Injector injector;
    private final Class<T> controllerClass;

    protected Routing(Class<T> controllerClass) {
        this.controllerClass = controllerClass;
    }

    protected T controller() {
        return injector.getInstance(controllerClass);
    }

    public abstract void bindRoutes();
}
