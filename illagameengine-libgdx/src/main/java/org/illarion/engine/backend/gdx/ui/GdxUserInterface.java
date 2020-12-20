package org.illarion.engine.backend.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import illarion.common.config.ConfigReader;
import org.illarion.engine.backend.gdx.GdxRenderable;
import org.illarion.engine.backend.gdx.ui.login.GdxLoginStage;
import org.illarion.engine.ui.LoginStage;
import org.illarion.engine.ui.NullSecureResourceBundle;
import org.illarion.engine.ui.UserInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GdxUserInterface implements UserInterface {
    private static final Logger log = LoggerFactory.getLogger(GdxUserInterface.class);

    private final Skin skin;

    private GdxLoginStage loginStage;
    private GdxRenderable currentStage;

    public GdxUserInterface(Skin skin) {
        this.skin = skin;
    }

    @Override
    public LoginStage activateLoginStage(NullSecureResourceBundle resourceBundle) {
        if (loginStage == null) {
            loginStage = new GdxLoginStage(skin, resourceBundle);
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
