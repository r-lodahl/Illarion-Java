package org.illarion.engine.ui;

import illarion.common.config.ConfigReader;
import org.illarion.engine.graphic.ResolutionManager;

import java.util.Map;
import java.util.function.Consumer;

public interface LoginStage {
    void setLoginData(LoginData[] data, int initialServer);
    void loginSuccessful(CharacterSelectionData[] data);
    void loginFailed();
    void setOptionsData(ConfigReader configReader, ResolutionManager resolutionManager);

    void setExitListener(Action event);
    void setLoginListener(Consumer<LoginData> event);
    void setLogoutListener(Action event);
    void setOptionsSaveListener(Consumer<Map<String, String>> event);
    void setCharacterSelectionListener(Consumer<CharacterSelectionData> data);
    void setCharacterCreationListener(Consumer<CharacterCreationData> data);

    void informLoginResult(RequestResult result);
    void informCharacterCreationResult(RequestResult result);
}
