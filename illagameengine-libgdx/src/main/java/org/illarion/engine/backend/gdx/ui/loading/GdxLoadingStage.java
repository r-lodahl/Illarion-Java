package org.illarion.engine.backend.gdx.ui.loading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.illarion.engine.backend.gdx.GdxRenderable;
import org.illarion.engine.ui.*;
import org.illarion.engine.ui.stage.LoadingStage;

public class GdxLoadingStage implements LoadingStage, GdxRenderable {
    private final Stage stage;
    private final LoadingView loadingView;

    public GdxLoadingStage(Skin skin, NullSecureResourceBundle resourceBundle) {
        loadingView = new LoadingView(skin, resourceBundle);

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
    public void setText(String localizationKey) {
        loadingView.setText(localizationKey);
    }

    @Override
    public void setProgess(float progess) {
        loadingView.setProgess(progess);
    }

    public void dispose() {
        stage.dispose();
    }
}
