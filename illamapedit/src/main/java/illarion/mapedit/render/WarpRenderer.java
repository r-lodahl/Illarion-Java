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

import illarion.mapedit.Lang;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.data.MapWarpPoint;
import illarion.mapedit.util.SwingLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * @author Tim
 */
public class WarpRenderer extends AbstractMapRenderer {
    private static final int XOFFSET = 20;
    private static final int YOFFSET = 10;

    /**
     * Creates a new map renderer
     */
    public WarpRenderer(RendererManager manager) {
        super(manager);
    }

    @Override
    public void renderMap(
            @NotNull Map map,
            @NotNull Rectangle viewport,
            int level,
            @NotNull Graphics2D g) {
        int width = map.getWidth();
        int height = map.getHeight();
        int z = map.getZ() - level;
        AffineTransform transform = g.getTransform();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                MapTile tile = map.getTileAt(x, y);
                if (tile != null) {
                    MapWarpPoint wp = tile.getMapWarpPoint();
                    if (wp == null) {
                        continue;
                    }
                    int xdisp = SwingLocation.displayCoordinateX(x + map.getX(), y + map.getY(), z);
                    int ydisp = SwingLocation.displayCoordinateY(x + map.getX(), y + map.getY(), z);
                    if (viewport.contains((xdisp * getZoom()) + getTranslateX() + (getTileWidth() * getZoom()),
                                          (ydisp * getZoom()) + getTranslateY() + (getTileHeight() * getZoom()))) {

                        g.setColor(Color.RED);
                        g.drawString("Warp", xdisp + (int) (XOFFSET * getZoom()), ydisp + (int) (YOFFSET * getZoom()));
                        g.drawString("X: " + wp.getXTarget(), xdisp + (int) (XOFFSET * getZoom()),
                                     ydisp + (int) ((YOFFSET + 10) * getZoom()));
                        g.drawString("Y: " + wp.getYTarget(), xdisp + (int) (XOFFSET * getZoom()),
                                     ydisp + (int) ((YOFFSET + 20) * getZoom()));
                        g.drawString("Z: " + wp.getZTarget(), xdisp + (int) (XOFFSET * getZoom()),
                                     ydisp + (int) ((YOFFSET + 30) * getZoom()));
                    }
                }
            }
        }
        g.setTransform(transform);
    }

    @Override
    protected int getRenderPriority() {
        return 7;
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("renderer.Warps");
    }

    @Nullable
    @Override
    public ResizableIcon getRendererIcon() {
        return null;
    }

    @Override
    public boolean isDefaultOn() {
        return false;
    }
}
