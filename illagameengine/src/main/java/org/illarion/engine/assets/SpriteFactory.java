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
package org.illarion.engine.assets;

import org.illarion.engine.graphic.Sprite;
import org.illarion.engine.graphic.Texture;
import org.jetbrains.annotations.NotNull;


/**
 * The sprite factory is used to create sprite objects that can be used by the specified backend for more complex
 * render operations.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@FunctionalInterface
public interface SpriteFactory {
    /**
     * Center location value for the x coordinate. This places the center point at the left side of the sprite.
     */
    float LEFT = 0.f;

    /**
     * Center location value for both the x and the y coordinate. It places the origin to the center of the sprite.
     */
    float CENTER = 0.5f;

    /**
     * Center location value for the y coordinate. This places the center point at the top of the sprite.
     */
    float TOP = 0.f;

    /**
     * Center location value for the y coordinate. This places the center point at the bottom of the sprite.
     */
    float BOTTOM = 1.f;
    /**
     * Center location value for the x coordinate. This places the center point at the right side of the sprite.
     */
    float RIGHT = 1.f;

    /**
     * Create a new sprite.
     *
     * @param textures the textures assigned to this sprite, the textures need to have the same size
     * @param offsetX the x offset that is applied to the texture
     * @param offsetY the y offset that is applied to the texture
     * @param centerX the x offset of the center (between {@code 0.f} and {@code 1.f}
     * @param centerY the y offset of the center (between {@code 0.f} and {@code 1.f}
     * @param mirror {@code true} in case the textures are supposed to be rendered mirrored
     * @return the created sprite
     */
    @NotNull
    Sprite createSprite(
            @NotNull Texture[] textures, int offsetX, int offsetY, float centerX, float centerY, boolean mirror);
}
