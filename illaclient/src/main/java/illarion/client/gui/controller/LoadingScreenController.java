/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.gui.controller;

import com.badlogic.gdx.scenes.scene2d.Stage;
import illarion.client.util.Lang;
import org.illarion.engine.ui.UserInterface;
import org.illarion.engine.ui.stage.LoadingStage;
import org.jetbrains.annotations.Nullable;

public final class LoadingScreenController implements ScreenController {
    private final UserInterface userInterface;

    @Nullable
    private LoadingStage loadingStage;

    public LoadingScreenController(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    @Override
    public void onStartScreen() {
        loadingStage = userInterface.activateStage(Lang.INSTANCE.getLoadingResourceBundle());
    }

    @Override
    public void onEndScreen() { }

    public void applyLoadingProgess(float loadProgress, String elementToBeLoaded) {

    }



    private boolean loadingDoneCalled;

    public void loadingDone() {
        if (loadingDoneCalled) {
            return;
        }
        loadingDoneCalled = true;

        //stateManager.enterState(StateManager.State.LOGIN);
    }

    public void setProgress(float progressValue) {
        if (progress != null) {
            progress.setProgress(progressValue);
        }
    }


}
