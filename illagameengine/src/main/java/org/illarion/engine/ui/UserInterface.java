package org.illarion.engine.ui;

public interface UserInterface {
    LoginStage activateLoginStage(NullSecureResourceBundle loginResources);
    void removeLoginStage();
}
