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
package illarion.mapedit.tools;

import illarion.mapedit.Lang;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.events.TileAnnotationEvent;
import illarion.mapedit.events.ToolSelectedEvent;
import illarion.mapedit.history.GroupAction;
import illarion.mapedit.tools.panel.DataPanel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.swing.*;

/**
 * @author Fredrik K
 */
public class DataTool extends AbstractTool {
    @NotNull
    protected DataPanel panel;

    public DataTool() {
        AnnotationProcessor.process(this);

        panel = new DataPanel();
    }

    @Override
    public void clickedAt(int x, int y, @NotNull Map map) {
        MapTile tile = map.getTileAt(x, y);

        if (tile != null) {
            panel.setVisible(true);
            panel.setItems(tile.getMapItems(), tile.getAnnotation());
            map.setActiveTile(x, y);
        }
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("tools.DataTool");
    }

    @Nullable
    @Override
    public ResizableIcon getToolIcon() {
        return null;
    }

    @NotNull
    @Override
    public JPanel getSettingsPanel() {
        return panel;
    }

    @Override
    public boolean isFillAreaAction() {
        return false;
    }

    @Override
    public boolean isFillSelected() {
        return false;
    }

    @Override
    public boolean isWarnAnnotated() {
        return false;
    }

    @Override
    public void paintSelected(int x, int y, Map map, GroupAction action) {
    }

    @EventSubscriber
    public void onItemDataAnnotation(@NotNull TileAnnotationEvent e) {
        panel.setAnnotation(e.getText());
    }

    @EventSubscriber
    public void onSelectTool(@NotNull ToolSelectedEvent e) {
        if (equals(e.getTool())) {
            MapTile tile = getManager().getActiveTile();
            if (tile == null) {
                panel.setVisible(false);
            } else {
                panel.setItems(tile.getMapItems(), tile.getAnnotation());
                panel.setVisible(true);
            }
        }
    }
}
