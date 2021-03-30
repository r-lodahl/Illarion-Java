package org.illarion.engine.backend.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.illarion.engine.backend.gdx.GdxRenderable;
import org.illarion.engine.backend.gdx.ui.loading.GdxLoadingStage;
import org.illarion.engine.backend.gdx.ui.login.GdxLoginStage;
import org.illarion.engine.ui.NullSecureResourceBundle;
import org.illarion.engine.ui.UserInterface;
import org.illarion.engine.ui.stage.LoadingStage;
import org.illarion.engine.ui.stage.LoginStage;
import org.illarion.engine.ui.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GdxUserInterface implements UserInterface {
    private final Skin skin;

    private final Map<Class, Class> stageTypeMap;


    private @Nullable GdxRenderable currentStage;

    public GdxUserInterface(Skin skin) {
        this.skin = skin;

        stageTypeMap = new HashMap<>(2);
        stageTypeMap.put(LoginStage.class, GdxLoginStage.class);
        stageTypeMap.put(LoadingStage.class, GdxLoadingStage.class);
    }

    @Override
    public <T extends Stage> T activateStage(Class<T> stageType, NullSecureResourceBundle resourceBundle) {
        if (!stageTypeMap.containsKey(stageType)) {
            throw new RuntimeException("NYI");
        }

        var implClass = stageTypeMap.get(stageType);

        implClass.getClassLoader().new

                // Fuck this, just add explicit "activateLogin/Loading/Game/...Stage methods!


        if (loginStage == null) {
            loginStage = new GdxLoginStage(skin, resourceBundle);
        }

        currentStage = loginStage;

        return loginStage;
    }

    @Override
    public void removeActiveStage() {
        currentStage = null;
        Optional.ofNullable(loginStage)
                .ifPresent(GdxLoginStage::dispose);
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
