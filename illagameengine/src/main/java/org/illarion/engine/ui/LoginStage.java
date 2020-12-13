package org.illarion.engine.ui;

import java.util.function.Consumer;

public interface LoginStage {
    void setLoginData(LoginData[] data);
    void setCharacterData(CharacterSelectionData[] data);
    void setOptionsData(OptionsData data);

    void setExitListener(Action event);
    void setLoginListener(Consumer<LoginData> event);
    void setLogoutListener(Action event);
    void setOptionsListener(Consumer<OptionsData> data);
    void setCharacterSelectionListener(Consumer<CharacterSelectionData> data);
    void setCharacterCreationListener(Consumer<CharacterCreationData> data);

    void informLoginResult(RequestResult result);
    void informCharacterCreationResult(RequestResult result);
}
