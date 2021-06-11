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

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import illarion.client.gui.controller.LoadingScreenController;
import illarion.client.loading.LoadingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.BackendBinding;
import org.illarion.engine.ui.Action;

import java.util.concurrent.Executors;

/**
 * This game state is active while the game loads. It takes care for showing the loading screen and to trigger the
 * actual loading.
 */
public final class LoadingState implements GameState {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ListeningExecutorService executorService;
    private final LoadingService loadingService;
    private final Action enterNextState;

    private BackendBinding binding;
    private LoadingScreenController controller;

    public LoadingState(Action enterNextState) {
        this.executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        this.loadingService = new LoadingService();
        this.enterNextState = enterNextState;
    }

    @Override
    public void create(BackendBinding binding) {
        this.binding = binding;
        this.controller = new LoadingScreenController(binding.getGui());
    }

    @Override
    public void dispose() {
        executorService.shutdown();
    }

    @Override
    public void update(int delta) { }

    @Override
    public void render() { }

    @Override
    public boolean isClosingGame() {
        return true;
    }

    @Override
    public void enterState()
    {
        controller.onStartScreen();

        var executor = Executors.newSingleThreadExecutor();

        LoadingService
                .loadAll(binding.getAssets())
                .thenRunAsync(enterNextState::invoke);

        executor.shutdown();
    }

    @Override
    public void leaveState() { }
}
