package org.illarion.engine.backend.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.illarion.engine.backend.gdx.GdxRenderable;
import org.illarion.engine.backend.gdx.ui.login.GdxLoginController;
import org.illarion.engine.ui.LoginStage;
import org.illarion.engine.ui.NullSecureResourceBundle;
import org.illarion.engine.ui.UserInterface;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GdxUserInterface implements UserInterface {
    private final Skin skin;

    private @Nullable GdxLoginController loginStage;
    private @Nullable GdxRenderable currentStage;

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
        Optional.ofNullable(loginStage)
                .ifPresent(GdxLoginController::dispose);
        loginStage = null;
    }

    public void render() {
        if (currentStage != null) {
            currentStage.render();
        }
    }

    public void resize(int width, int height) {
        Optional.ofNullable(currentStage)
                .ifPresent(stage -> stage.resize(width, height));
    }
}
