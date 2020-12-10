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

import org.jetbrains.annotations.Contract;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is used to supply and compare possible display resolutions.
 */
public final class GraphicResolution {
    /**
     * The bits per point of this resolution.
     */
    public final int bitsPerPoint;

    /**
     * The screen height of this resolution.
     */
    public final int height;

    /**
     * The refresh rate of this resolution.
     */
    public final int refreshRate;

    /**
     * The screen width of that resolution.
     */
    public final int width;

    /**
     * Constructor for a graphic resolution definition.
     *
     * @param width the width of this resolution in pixel
     * @param height the height of this resolution in pixel
     * @param bitsPerPoint the bits per point of this resolution
     * @param refreshRate the refresh rate of this resolution in Hz
     */
    public GraphicResolution(int width, int height, int bitsPerPoint, int refreshRate) {
        this.height = height;
        this.width = width;
        this.bitsPerPoint = bitsPerPoint;
        this.refreshRate = refreshRate;
    }

    /**
     * Compare this resolution to another object.
     *
     * @param obj the object this resolution is to be compared with
     * @return {@code true} in case both objects are equal
     */
    @Override
    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof GraphicResolution)) {
            return false;
        }

        GraphicResolution resolution = (GraphicResolution) obj;
        return height == resolution.height &&
                width == resolution.width &&
                bitsPerPoint == resolution.bitsPerPoint &&
                refreshRate == resolution.refreshRate;
    }

    /**
     * Generate a hash code for this graphic resolution object.
     *
     * @return the hash code of this object
     */
    @Override
    @Contract(pure = true)
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Get a human readable string that describes that resolution.
     *
     * @return human readable string to describe that resolution
     */
    @Nonnull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(width);
        builder.append(' ').append('x').append(' ');
        builder.append(height);
        if (bitsPerPoint > -1) {
            builder.append(' ').append('x').append(' ');
            builder.append(bitsPerPoint);
        }
        if (refreshRate > -1) {
            builder.append(' ').append('@').append(' ');
            builder.append(refreshRate);
            builder.append('H').append('z');
        }
        return builder.toString();
    }
}
