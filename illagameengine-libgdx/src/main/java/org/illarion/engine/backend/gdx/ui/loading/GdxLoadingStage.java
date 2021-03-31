package org.illarion.engine.backend.gdx.ui.loading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.illarion.engine.backend.gdx.GdxRenderable;
import org.illarion.engine.ui.*;
import org.illarion.engine.ui.stage.LoadingStage;

public class GdxLoadingStage implements LoadingStage, GdxRenderable {
    private final Stage stage;
    private final LoadingView loadingView;

    private final Dialog dialog;
    private final TextButton btDialog;

    private Action dialogConfirmationCallback;

    public GdxLoadingStage(Skin skin, NullSecureResourceBundle resourceBundle) {
        loadingView = new LoadingView(skin, resourceBundle);

        btDialog = new TextButton("OK", skin);
        btDialog.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                if (dialogConfirmationCallback != null) {
                    dialogConfirmationCallback.invoke();
                }
            }
        });
        dialog = new Dialog("Attention", skin, "dialog");
        dialog.button(btDialog);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        stage.addActor(loadingView);
        loadingView.setFillParent(true);
        loadingView.center();
    }

    @Override
    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        loadingView.setSize(width, height);
    }

    @Override
    public void setLoadedElement(String localizationKey) {
        Gdx.app.postRunnable(() -> loadingView.setText(localizationKey));
    }

    @Override
    public void setProgess(float progess) {
        Gdx.app.postRunnable(() -> loadingView.setProgess(progess));
    }

    @Override
    public void setFailure(String localizationKey, Action failureCallback) {
        Gdx.app.postRunnable(() -> {
            dialogConfirmationCallback = failureCallback;
            dialog.button(btDialog);
            dialog.show(stage);
        });
    }

    public void dispose() {
        stage.dispose();
    }
}
