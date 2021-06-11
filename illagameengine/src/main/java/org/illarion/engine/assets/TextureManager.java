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
package org.illarion.engine.assets;

import org.illarion.engine.graphic.Texture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;

/**
 * This interface provides access to the textures loaded by the backend. The references to those textures are used to
 * draw the respective images on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface TextureManager {
    /**
     * Add a directory that provides texture data.
     *
     * @param directory the directory providing the texture data
     */
    void addTextureDirectory(@NotNull String directory);

    /**
     * Get a specified texture.
     *
     * @param directory the directory to read the data from
     * @param name the name of the texture required
     * @return the loaded texture or {@code null} in case the texture requested does not exist
     */
    @Nullable
    Texture getTexture(@NotNull String directory, @NotNull String name);

    /**
     * Get a specified texture.
     *
     * @param name the name of the texture required
     * @return the loaded texture or {@code null} in case the texture requested does not exist
     */
    @Nullable
    Texture getTexture(@NotNull String name);

    /**
     * Calling this function starts the automatic loading of all texture atlas files in all the texture directories
     * that are currently set for the texture manager.
     * <p/>
     * In case the loading is already finished, this function does nothing.
     */
    void loadAll(Executor executor);
}
