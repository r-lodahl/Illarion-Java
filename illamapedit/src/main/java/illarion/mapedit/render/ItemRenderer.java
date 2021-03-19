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
import illarion.mapedit.data.MapItem;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.resource.loaders.ItemLoader;
import illarion.mapedit.util.SwingLocation;
import org.jetbrains.annotations.NotNull;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

/**
 * @author Tim
 */
public class ItemRenderer extends AbstractMapRenderer {

    /**
     * Creates a new map renderer
     */
    public ItemRenderer(RendererManager manager) {
        super(manager);
    }

    @Override
    public void renderMap(
            @NotNull Map map,
            @NotNull Rectangle viewport,
            int level,
            @NotNull Graphics2D g) {
        AffineTransform t = g.getTransform();

        //actual H-Position
        int actualH = 0;
        //the start-w-position of the current diagonal iteration
        int iterationStartW = map.getWidth() - 1;
        //actual W-Position
        int actualW = iterationStartW;
        //iterate diagonal until iterations can reach the nearest tile
        while ((iterationStartW > -map.getHeight()) && (actualH < map.getHeight())) {
            render(actualW, actualH, viewport, map, level, g);
            //iterate diagonal
            actualH++;
            actualW++;
            //iteration will end at max W or at max H
            if ((actualW >= map.getWidth()) || (actualH >= map.getHeight())) {
                //start at the next lower W position
                actualW = --iterationStartW;
                if (actualW < 0) {
                    //in case of the lower-left half start at left side and lower
                    actualH = -actualW;
                    actualW = 0;
                } else {
                    //otherwise start at h = 0
                    actualH = 0;
                }
            }
        }
        g.setTransform(t);
    }

    private void render(
            int x,
            int y,
            @NotNull Rectangle viewport,
            @NotNull Map map,
            int level,
            @NotNull Graphics2D g) {
        int z = map.getZ() - level;
        List<MapItem> items = map.getTileAt(x, y).getMapItems();
        if ((items == null) || items.isEmpty()) {
            return;
        }

        int xdisp = SwingLocation.displayCoordinateX(x + map.getX(), y + map.getY(), z);
        int ydisp = SwingLocation.displayCoordinateY(x + map.getX(), y + map.getY(), z);
        if (viewport.contains((xdisp * getZoom()) + getTranslateX() + (getTileWidth() * getZoom()),
                              (ydisp * getZoom()) + getTranslateY() + (getTileHeight() * getZoom()))) {
            int height = 0;
            AffineTransform tr = g.getTransform();
            for (MapItem item : items) {

                ItemImg img = ItemLoader.getInstance().getTileFromId(item.getId());
                if ((img != null) && (img.getImgs() != null)) {
                    Image paintImg = img.getImgs()[0];

                    g.translate(getTileWidth(), getTileHeight());
                    g.translate(xdisp, ydisp);
                    g.translate(0, -height);
                    g.translate(img.getOffsetX(), -img.getOffsetY());
                    g.translate(-paintImg.getWidth(null) / 2, -paintImg.getHeight(null));

                    g.drawImage(img.getImgs()[0], 0, 0, null);
                    g.setTransform(tr);
                    height += img.getHeight();
                }
            }
        }
    }

    @Override
    protected int getRenderPriority() {
        return 6;
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("renderer.Item");
    }

    @Override
    public ResizableIcon getRendererIcon() {
        return ImageLoader.getResizableIcon("file_items");
    }

    @Override
    public boolean isDefaultOn() {
        return true;
    }

    @NotNull
    @Override
    public RibbonElementPriority getPriority() {
        return RibbonElementPriority.TOP;
    }
}
