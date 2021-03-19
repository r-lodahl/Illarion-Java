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
package illarion.mapedit.events.map;

import illarion.mapedit.util.Vector2i;
import org.jetbrains.annotations.Nullable;


/**
 * @author Tim
 */
public class ZoomEvent {

    private final boolean original;

    private final float value;
    @Nullable
    private final Vector2i pos;

    public ZoomEvent(float value, @Nullable Vector2i pos) {
        this.pos = pos;
        original = false;
        this.value = value;
    }

    public ZoomEvent() {
        pos = null;
        original = true;
        value = 0;
    }

    public float getValue() {
        if (original) {
            throw new IllegalStateException("ZoomEvent has no value if it's 'original'," + " check that first.");
        }
        return value;
    }

    public boolean isOriginal() {
        return original;
    }

    @Nullable
    public Vector2i getPos() {
        return pos;
    }
}
