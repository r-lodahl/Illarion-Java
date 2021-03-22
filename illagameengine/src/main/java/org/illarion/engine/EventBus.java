package org.illarion.engine;

public enum EventBus {
    INSTANCE;

    private final com.google.common.eventbus.EventBus eventBus = new com.google.common.eventbus.EventBus();

    public void register(Object object) {
        eventBus.register(object);
    }

    public void post(Object event) {
        eventBus.post(event);
    }
}
