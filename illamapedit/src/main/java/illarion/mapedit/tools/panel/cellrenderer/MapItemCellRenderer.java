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
package illarion.mapedit.tools.panel.cellrenderer;

import illarion.mapedit.data.MapItem;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.resource.loaders.ItemLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author Fredrik K
 */
public class MapItemCellRenderer extends JPanel implements ListCellRenderer<MapItem> {
    private static final Color COLOR_SELECTED = new Color(-6100481);
    private static final Color COLOR_UNSELECTED = new Color(-1246977);

    @NotNull
    private final JLabel itemId;
    @NotNull
    private final JLabel name;

    public MapItemCellRenderer() {
        itemId = new JLabel();
        name = new JLabel();

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(itemId);
        add(name);
    }

    private static void adjustColors(@Nullable Color bg, @NotNull Component... components) {
        for (Component component : components) {
            if (bg != null) {
                component.setBackground(bg);
            }
        }
    }

    @NotNull
    @Override
    public Component getListCellRendererComponent(
            JList<? extends MapItem> list,
            MapItem value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        if (isSelected) {
            adjustColors(COLOR_SELECTED, this, itemId, name);
        } else {
            adjustColors(COLOR_UNSELECTED, this, itemId, name);
        }
        String idText = String.valueOf(value.getId());
        itemId.setText(idText + String.format("%" + (10 - idText.length()) + 's', ""));
        ItemImg itm = ItemLoader.getInstance().getTileFromId(value.getId());
        if (itm != null) {
            name.setText(itm.getResourceName());
        }
        return this;
    }
}
