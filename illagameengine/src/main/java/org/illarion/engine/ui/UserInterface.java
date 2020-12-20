package org.illarion.engine.ui;

import illarion.common.config.ConfigReader;

public interface UserInterface {
    LoginStage activateLoginStage(NullSecureResourceBundle loginResources);
    void removeLoginStage();
}
