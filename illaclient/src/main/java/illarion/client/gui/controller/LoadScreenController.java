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

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import org.illarion.nifty.controls.Progress;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class LoadScreenController implements ScreenController {

    @Nullable
    private Progress progress;

    //@NotNull
    //private final StateManager stateManager;

    public LoadScreenController() {
        //this.stateManager = stateManager;
    }

    @Override
    public void bind(@NotNull Nifty nifty, @NotNull Screen screen) {
        progress = screen.findNiftyControl("loading", Progress.class);
    }

    @Override
    public void onStartScreen() {
        loadingDoneCalled = false;
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

    @Override
    public void onEndScreen() {
        // nothing to do
    }
}
