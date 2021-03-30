package org.illarion.engine.ui.stage;

import illarion.common.config.ConfigReader;
import org.illarion.engine.graphic.ResolutionManager;
import org.illarion.engine.ui.*;

import java.util.Map;
import java.util.function.Consumer;

public interface LoginStage extends Stage {
    void setLoginData(LoginData[] data, int initialServer);
    void loginSuccessful(CharacterSelectionData[] data);
    void loginFailed();
    void setOptionsData(ConfigReader configReader, ResolutionManager resolutionManager);

    void setExitListener(Action event);
    void setLoginListener(Consumer<LoginData> event);
    void setOptionsSaveListener(Consumer<Map<String, String>> event);
    void setCharacterSelectionListener(Consumer<CharacterSelectionData> data);
    void setCharacterCreationListener(Consumer<CharacterCreationData> data);

    void informLoginResult(RequestResult result);
    void informCharacterCreationResult(RequestResult result);
}
