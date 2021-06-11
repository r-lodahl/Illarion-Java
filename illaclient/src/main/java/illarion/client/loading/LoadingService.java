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

import org.illarion.engine.assets.Assets;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * This class is used to enlist the required loading tasks and perform the loading operations itself.
 */
public final class LoadingService {
    public static CompletableFuture<Void> loadAll(@NotNull Assets assets) {
        var executor = Executors.newFixedThreadPool(4);

        var soundLoadingTask = CompletableFuture.runAsync(new SoundLoadingTask(assets), executor);

        // Texture loading has to happen on the main thread (GL context needed)
        assets.getTextureManager().loadAll(executor);

        var resourceLoadingTask = CompletableFuture.runAsync(new ResourceTableLoading(assets), executor);

        executor.shutdown();

        return CompletableFuture.allOf(resourceLoadingTask, soundLoadingTask);
    }
}
