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

import com.google.common.util.concurrent.*;
import illarion.client.IllaClient;
import illarion.client.gui.controller.LoadingScreenController;
import illarion.client.loading.LoadingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.illarion.engine.BackendBinding;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.Executors;

/**
 * This game state is active while the game loads. It takes care for showing the loading screen and to trigger the
 * actual loading.
 */
public final class LoadingState implements GameState {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ListeningExecutorService executorService;
    private final LoadingService loadingService;

    private BackendBinding binding;
    private LoadingScreenController controller;

    public LoadingState() {
        executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        loadingService = new LoadingService();
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
        Futures.addCallback(loadingService.loadAll(binding.getAssets(), controller::setLoadingProgess), new FutureCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void result) {
                // LEAVE STATE
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                LOGGER.warn("Resource Loading Failed: " + Arrays.toString(t.getStackTrace()));
                controller.setFailure("loadingfailed", IllaClient::exit);
            }
        }, executor);

        executor.shutdown();
    }

    @Override
    public void leaveState() { }
}
