package org.illarion.engine.ui;

import illarion.common.config.ConfigReader;
import org.illarion.engine.graphic.ResolutionManager;

import java.util.function.Consumer;

public interface LoginStage {
    void setLoginData(LoginData[] data, int initialServer);
    void setCharacterData(CharacterSelectionData[] data);
    void setOptionsData(ConfigReader configReader, ResolutionManager resolutionManager);

    void setExitListener(Action event);
    void setLoginListener(Consumer<LoginData> event);
    void setLogoutListener(Action event);
    void setOptionsListener(Consumer<OptionsData> data);
    void setCharacterSelectionListener(Consumer<CharacterSelectionData> data);
    void setCharacterCreationListener(Consumer<CharacterCreationData> data);

    void informLoginResult(RequestResult result);
    void informCharacterCreationResult(RequestResult result);
}
