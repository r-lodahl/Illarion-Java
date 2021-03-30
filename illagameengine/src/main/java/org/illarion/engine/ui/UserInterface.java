package org.illarion.engine.ui;

import org.illarion.engine.ui.stage.Stage;

public interface UserInterface {
    <T extends Stage> T activateStage(NullSecureResourceBundle loginResources);
    void removeActiveStage();
}
