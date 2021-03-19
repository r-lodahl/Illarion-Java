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

import illarion.client.util.UpdateTask;
import illarion.client.world.CharMovementMode;
import illarion.client.world.World;
import illarion.common.types.Direction;
import illarion.common.types.ServerCoordinate;
import illarion.common.util.FastMath;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This keyboard movement handler simply handles the keyboard input to perform the walking operations.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SimpleKeyboardMovementHandler extends AbstractMovementHandler implements KeyboardMovementHandler {
    @NotNull
    private final Input input;

    @NotNull
    private final Set<Direction> activeDirections;

    private boolean delayedMove;

    @NotNull
    private final ScheduledExecutorService delayedMoveExecutor;

    @Nullable
    private ScheduledFuture<Void> delayedMoveTask;

    @NotNull
    private final UpdateTask updateMovementTask = (delta) -> getMovement().update();

    SimpleKeyboardMovementHandler(@NotNull Movement movement, @NotNull Input input) {
        super(movement);
        activeDirections = EnumSet.noneOf(Direction.class);
        this.input = input;
        delayedMoveExecutor = new ScheduledThreadPoolExecutor(0);
    }

    @Override
    public void startMovingTowards(@NotNull Direction direction) {
        if (activeDirections.add(direction)) {
            if (delayedMoveTask != null) {
                delayedMoveTask.cancel(false);
                delayedMoveTask = null;
            }
            if (!getMovement().isMoving()) {
                delayedMoveTask = delayedMoveExecutor.schedule(() -> {
                    delayedMove = false;
                    World.getUpdateTaskManager().addTask(updateMovementTask);
                    return null;
                }, 100, TimeUnit.MILLISECONDS);
                delayedMove = true;
            }
        }
    }

    @Override
    public void stopMovingTowards(@NotNull Direction direction) {
        activeDirections.remove(direction);
    }

    @Nullable
    @Override
    public StepData getNextStep(@NotNull ServerCoordinate currentLocation) {
        return new DefaultStepData(getMovementMode(), getMovementDirection());
    }

    @Nullable
    private Direction getMovementDirection() {
        if (delayedMove) {
            return null;
        }

        if (activeDirections.isEmpty() || (activeDirections.size() > 2)) {
            return null;
        }

        return getCombined(activeDirections);
    }

    @Nullable
    public static Direction getCombined(@NotNull Iterable<Direction> activeDirections) {
        int xVec = 0;
        int yVec = 0;
        for (Direction activeDir : activeDirections) {
            xVec += activeDir.getDirectionVectorX();
            yVec += activeDir.getDirectionVectorY();
        }
        xVec = FastMath.sign(xVec);
        yVec = FastMath.sign(yVec);

        //noinspection ConstantConditions
        for (Direction dir : Direction.values()) {
            if ((dir.getDirectionVectorX() == xVec) && (dir.getDirectionVectorY() == yVec)) {
                return dir;
            }
        }
        return null;
    }

    @NotNull
    private CharMovementMode getMovementMode() {
        if (input.isKeyDown(Key.LeftAlt)) {
            return CharMovementMode.None;
        }

        if (!World.getPlayer().getCarryLoad().isRunningPossible()) {
            return CharMovementMode.Walk;
        }

        CharMovementMode mode = getMovement().getDefaultMovementMode();
        if (getMovement().isMovementModePossible(mode)) {
            return mode;
        }
        return CharMovementMode.Walk;
    }

    @NotNull
    @Override
    public String toString() {
        return "Simple keyboard movement handler";
    }
}
