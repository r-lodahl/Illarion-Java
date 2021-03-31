package org.illarion.engine.backend.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.apache.commons.lang3.NotImplementedException;
import org.illarion.engine.backend.gdx.GdxRenderable;
import org.illarion.engine.backend.gdx.ui.loading.GdxLoadingStage;
import org.illarion.engine.backend.gdx.ui.login.GdxLoginStage;
import org.illarion.engine.ui.NullSecureResourceBundle;
import org.illarion.engine.ui.UserInterface;
import org.illarion.engine.ui.stage.GameStage;
import org.illarion.engine.ui.stage.LoadingStage;
import org.illarion.engine.ui.stage.LoginStage;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GdxUserInterface implements UserInterface {
    private final Skin skin;

    @Nullable private GdxRenderable currentStage;

    public GdxUserInterface(Skin skin) {
        this.skin = skin;
    }

    @Override
    public LoginStage activateLoginStage(NullSecureResourceBundle loginResourceBundle) {
        detachActiveStage();

        var loginStage = new GdxLoginStage(skin, loginResourceBundle);
        currentStage = loginStage;

        return loginStage;
    }

    @Override
    public LoadingStage activateLoadingStage(NullSecureResourceBundle loadingResourceBundle) {
        detachActiveStage();

        var loadingStage = new GdxLoadingStage(skin, loadingResourceBundle);
        currentStage = loadingStage;

        return loadingStage;
    }

    @Override
    public GameStage activateGameStage(NullSecureResourceBundle gameResourceBundle) {
        throw new NotImplementedException();
    }

    private void detachActiveStage() {
        Optional.ofNullable(currentStage)
                .ifPresent(GdxRenderable::dispose);

        currentStage = null;
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
