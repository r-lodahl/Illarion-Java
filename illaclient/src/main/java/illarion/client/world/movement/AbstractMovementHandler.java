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
package illarion.client.world.movement;


import org.jetbrains.annotations.NotNull;

/**
 * The generic shared implementation for the movement handlers.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
abstract class AbstractMovementHandler implements MovementHandler {
    @NotNull
    private final Movement movement;

    protected AbstractMovementHandler(@NotNull Movement movement) {
        this.movement = movement;
    }

    @Override
    public boolean isActive() {
        return movement.isActive(this);
    }

    @Override
    public void assumeControl() {
        movement.activate(this);
    }

    @Override
    public void disengage(boolean transferAllowed) {
        movement.disengage(this);
    }

    @NotNull
    public Movement getMovement() {
        return movement;
    }
}
