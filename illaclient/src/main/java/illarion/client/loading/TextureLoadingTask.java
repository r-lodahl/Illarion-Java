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
package illarion.client.loading;

import illarion.common.util.ProgressMonitor;
import org.illarion.engine.assets.TextureManager;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * The purpose of this class is to load the texture resources that are required for the game.
 */
@NotThreadSafe
final class TextureLoadingTask implements LoadingTask {
    /**
     * The engine used to load the data.
     */
    @Nonnull
    private final TextureManager textureManager;

    /**
     * This flag is turned {@code true} once the loading is started for the first time.
     */
    private boolean loadingStarted;

    /**
     * Create a new texture loading task.
     *
     * @param textureManager the engine used to load the textures
     */
    TextureLoadingTask(@Nonnull TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    @Override
    public void load() {
        if (!loadingStarted) {
            textureManager.startLoading();
            loadingStarted = true;
        }
    }

    @Override
    public boolean isLoadingDone() {
        return textureManager.isLoadingDone();
    }

    @Nonnull
    @Override
    public ProgressMonitor getProgressMonitor() {
        return textureManager.getProgress();
    }
}
