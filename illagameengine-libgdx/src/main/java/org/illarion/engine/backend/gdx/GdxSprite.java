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
package org.illarion.engine.backend.gdx;

import org.illarion.engine.backend.shared.AbstractSprite;
import org.jetbrains.annotations.NotNull;


/**
 * The sprite implementation of libGDX.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxSprite extends AbstractSprite<GdxTexture> {
    /**
     * Create a new sprite.
     *
     * @param textures the textures that are the frames of this sprite
     * @param offsetX the x offset of the sprite
     * @param offsetY the y offset of the sprite
     * @param centerX the offset of the center point long the x coordinate
     * @param centerY the offset of the center point long the y coordinate
     * @param mirror the mirrored flag
     */
    protected GdxSprite(
            @NotNull GdxTexture[] textures, int offsetX, int offsetY, float centerX, float centerY, boolean mirror) {
        super(textures, offsetX, offsetY, centerX, centerY, mirror);
    }
}
