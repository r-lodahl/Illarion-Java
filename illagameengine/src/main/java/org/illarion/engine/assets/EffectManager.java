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
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.effects.*;
import org.jetbrains.annotations.NotNull;


/**
 * This is the manager that creates and maintains the references to all the effects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface EffectManager {
    /**
     * Get the effect that can be used to render the mini map from the world map.
     *
     * @param worldMap the world map that supplies this effect with the required data
     * @param sharedInstance {@code true} to return the shared single instance, {@code false} to get a new instance
     * @return the mini map effect
     * @throws EngineException in case loading the effect fails
     */
    @NotNull
    MiniMapEffect getMiniMapEffect(@NotNull WorldMap worldMap, boolean sharedInstance) throws EngineException;

    /**
     * Get the effect that should be used to highlight objects on the map.
     *
     * @param sharedInstance {@code true} to receive the shared instance, {@code false} to create a new one
     * @return the highlight effect
     * @throws EngineException in case creating the effect fails
     */
    @NotNull
    HighlightEffect getHighlightEffect(boolean sharedInstance) throws EngineException;

    /**
     * Get the effect that is used to render the fog on the entire scene.
     *
     * @param sharedInstance {@code true} to receive the shared instance, {@code false} to create a new one
     * @return the fog effect
     * @throws EngineException in case creating the effect fails
     */
    @NotNull
    FogEffect getFogEffect(boolean sharedInstance) throws EngineException;

    /**
     * Get the effect that is used to render the scene or a single texture as gray scale.
     *
     * @param sharedInstance {@code true} to receive the shared instance, {@code false} to create a new one
     * @return the gray scale effect
     * @throws EngineException in case creating the effect fails
     */
    @NotNull
    GrayScaleEffect getGrayScaleEffect(boolean sharedInstance) throws EngineException;

    /**
     * Get the effect that is used to render the light on a tile.
     *
     * @param sharedInstance {@code true} to receive the shared instance, {@code false} to create a new one
     * @return the tile light effect
     * @throws EngineException in case creating the effect fails
     */
    @NotNull
    TileLightEffect getTileLightEffect(boolean sharedInstance) throws EngineException;
}
