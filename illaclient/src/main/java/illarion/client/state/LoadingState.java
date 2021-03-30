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
package illarion.client.state;

import illarion.client.gui.controller.LoadingScreenController;
import illarion.client.loading.Loading;
import org.illarion.engine.BackendBinding;
import org.jetbrains.annotations.NotNull;


/**
 * This game state is active while the game loads. It takes care for showing the loading screen and to trigger the
 * actual loading.
 */
public final class LoadingState implements GameState {
    /**
     * The screen controller that handles the display of the loading progress.
     */
    private LoadingScreenController controller;

    /**
     * The manager of the loading tasks.
     */
    @NotNull
    private final Loading loadingManager = new Loading();

    private BackendBinding binding;

    @Override
    public void create(BackendBinding binding) {
        this.binding = binding;
        controller = new LoadingScreenController();
        //nifty.registerScreenController(controller);
        //Util.loadXML(nifty, "illarion/client/gui/xml/loading.xml");
    }

    @Override
    public void dispose() { }

    @Override
    public void update(int delta) { }

    @Override
    public void render() {
        loadingManager.load();
        controller.setProgress(loadingManager.getProgress());

        if (loadingManager.isLoadingDone()) {
            controller.loadingDone();
        }
    }

    @Override
    public boolean isClosingGame() {
        return true;
    }

    @Override
    public void enterState() {
        loadingManager.enlistMissingComponents(binding.getAssets());
        //nifty.gotoScreen("loading");
    }

    @Override
    public void leaveState() { }
}
