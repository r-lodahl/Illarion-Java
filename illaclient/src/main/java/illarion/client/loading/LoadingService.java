/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.client.loading;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.assets.Assets;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * This class is used to enlist the required loading tasks and perform the loading operations itself.
 */
public final class LoadingService {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int POLLING_RATE = 15;

    private final Object pollingLock = new Object();

    public ListenableFuture<Void> loadAll(@NotNull Assets assets, BiConsumer<Float, String> loadingProgressChanged) {
        var executor = Executors.newSingleThreadExecutor();

        var loadingAssets = Futures.submit(() -> {
            var loadTexturesTask = new TextureLoadingTask(assets.getTextureManager());
            waitForLoading(loadTexturesTask);
            loadingProgressChanged.accept(0.333f, "Textures");

            var loadResourcesTask = new ResourceTableLoading(assets);
            waitForLoading(loadResourcesTask);
            loadingProgressChanged.accept(0.333f, "Resources");

            var loadSoundsTask = new SoundLoadingTask(assets);
            waitForLoading(loadSoundsTask);
            loadingProgressChanged.accept(0.333f, "Sounds");
        }, executor);

        executor.shutdown();

        return loadingAssets;
    }

    private void waitForLoading(LoadingTask task) {
        synchronized (pollingLock) {
            while (!task.isLoadingDone()) {
                try {
                    task.load();
                    pollingLock.wait(POLLING_RATE);
                } catch (InterruptedException e) {
                    LOGGER.error("Loading of resource failed: " + Arrays.toString(e.getStackTrace()));
                    throw new RuntimeException("Loading of resource failed.");
                }
            }
        }
    }
}
