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

import org.illarion.engine.EngineException;
import org.illarion.engine.graphic.Scene;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.WorldMapDataProvider;
import org.jetbrains.annotations.NotNull;


/**
 * This interface defines how the assets that need to be managed by the game engine can be accessed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Assets {
    /**
     * Get the manager for the texture assets.
     *
     * @return the texture asset manager
     */
    @NotNull
    TextureManager getTextureManager();

    /**
     * Get the manager for the font assets.
     *
     * @return the font asset manager
     */
    @NotNull
    FontManager getFontManager();

    /**
     * Get the manager for the mouse cursor assets.
     *
     * @return the mouse cursor asset manager
     */
    @NotNull
    CursorManager getCursorManager();

    /**
     * Get the manager for the sound assets.
     *
     * @return the sound asset manager
     */
    @NotNull
    SoundsManager getSoundsManager();

    /**
     * Get the factory that is used to create sprites.
     *
     * @return the sprite factory
     */
    @NotNull
    SpriteFactory getSpriteFactory();

    /**
     * Create a new scene instance that should be used for rendering the game.
     *
     * @return the newly created scene
     */
    @NotNull
    Scene createNewScene();

    /**
     * Create a new world map instance. This class is then used to create the world map texture that is displayed in
     * the game.
     *
     * @param provider the provider that will supply the world map with the required data
     * @return the newly created world map texture creator
     * @throws EngineException in case creating the world map fails for some reason
     */
    @NotNull
    WorldMap createWorldMap(@NotNull WorldMapDataProvider provider) throws EngineException;

    /**
     * Get the manager for the graphical effects.
     *
     * @return the graphic effect manager
     */
    @NotNull
    EffectManager getEffectManager();
}
