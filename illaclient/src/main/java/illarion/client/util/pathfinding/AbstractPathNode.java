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
package illarion.client.util.pathfinding;

import illarion.client.world.CharMovementMode;
import illarion.common.types.ServerCoordinate;
import org.jetbrains.annotations.NotNull;


/**
 * This is the shared implementation of a path node.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
abstract class AbstractPathNode implements PathNode {
    @NotNull
    private final ServerCoordinate location;

    @NotNull
    private final CharMovementMode movementMethod;

    protected AbstractPathNode(@NotNull ServerCoordinate location, @NotNull CharMovementMode movementMethod) {
        this.location = location;
        this.movementMethod = movementMethod;
    }

    /**
     * The location of this node.
     */
    @Override
    @NotNull
    public ServerCoordinate getLocation() {
        return location;
    }

    /**
     * The method of movement.
     */
    @Override
    @NotNull
    public CharMovementMode getMovementMethod() {
        return movementMethod;
    }
}
