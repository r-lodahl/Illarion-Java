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
package illarion.client.world.movement;

import illarion.client.world.CharMovementMode;
import illarion.common.types.Direction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class DefaultStepData implements StepData {
    @NotNull
    private final CharMovementMode movementMode;
    @Nullable
    private final Direction direction;
    @Nullable
    private final Runnable postStepAction;

    public DefaultStepData(@NotNull CharMovementMode movementMode,
                           @Nullable Direction direction) {
        this(movementMode, direction, null);
    }

    public DefaultStepData(@NotNull CharMovementMode movementMode,
                           @Nullable Direction direction,
                           @Nullable Runnable postStepAction) {
        this.movementMode = movementMode;
        this.direction = direction;
        this.postStepAction = postStepAction;
    }

    @Override
    @NotNull
    @Contract(pure = true)
    public CharMovementMode getMovementMode() {
        return movementMode;
    }

    @Override
    @Nullable
    @Contract(pure = true)
    public Direction getDirection() {
        return direction;
    }

    @Nullable
    @Override
    @Contract(pure = true)
    public Runnable getPostStepAction() {
        return postStepAction;
    }

    @Override
    @NotNull
    @Contract(pure = true)
    public String toString() {
        return "Mode: " + movementMode.name() + " Direction: " +
                ((direction != null) ? direction.name() : "none");
    }
}
