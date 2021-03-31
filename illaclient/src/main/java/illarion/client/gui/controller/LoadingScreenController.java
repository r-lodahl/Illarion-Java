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

import illarion.client.util.Lang;
import org.illarion.engine.ui.Action;
import org.illarion.engine.ui.UserInterface;
import org.illarion.engine.ui.stage.LoadingStage;
import org.jetbrains.annotations.Nullable;

public final class LoadingScreenController implements ScreenController {
    private final UserInterface userInterface;

    private LoadingStage loadingStage;

    public LoadingScreenController(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    @Override
    public void onStartScreen() {
        loadingStage = userInterface.activateLoadingStage(Lang.INSTANCE.getLoadingResourceBundle());
    }

    @Override
    public void onEndScreen() { }

    public void setLoadingProgess(float loadProgress, String elementToBeLoaded) {
        if (loadingStage == null) {
            throw new IllegalStateException("Screen was not yet started!");
        }

        loadingStage.setProgess(loadProgress);
        loadingStage.setLoadedElement(elementToBeLoaded);
    }

    public void setFailure(String localizationKey, Action callback) {
        if (loadingStage == null) {
            throw new IllegalStateException("Screen was not yet started!");
        }

        loadingStage.setFailure(localizationKey, callback);
    }
}
