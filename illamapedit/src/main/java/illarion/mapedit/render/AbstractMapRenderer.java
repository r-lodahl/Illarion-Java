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
package illarion.mapedit.render;

import illarion.mapedit.data.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This is the base class off all classes that want to draw on the map.
 *
 * @author Tim
 */
public abstract class AbstractMapRenderer implements Comparable<AbstractMapRenderer> {
    private static final Shape TILE_POLYGON = new Polygon(new int[]{33, 0, 33, 65}, new int[]{0, 17, 33, 17}, 4);

    /**
     * The render manager.
     */
    @NotNull
    private final RendererManager manager;

    /**
     * Creates a new map renderer
     */
    protected AbstractMapRenderer(@NotNull RendererManager manager) {
        this.manager = manager;
    }

    /**
     * @return the map zoom.
     */
    protected float getZoom() {
        return manager.getZoom();
    }

    @NotNull
    protected Shape getTilePolygon() {
        return TILE_POLYGON;
    }

    protected boolean isInViewport(@NotNull Shape viewport, int xDisplay, int yDisplay) {
        float viewX = calculateZoom(xDisplay, getTranslateX(), getTileWidth());
        float viewY = calculateZoom(yDisplay, getTranslateY(), getTileHeight());
        return viewport.contains(viewX, viewY);
    }

    protected float calculateZoom(int display, int translate, float size) {
        return (display * getZoom()) + translate + (size * getZoom());
    }

    /**
     * @return the x translation of the map.
     */
    protected int getTranslateX() {
        return manager.getTranslationX();
    }

    /**
     * the y translation of the map.
     *
     * @return
     */
    protected int getTranslateY() {
        return manager.getTranslationY();
    }

    /**
     * @return The height of a tile.
     */
    protected float getTileHeight() {
        return RendererManager.getTileHeight();
    }

    /**
     * @return The width of a tile.
     */
    protected float getTileWidth() {
        return RendererManager.getTileWidth();
    }

    protected float getMinZoom() {
        return manager.getMinZoom();
    }

    protected RendererManager getManager() {
        return manager;
    }

    /**
     * In this method all rendering should be done.
     *
     * @param g the graphics object.
     */
    public abstract void renderMap(Map map, Rectangle viewport, int level, Graphics2D g);

    /**
     * Returns a value. The renderer with the lowest value will be rendered first.
     *
     * @return the render priority
     */
    protected abstract int getRenderPriority();

    public abstract String getLocalizedName();

    @Nullable
    public abstract ResizableIcon getRendererIcon();

    /**
     * This method is for sorting the renderers in the correct order.
     *
     * @param o
     * @return
     */
    @Override
    public final int compareTo(@NotNull AbstractMapRenderer o) {
        int i = getRenderPriority();
        int j = o.getRenderPriority();
        if (i < j) {
            return -1;
        }
        if (i == j) {
            return 0;
        }
        return 1;
    }

    public abstract boolean isDefaultOn();

    @NotNull
    public RibbonElementPriority getPriority() {
        return RibbonElementPriority.MEDIUM;
    }

    @NotNull
    protected static Image resizeImage(BufferedImage originalImage, Integer width, Integer height) {
        BufferedImage resizeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizeImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizeImage;
    }
}
