package org.illarion.engine.ui;

public class RequestResult {
    public final boolean isRequestSuccessful;
    public final String errorMessage;

    public RequestResult(boolean isRequestSuccessful, String errorMessage) {
        this.isRequestSuccessful = isRequestSuccessful;
        this.errorMessage = errorMessage;
    }
}
