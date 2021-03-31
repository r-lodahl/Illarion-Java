package org.illarion.engine.ui;

import org.illarion.engine.ui.stage.GameStage;
import org.illarion.engine.ui.stage.LoadingStage;
import org.illarion.engine.ui.stage.LoginStage;

public interface UserInterface {
    LoginStage activateLoginStage(NullSecureResourceBundle loginResourceBundle);
    LoadingStage activateLoadingStage(NullSecureResourceBundle loadingResourceBundle);
    GameStage activateGameStage(NullSecureResourceBundle gameResourceBundle);
}
