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
package illarion.client.resources.data;

import org.illarion.engine.graphic.Sprite;
import org.jetbrains.annotations.NotNull;

/**
 * This is the template that contains the required data to create the graphical representation of a utility or GUI
 * graphic.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MiscImageTemplate extends AbstractMultiFrameEntityTemplate {
    /**
     * The constructor of this class.
     *
     * @param id the identification number of the entity
     * @param sprite the sprite used to render the entity
     * @param frames the total amount of frames
     */
    public MiscImageTemplate(int id, @NotNull Sprite sprite, int frames) {
        super(id, sprite, frames, 0, null, 0);
    }
}
