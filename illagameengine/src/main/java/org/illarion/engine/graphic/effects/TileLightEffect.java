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
package org.illarion.engine.graphic.effects;

import org.illarion.engine.graphic.Color;
import org.jetbrains.annotations.NotNull;


/**
 * This effect is used to render the light on a texture.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface TileLightEffect extends TextureEffect {
    /**
     * Set the color of the tile top left of the rendered tile.
     *
     * @param color the color of the tile
     */
    void setTopLeftColor(@NotNull Color color);

    /**
     * Set the color of the tile top right of the rendered tile.
     *
     * @param color the color of the tile
     */
    void setTopRightColor(@NotNull Color color);

    /**
     * Set the color of the tile bottom left of the rendered tile.
     *
     * @param color the color of the tile
     */
    void setBottomLeftColor(@NotNull Color color);

    /**
     * Set the color of the tile bottom right of the rendered tile.
     *
     * @param color the color of the tile
     */
    void setBottomRightColor(@NotNull Color color);

    /**
     * Set the color of the tile top of the rendered tile.
     *
     * @param color the color of the tile
     */
    void setTopColor(@NotNull Color color);

    /**
     * Set the color of the tile bottom of the rendered tile.
     *
     * @param color the color of the tile
     */
    void setBottomColor(@NotNull Color color);

    /**
     * Set the color of the tile left of the rendered tile.
     *
     * @param color the color of the tile
     */
    void setLeftColor(@NotNull Color color);

    /**
     * Set the color of the tile right of the rendered tile.
     *
     * @param color the color of the tile
     */
    void setRightColor(@NotNull Color color);

    /**
     * Set the color in the center of the rendered tile.
     *
     * @param color the color of the tile
     */
    void setCenterColor(@NotNull Color color);
}
