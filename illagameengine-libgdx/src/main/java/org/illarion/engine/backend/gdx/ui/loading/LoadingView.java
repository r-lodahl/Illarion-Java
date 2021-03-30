package org.illarion.engine.backend.gdx.ui.loading;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.illarion.engine.ui.NullSecureResourceBundle;

public final class LoadingView extends Table {
    private static final float PROGESS_BAR_SIDE_PADDING = 20.0f;

    private final NullSecureResourceBundle resourceBundle;

    private final Label lbLoading;
    private final ProgressBar pbLoading;

    public LoadingView(Skin skin, NullSecureResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        lbLoading = new Label("Loading", skin);
        pbLoading = new ProgressBar(0.0f, 100.0f, 1.0f, false, skin);

        row();
        add(pbLoading).pad(0.0f, PROGESS_BAR_SIDE_PADDING, 0.0f, PROGESS_BAR_SIDE_PADDING).fillX();
        row();
        add(lbLoading);
    }

    public void setText(String localizationKey) {
        lbLoading.setText(resourceBundle.getLocalizedString(localizationKey));
    }

    public void setProgess(float progess) {
        pbLoading.setValue(progess);
    }
}
