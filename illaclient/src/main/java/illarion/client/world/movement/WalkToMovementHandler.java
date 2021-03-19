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

import illarion.client.util.pathfinding.*;
import illarion.client.world.CharMovementMode;
import illarion.client.world.World;
import illarion.common.types.Direction;
import illarion.common.types.ServerCoordinate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;

import static illarion.client.world.CharMovementMode.Run;
import static illarion.client.world.CharMovementMode.Walk;

/**
 * This movement handler is used to approach a specified location.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class WalkToMovementHandler extends AbstractMovementHandler implements TargetMovementHandler, MoveCostProvider {
    @NotNull
    private static final Logger log = LogManager.getLogger();
    @NotNull
    private static final Marker marker = MarkerManager.getMarker("Movement");

    /**
     * The path finder used to calculate the paths towards the target location.
     */
    @NotNull
    private final PathFindingAlgorithm pathFindingAlgorithm;

    @Nullable
    private ServerCoordinate targetLocation;
    private int targetDistance;

    @Nullable
    private Runnable targetAction;

    @Nullable
    private Path currentPath;

    @NotNull
    private final Collection<Direction> allowedDirections;

    WalkToMovementHandler(@NotNull Movement movement) {
        super(movement);
        allowedDirections = EnumSet.allOf(Direction.class);
        pathFindingAlgorithm = new AStar();
    }

    @Nullable
    @Override
    public StepData getNextStep(@NotNull ServerCoordinate currentLocation) {
        ServerCoordinate target = targetLocation;
        if (target == null) {
            return null;
        }

        int remainingDistance = currentLocation.getStepDistance(target);
        log.debug(marker, "Remaining distance to target: {} Expected distance: {}", remainingDistance, targetDistance);
        if (remainingDistance <= targetDistance) {
            return new DefaultStepData(CharMovementMode.None, finishMove(currentLocation), fetchTargetAction());
        }
        Path activePath;
        if (isCurrentPathValid()) {
            activePath = currentPath;
        } else {
            activePath = calculateNewPath(currentLocation);
            currentPath = activePath;
        }
        if ((activePath == null) || activePath.isEmpty()) {
            return new DefaultStepData(CharMovementMode.None, finishMove(currentLocation), fetchTargetAction());
        }

        PathNode node = activePath.nextStep();
        if (node == null) {
            return new DefaultStepData(CharMovementMode.None, finishMove(currentLocation), fetchTargetAction());
        }
        if (!isPathNodeValid(currentLocation, node)) {
            activePath = calculateNewPath(currentLocation);
            currentPath = activePath;
            if (activePath == null) {
                return new DefaultStepData(CharMovementMode.None, finishMove(currentLocation), fetchTargetAction());
            }
            node = activePath.nextStep();
            if (node == null) {
                return new DefaultStepData(CharMovementMode.None, finishMove(currentLocation), fetchTargetAction());
            }
            if (!isPathNodeValid(currentLocation, node)) {
                Direction lastDirection = currentLocation.getDirection(target);
                targetLocation = null;
                targetAction = null;
                return new DefaultStepData(CharMovementMode.None, lastDirection);
            }
        }
        log.debug(marker, "Performing step to: {}", node.getLocation());
        Direction moveDir = currentLocation.getDirection(node.getLocation());
        if (activePath.isEmpty() && (targetDistance == 0) && !target.equals(node.getLocation())) {
            targetDistance = 1;
        }
        return new DefaultStepData(node.getMovementMethod(), moveDir);
    }

    @Nullable
    private Direction finishMove(@NotNull ServerCoordinate currentLocation) {
        if (targetLocation == null) {
            throw new IllegalStateException("Finishing a move is not possible while there is no target location set.");
        }
        Direction direction = null;
        int remainingDistance = currentLocation.getStepDistance(targetLocation);
        if (remainingDistance > 0) {
            direction = currentLocation.getDirection(targetLocation);
        }
        targetLocation = null;
        return direction;
    }

    @Nullable
    private Runnable fetchTargetAction() {
        Runnable action = targetAction;
        targetAction = null;
        return action;
    }

    private static boolean isPathNodeValid(@NotNull ServerCoordinate currentLocation, @NotNull PathNode node) {
        int distanceToPlayer = currentLocation.getStepDistance(node.getLocation());
        switch (node.getMovementMethod()) {
            case Walk:
                if (distanceToPlayer == 1) {
                    return true;
                }
                break;
            case Run:
                if (!World.getPlayer().getCarryLoad().isRunningPossible()) {
                    return false;
                }
                if (distanceToPlayer == 2) {
                    return true;
                }
                break;
        }
        log.warn(marker, "Next path node {} is out of range: {}", node, distanceToPlayer);
        return false;
    }

    protected int getTargetDistance() {
        return targetDistance;
    }

    protected void increaseTargetDistance() {
        targetDistance++;
    }

    private boolean isCurrentPathValid() {
        Path path = currentPath;
        if (path == null) {
            log.debug(marker, "Path is not valid: Current path is NULL");
            return false;
        }
        ServerCoordinate destination = path.getDestination();
        if (destination == null) {
            log.debug(marker, "Path is not valid: Path destination is NULL");
            return false;
        }
        if (!destination.equals(targetLocation)) {
            log.debug(marker, "Path is not valid: Destination ({}) does not equal the current target location ({}).",
                      destination, targetLocation);
            return false;
        }
        return true;
    }

    @Nullable
    protected Path calculateNewPath(@NotNull ServerCoordinate currentLocation) {
        ServerCoordinate target = getTargetLocation();

        log.info(marker, "Calculating a new path from {} to {}", currentLocation, target);
        PathFindingAlgorithm algorithm = pathFindingAlgorithm;

        switch (getMovementMode()) {
            case Walk:
                return algorithm.findPath(this, currentLocation, target, targetDistance,
                        getAllowedDirections(currentLocation, target), Walk);
            case Run:
                return algorithm.findPath(this, currentLocation, target, targetDistance,
                        getAllowedDirections(currentLocation, target), Walk, Run);
            default:
                return null;
        }
    }

    @NotNull
    protected CharMovementMode getMovementMode() {
        if (!World.getPlayer().getCarryLoad().isRunningPossible()) {
            return Walk;
        }
        CharMovementMode mode = getMovement().getDefaultMovementMode();
        if (getMovement().isMovementModePossible(mode)) {
            return mode;
        }
        return Walk;
    }

    @Override
    public void disengage(boolean transferAllowed) {
        super.disengage(transferAllowed);
        targetLocation = null;
        setTargetReachedAction(null);
    }

    @NotNull
    protected Collection<Direction> getAllowedDirections(@NotNull ServerCoordinate current,
                                                         @NotNull ServerCoordinate target) {
        return Collections.unmodifiableCollection(allowedDirections);
    }

    @Override
    public void walkTo(@NotNull ServerCoordinate target, int distance) {
        setTargetReachedAction(null);
        targetLocation = target;
        targetDistance = distance;
    }

    @Override
    public void setTargetReachedAction(@Nullable Runnable action) {
        if (!Objects.equals(targetAction, action)) {
            targetAction = action;
            if (action == null) {
                log.debug(marker, "Removed target reached action");
            } else {
                log.debug(marker, "Set target reached action");
            }
        }
    }

    @NotNull
    @Override
    public String toString() {
        return "Walk to target movement handler";
    }

    protected boolean isTargetSet() {
        return targetLocation != null;
    }

    @NotNull
    protected ServerCoordinate getTargetLocation() {
        if (targetLocation == null) {
            throw new IllegalStateException("The target location is not set.");
        }
        return targetLocation;
    }

    @Override
    public int getMovementCost(@NotNull ServerCoordinate origin, @NotNull CharMovementMode mode, @NotNull Direction direction) {
        int cost = getMovement().getMovementDuration(origin, mode, direction);

        if ((cost != MoveCostProvider.BLOCKED) &&
                origin.equals(getMovement().getServerLocation()) &&
                (mode == getMovementMode()) &&
                (direction == getPreferredDirection())) {
            cost /= 2;
        }

        return cost;
    }

    @Nullable
    protected Direction getPreferredDirection() {
        if (isTargetSet()) {
            ServerCoordinate currentPos = getMovement().getServerLocation();
            ServerCoordinate targetPos = getTargetLocation();
            return currentPos.getDirection(targetPos);
        }
        return null;
    }
}
