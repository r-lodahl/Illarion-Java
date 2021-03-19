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
package illarion.client.resources.data;

import illarion.client.graphics.AbstractEntity;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Sprite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This template in general stores the data required to create any class that inherits
 * {@link AbstractEntity} and has additional support for animations.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AbstractAnimatedEntityTemplate extends AbstractMultiFrameEntityTemplate {
    /**
     * The animation speed.
     */
    private final int speed;

    /**
     * The constructor of this class.
     *
     * @param id the identification number of the entity
     * @param sprite the sprite used to render the entity
     * @param frames the total amount of frames
     * @param stillFrame the frame displayed in case no frame animation is running
     * @param speed the animation speed of this entity
     * @param defaultColor the default color of the entity
     * @param shadowOffset the offset of the shadow
     */
    protected AbstractAnimatedEntityTemplate(
            int id,
            @NotNull Sprite sprite,
            int frames,
            int stillFrame,
            int speed,
            @Nullable Color defaultColor,
            int shadowOffset) {
        super(id, sprite, frames, stillFrame, defaultColor, shadowOffset);
        this.speed = speed;
    }

    public int getAnimationSpeed() {
        return speed;
    }
}
