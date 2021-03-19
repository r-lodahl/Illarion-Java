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
package illarion.client.resources.data;

import illarion.client.graphics.AvatarClothManager;
import illarion.client.graphics.AvatarInfo;
import illarion.common.types.Direction;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Sprite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is the template that contains the required data to create the graphical representation of a avatar on the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AvatarTemplate extends AbstractMultiFrameEntityTemplate {
    /**
     * The direction this avatar is facing.
     */
    @NotNull
    private final Direction direction;

    /**
     * The clothes this avatar is able to wear.
     */
    @NotNull
    private final AvatarClothManager clothes;

    /**
     * General information about this avatar.
     */
    @NotNull
    private final AvatarInfo avatarInfo;

    /**
     * The constructor of this class.
     *
     * @param id the identification number of the entity
     * @param sprite the sprite used to render the entity
     * @param frames the total amount of frames
     * @param stillFrame the frame that is displayed as first and last frame and in case the animation is stopped
     * @param defaultColor the color that is used to render this avatar
     * @param shadowOffset the size of the shadow or of light effects in the graphics
     * @param direction the direction the character is facing
     * @param avatarInfo the general avatar information object
     */
    public AvatarTemplate(
            int id,
            @NotNull Sprite sprite,
            int frames,
            int stillFrame,
            @Nullable Color defaultColor,
            int shadowOffset, @NotNull Direction direction,
            @NotNull AvatarInfo avatarInfo) {
        super(id, sprite, frames, stillFrame, defaultColor, shadowOffset);

        this.direction = direction;
        this.avatarInfo = avatarInfo;
        clothes = new AvatarClothManager();
    }

    @NotNull
    public Direction getDirection() {
        return direction;
    }

    @NotNull
    public AvatarClothManager getClothes() {
        return clothes;
    }

    @NotNull
    public AvatarInfo getAvatarInfo() {
        return avatarInfo;
    }
}
