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

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author Tim
 */
public class RepaintRequestEvent {
    private final Rectangle dirty;

    public RepaintRequestEvent() {
        this(null);
    }

    public RepaintRequestEvent(Rectangle dirty) {
        this.dirty = dirty;
    }

    public void doRepaint(@NotNull JPanel c) {
        if (dirty != null) {
            c.repaint(dirty);
        } else {
            c.repaint();
        }
    }
}
