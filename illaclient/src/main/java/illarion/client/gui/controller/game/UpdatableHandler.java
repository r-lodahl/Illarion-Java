/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.client.gui.controller.game;

/**
 * This interface has to be implemented by update handlers that want to receive update calls during the main loop.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public interface UpdatableHandler {
    /**
     * This function is called once during a update loop. It should be used to perform changes at the optics of the
     * game.
     *
     * @param delta the time since the last update
     */
    void update(int delta);
}
