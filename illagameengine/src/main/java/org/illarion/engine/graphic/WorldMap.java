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
package org.illarion.engine.graphic;

import illarion.common.types.ServerCoordinate;
import org.illarion.engine.Disposable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * This interfaces defines how the world map is drawn. This map is drawn on a fully black texture.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface WorldMap extends Disposable {
    /**
     * This constant is used to define that a specific tile is empty.
     */
    int NO_TILE = 0;

    /**
     * The amount of tiles that are stored on the world map texture in height.
     */
    int WORLD_MAP_HEIGHT = 1024;

    /**
     * The amount of tiles that are stored on the world map texture in width.
     */
    int WORLD_MAP_WIDTH = 1024;

    /**
     * Get the origin location of the world map.
     *
     * @return the origin location
     */
    @Nullable
    ServerCoordinate getMapOrigin();

    /**
     * Get the last reported location of the player.
     *
     * @return the player location
     */
    @Nullable
    ServerCoordinate getPlayerLocation();

    /**
     * Get the texture of the world map.
     *
     * @return the world map texture
     */
    @NotNull
    Texture getWorldMap();

    /**
     * Mark a tile as changed. The world map is supposed to update this tile at some later point.
     *
     * @param location the location of the tile that was changed
     */
    void setTileChanged(@NotNull ServerCoordinate location);

    /**
     * Mark the entire map as changed. Once this is done the entire map needs to be updated.
     */
    void setMapChanged();

    /**
     * Set the location of the player on the map. This is used in some backend to prioritise the areas updated on the
     * world map.
     *
     * @param location the new location of the map
     */
    void setPlayerLocation(@NotNull ServerCoordinate location);

    /**
     * Set the origin location of the world map. The map will expand starting from this point a specific amount of
     * tiles defined by {@link #WORLD_MAP_HEIGHT} and {@link #WORLD_MAP_WIDTH}.
     * <p/>
     * Changing the origin location automatically clears the map.
     *
     * @param location the new origin location of the map
     */
    void setMapOrigin(@NotNull ServerCoordinate location);

    /**
     * This function causes all map data to be thrown away. The map turned empty.
     */
    void clear();

    /**
     * Render the changes to the world map. This does not render anything to the screen and it will not render
     * anything at all mostly. It will update the texture of the world map in case there are updates pending that
     * need to be applied.
     * <p/>
     * This function needs to be called during the render loop.
     */
    void render();
}
