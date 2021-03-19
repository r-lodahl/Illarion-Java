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
import illarion.mapedit.data.MapTile.MapTileFactory;
import illarion.mapedit.history.GroupAction;
import illarion.mapedit.history.MusicIDChangedAction;
import illarion.mapedit.tools.panel.MusicPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.swing.*;

/**
 * @author Tim
 */
public class MusicTool extends AbstractTool {

    @NotNull
    private final MusicPanel panel;

    public MusicTool() {
        panel = new MusicPanel();
    }

    @Override
    public void clickedAt(int x, int y, @NotNull Map map) {
        MusicIDChangedAction newAction = addMusic(x, y, map);
        if (newAction != null) {
            getHistory().addEntry(newAction);
        }
    }

    @Override
    public void paintSelected(int x, int y, @NotNull Map map, @NotNull GroupAction action) {
        MusicIDChangedAction newAction = addMusic(x, y, map);
        if (newAction != null) {
            action.addAction(newAction);
        }
    }

    @Nullable
    public MusicIDChangedAction addMusic(int x, int y, @NotNull Map map) {
        MapTile tile = map.getTileAt(x, y);
        int musicID = panel.getMusicID();
        if ((tile == null) || (tile.getMusicID() == musicID)) {
            return null;
        }
        MapTile newTile = MapTileFactory.setMusicId(musicID, tile);
        map.setTileAt(x, y, newTile);
        return new MusicIDChangedAction(x, y, tile.getMusicID(), musicID, map);
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("tools.MusicTool");
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
        return panel.isFillArea();
    }

    @Override
    public boolean isFillSelected() {
        return panel.isFillSelected();
    }

    @Override
    public boolean isWarnAnnotated() {
        return true;
    }
}
