package org.illarion.engine.backend.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.backend.gdx.GdxRenderable;
import org.illarion.engine.backend.gdx.ui.login.GdxLoginController;
import org.illarion.engine.ui.LoginStage;
import org.illarion.engine.ui.NullSecureResourceBundle;
import org.illarion.engine.ui.UserInterface;

public class GdxUserInterface implements UserInterface {
    private static final Logger log = LogManager.getLogger();

    private final Skin skin;

    private GdxLoginController loginStage;
    private GdxRenderable currentStage;

    public GdxUserInterface(Skin skin) {
        this.skin = skin;
    }

    @Override
    public LoginStage activateLoginStage(NullSecureResourceBundle resourceBundle) {
        if (loginStage == null) {
            loginStage = new GdxLoginController(skin, resourceBundle);
        }

        currentStage = loginStage;

        return loginStage;
    }

    @Override
    public void removeLoginStage() {
        currentStage = null;
        loginStage.dispose();
        loginStage = null;
    }

    public void render() {
        if (currentStage != null) {
            currentStage.render();
        }
    }
}
