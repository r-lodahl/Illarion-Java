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
package illarion.mapedit.resource;

import illarion.common.graphics.ItemInfo;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author Tim
 */
public class ItemImg {

    private final ItemInfo info;
    private final int itemId;
    private final String resourceName;
    private final int offsetX;
    private final int offsetY;
    private final int frameCount;
    private final int animationSpeed;
    private final int itemMode;
    @NotNull
    private final Image[] imgs;
    private final String itemName;
    private final int editorGroup;

    public int getItemId() {
        return itemId;
    }

    public boolean isObstacle() {
        return !info.isObstacle();
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getName() {
        if (itemName == null) {
            return getResourceName();
        }
        return itemName;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getFrameCount() {
        return frameCount;
    }

    /**
     * Get the surface level of the item. So the offset how much a item that lies on this item has to move up to
     * appear to lie on this item.
     *
     * @return the amount of pixels the next item offset has to move up
     */
    public int getHeight() {
        return info.getLevel();
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    public int getItemMode() {
        return itemMode;
    }

    public ItemImg(
            int itemId,
            String resourceName,
            String itemName,
            int editorGroup,
            int offsetX,
            int offsetY,
            int frameCount,
            int animationSpeed,
            int itemMode,
            @NotNull Image[] imgs,
            ItemInfo info) {

        this.itemId = itemId;
        this.resourceName = resourceName;
        this.itemName = itemName;
        this.editorGroup = editorGroup;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.frameCount = frameCount;
        this.animationSpeed = animationSpeed;
        this.itemMode = itemMode;

        this.info = info;
        this.imgs = new Image[imgs.length];

        System.arraycopy(imgs, 0, this.imgs, 0, imgs.length);
    }

    @NotNull
    public Image[] getImgs() {
        return imgs;
    }

    public int getEditorGroup() {
        return editorGroup;
    }
}
