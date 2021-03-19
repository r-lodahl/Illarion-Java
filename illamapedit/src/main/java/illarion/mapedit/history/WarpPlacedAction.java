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
package illarion.mapedit.history;

import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapWarpPoint;
import org.jetbrains.annotations.Nullable;


/**
 * @author Tim
 */
public class WarpPlacedAction extends HistoryAction {

    private final int x;
    private final int y;
    private final MapWarpPoint oldWP;
    @Nullable
    private final MapWarpPoint newWP;

    public WarpPlacedAction(
            int x, int y, MapWarpPoint oldWP, @Nullable MapWarpPoint newWP, Map map) {
        super(map);
        this.x = x;
        this.y = y;
        this.oldWP = oldWP;
        this.newWP = newWP;
    }

    @Override
    void redo() {
        map.getTileAt(x, y).setMapWarpPoint(newWP);
    }

    @Override
    void undo() {
        map.getTileAt(x, y).setMapWarpPoint(oldWP);
    }
}
