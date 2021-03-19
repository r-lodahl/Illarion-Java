/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2017 - Illarion e.V.
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
import illarion.common.types.ServerCoordinate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A movement handler that turns to the target instead of moving.
 *
 * @author Ilya Osadchiy
 */
public class TurnToMovementHandler extends AbstractMovementHandler implements TargetTurnHandler {
    @NotNull
    private static final Logger log = LogManager.getLogger();
    @NotNull
    private static final Marker marker = MarkerManager.getMarker("Movement");

    @Nullable
    private ServerCoordinate targetLocation;

    TurnToMovementHandler(@NotNull Movement movement) {
        super(movement);
    }

    @Override
    public void turnTo(@NotNull ServerCoordinate target) {
        targetLocation = target;
    }

    @Override
    public void disengage(boolean transferAllowed) {
        super.disengage(transferAllowed);
        targetLocation = null;
    }

    @Nullable
    @Override
    public StepData getNextStep(@NotNull ServerCoordinate currentLocation) {
        if (targetLocation == null) {
            return null;
        }
        log.debug(marker, "Performing turn to {}", targetLocation);
        Direction direction = currentLocation.getDirection(targetLocation);
        targetLocation = null;
        return new DefaultStepData(CharMovementMode.None, direction);
    }

    @NotNull
    @Override
    public String toString() {
        return "Turn to target movement handler";
    }

}
