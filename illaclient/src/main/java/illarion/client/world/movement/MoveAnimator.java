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

import illarion.client.graphics.AnimatedMove;
import illarion.client.graphics.MoveAnimation;
import illarion.client.util.ConnectionPerformanceClock;
import illarion.client.world.Char;
import illarion.client.world.CharMovementMode;
import illarion.client.world.Player;
import illarion.client.world.World;
import illarion.common.types.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nonnull;
import java.beans.ConstructorProperties;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class takes care for everything regarding the animation of the moves. It triggers and monitors the required
 * animations and reports back the different states of the animation.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class MoveAnimator implements AnimatedMove {
    private static final Logger log = LoggerFactory.getLogger(MoveAnimator.class);
    private static final Marker marker = MarkerFactory.getMarker("Movement");

    private interface MoveAnimatorTask {
        void execute();
    }

    private class MovingTask implements MoveAnimatorTask {
        @Nonnull
        private final CharMovementMode mode;
        @Nonnull
        private final Location target;
        private final int duration;

        private MovingTask(@Nonnull CharMovementMode mode, @Nonnull Location target, int duration) {
            this.mode = mode;
            this.target = target;
            this.duration = duration;
        }

        @Override
        public void execute() {
            executeMove(mode, target, duration);
        }
    }

    private class TurningTask implements MoveAnimatorTask {
        private final int direction;

        private TurningTask(int direction) {
            this.direction = direction;
        }

        @Override
        public void execute() {
            executeTurn(direction);
        }
    }

    @Nonnull
    private final Movement movement;

    @Nonnull
    private final MoveAnimation moveAnimation;

    @Nonnull
    private final Queue<MoveAnimatorTask> taskQueue = new LinkedList<>();

    private boolean animationInProgress;
    private boolean reportingDone;

    @ConstructorProperties({"movement", "moveAnimation"})
    public MoveAnimator(@Nonnull Movement movement, @Nonnull MoveAnimation moveAnimation) {
        this.movement = movement;
        this.moveAnimation = moveAnimation;
    }

    void scheduleMove(@Nonnull CharMovementMode mode, @Nonnull Location target, int duration) {
        taskQueue.offer(new MovingTask(mode, target, duration));
        if (!animationInProgress) {
            executeNext();
        }
    }

    void scheduleTurn(int direction) {
        taskQueue.offer(new TurningTask(direction));
        if (!animationInProgress) {
            executeNext();
        }
    }

    void cancelAll() {
        taskQueue.clear();
        moveAnimation.stop();
    }

    private void executeTurn(int direction) {
        movement.getPlayer().getCharacter().setDirection(direction);
        executeNext();
    }

    private void executeMove(@Nonnull CharMovementMode mode, @Nonnull Location target, int duration) {
        Player parentPlayer = movement.getPlayer();
        Char playerCharacter = parentPlayer.getCharacter();
        if ((mode == CharMovementMode.None) || playerCharacter.getLocation().equals(target)) {
            parentPlayer.updateLocation(target);
            playerCharacter.setLocation(target);
            World.getMapDisplay().animationFinished(true);
            movement.reportReadyForNextStep();
            executeNext();
            return;
        }

        reportingDone = false;
        playerCharacter.moveTo(target, mode, duration);
        int oldElevation = World.getMapDisplay().getElevation();
        int newElevation = World.getMap().getElevationAt(target);
        int xOffset = parentPlayer.getLocation().getDcX() - target.getDcX();
        int yOffset = parentPlayer.getLocation().getDcY() - target.getDcY();
        moveAnimation.start(0, 0, -oldElevation, xOffset, yOffset, -newElevation, duration);

        parentPlayer.updateLocation(target);
    }

    private void executeNext() {
        MoveAnimatorTask task = taskQueue.poll();
        if (task != null) {
            animationInProgress = true;
            task.execute();
        } else {
            animationInProgress = false;
        }
    }

    @Override
    public void setPosition(int posX, int posY, int posZ) {
        if (!reportingDone) {
            long ping = Math.min(60, (long) (ConnectionPerformanceClock.getNetCommPing() * 1.5));
            int remaining = moveAnimation.timeRemaining();
            if (remaining < ping) {
                log.debug(marker, "Requesting next move {}ms before the animation finishes.", remaining);
                reportingDone = true;
                movement.reportReadyForNextStep();
            }
        }
    }

    @Override
    public void animationStarted() {

    }

    @Override
    public void animationFinished(boolean finished) {
        executeNext();
    }
}
