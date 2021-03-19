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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import illarion.client.graphics.AnimatedMove;
import illarion.client.graphics.MoveAnimation;
import illarion.client.net.client.MoveCmd;
import illarion.client.net.client.TurnCmd;
import illarion.client.world.CharMovementMode;
import illarion.client.world.MapTile;
import illarion.client.world.Player;
import illarion.client.world.World;
import illarion.client.world.characters.CharacterAttribute;
import illarion.common.graphics.CharAnimations;
import illarion.common.types.CharacterId;
import illarion.common.types.Direction;
import illarion.common.types.ServerCoordinate;
import illarion.common.util.FastMath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.illarion.engine.input.Input;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the main controlling class for the movement. It maintains the references to the different handlers and
 * makes sure that the movement commands of the handlers are put in action.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class Movement {
    @NotNull
    private static final Logger log = LogManager.getLogger();
    @NotNull
    private static final Marker marker = MarkerManager.getMarker("Movement");
    @NotNull
    private static final String THEAD_NAME_HEADER = "Movement Thread-";
    private static final int MAX_WALK_AGI = 20;
    private static final int MIN_WALK_COST = 300;
    private static final int MAX_WALK_COST = 800;
    /**
     * The instance of the player that is moved around by this class.
     */
    @NotNull
    private final Player player;
    @NotNull
    private final ExecutorService executorService;
    @NotNull
    private final MoveAnimator animator;

    @NotNull
    private final MouseMovementHandler followMouseHandler;

    @NotNull
    private final KeyboardMovementHandler keyboardHandler;

    @NotNull
    private final TargetMovementHandler targetMovementHandler;

    @NotNull
    private final MouseTargetMovementHandler targetMouseMovementHandler;

    @NotNull
    private final TargetTurnHandler targetTurnHandler;

    @NotNull
    private final MoveAnimation moveAnimation;
    /**
     * The currently active movement handler.
     */
    @Nullable
    private MovementHandler activeHandler;
    @NotNull
    private CharMovementMode defaultMovementMode;
    private boolean stepInProgress;
    @Nullable
    private MoveCmd lastSendMoveCommand;
    /**
     * This instance of the player location is kept in sync with the location that was last confirmed by the server
     * to keep track of where the player REALLY is.
     */
    @Nullable
    private ServerCoordinate playerLocation;

    public Movement(@NotNull Player player, @NotNull Input input, @NotNull AnimatedMove movementReceiver) {
        this.player = player;
        moveAnimation = new MoveAnimation(movementReceiver);
        defaultMovementMode = CharMovementMode.Walk;
        stepInProgress = false;
        animator = new MoveAnimator(this, moveAnimation);
        moveAnimation.addTarget(animator, false);

        followMouseHandler = new FollowMouseMovementHandler(this, input);
        keyboardHandler = new SimpleKeyboardMovementHandler(this, input);
        targetMovementHandler = new WalkToMovementHandler(this);
        targetMouseMovementHandler = new WalkToMouseMovementHandler(this, input);
        targetTurnHandler = new TurnToMovementHandler(this);

        executorService = Executors.newSingleThreadExecutor(
                new ThreadFactoryBuilder()
                        .setNameFormat(THEAD_NAME_HEADER + "%d")
                        .setDaemon(true)
                        .build());
    }

    @Contract(pure = true)
    private static int getMovementDuration(int tileMovementCost, double mods, boolean diagonal, boolean running) {
        // do not mess with this function. This one has to match the server function exactly to yield the same results
        int movementDuration = FastMath.clamp((int) (tileMovementCost * 100.0 * mods), MIN_WALK_COST, MAX_WALK_COST);

        if (diagonal) {
            movementDuration = (int) (1.4142135623730951 * movementDuration); // sqrt(2)
        }
        if (running) {
            movementDuration = (int) (0.6 * movementDuration);
        }
        return movementDuration;
    }

    /**
     * The location of the player as its known to the server.
     *
     * @return the server location of the player
     */
    @NotNull
    public ServerCoordinate getServerLocation() {
        if (playerLocation == null) {
            throw new IllegalStateException("The location of the player is not known yet.");
        }
        return playerLocation;
    }

    public boolean isMoving() {
        return moveAnimation.isRunning();
    }

    void activate(@NotNull MovementHandler handler) {
        if (!isActive(handler)) {
            MovementHandler oldHandler = activeHandler;
            if (oldHandler != null) {
                oldHandler.disengage(false);
            }
            activeHandler = handler;
            log.debug(marker, "New movement handler is assuming control: {}", activeHandler);
        }
        update();
    }

    void disengage(@NotNull MovementHandler handler) {
        if (isActive(handler)) {
            activeHandler = null;
        } else {
            log.debug(marker, "Tried to disengage a movement handler ({}) that was not active!", handler);
        }
    }

    boolean isActive(@NotNull MovementHandler handler) {
        return (activeHandler != null) && handler.equals(activeHandler);
    }

    public void executeServerRespTurn(@NotNull Direction direction) {
        executorService.submit(() -> executeServerRespTurnInternal(direction));
    }

    private void executeServerRespTurnInternal(@NotNull Direction direction) {
        animator.scheduleTurn(direction);
    }

    private void scheduleEarlyTurn(@NotNull Direction direction) {
        animator.scheduleTurn(direction);
    }

    public void executeServerRespMoveTooEarly() {
        executorService.submit(() -> {
            log.debug(
                    "Response indicates that the request was received too early. A new request is required later.");
            resendMoveToServer();
        });
    }

    public void executeServerRespMove(
            @NotNull CharMovementMode mode, @NotNull ServerCoordinate target, int duration) {
        ServerCoordinate orgLocation = playerLocation;
        if (orgLocation == null) {
            throw new IllegalStateException("The player location is currently unknown.");
        }

        executorService.submit(() -> executeServerRespMoveInternal(orgLocation, mode, target, duration));
        playerLocation = target;
    }

    private void executeServerRespMoveInternal(
            @NotNull ServerCoordinate orgLocation,
            @NotNull CharMovementMode mode,
            @NotNull ServerCoordinate target,
            int duration) {
        log.debug("Received response from the server! Mode: {} Target {} Duration {}ms", mode, target, duration);
        if (orgLocation.equals(target)) {
            log.debug("Current location and target location match. Cancel any pending move.");
            animator.cancelMove(target);
        } else {
            // confirm a move that was started early
            animator.confirmMove(mode, target, duration);
        }
    }

    private void scheduleEarlyMove(@NotNull CharMovementMode mode, @NotNull Direction direction) {
        if (playerLocation == null) {
            throw new IllegalStateException("The current player location is not known yet.");
        }
        int movementDuration = getMovementDuration(playerLocation, mode, direction);
        if (movementDuration != -1) {
            animator.scheduleEarlyMove(mode, getTargetLocation(mode, direction), (movementDuration / 100) * 100);
        }
    }

    @Contract(pure = true)
    public int getMovementDuration(@NotNull ServerCoordinate current,
                                   @NotNull CharMovementMode mode,
                                   @NotNull Direction dir) {
        if (player.getCarryLoad().isWalkingPossible()) {
            ServerCoordinate walkingTarget = new ServerCoordinate(current, dir);
            MapTile walkingTile = World.getMap().getMapAt(walkingTarget);

            if ((walkingTile != null) && !walkingTile.isBlocked()) {
                int agility = Math.min(player.getCharacter().getAttribute(CharacterAttribute.Agility), MAX_WALK_AGI);
                double agilityMod = (10 - agility) / 100.0;
                double loadMod = (player.getCarryLoad().getLoadFactor() / 10.0) * 3.0;
                double mods = agilityMod + loadMod + 1.0;

                int movementDuration = getMovementDuration(walkingTile.getMovementCost(), mods, dir.isDiagonal(),
                        mode == CharMovementMode.Run);

                if (mode == CharMovementMode.Run) {
                    ServerCoordinate runTarget = new ServerCoordinate(walkingTarget, dir);
                    MapTile runningTile = World.getMap().getMapAt(runTarget);
                    if ((runningTile != null) && !runningTile.isBlocked()) {
                        movementDuration += getMovementDuration(runningTile.getMovementCost(), mods,
                                dir.isDiagonal(), true);
                    } else {
                        return -1;
                    }
                }
                return (movementDuration / 100) * 100;
            }
        }
        return -1;
    }

    public void executeServerLocation(@NotNull ServerCoordinate target) {
        World.getUpdateTaskManager().addTask((delta) -> {
            MovementHandler currentHandler = activeHandler;
            if (currentHandler != null) {
                currentHandler.disengage(false);
            }
        });

        stepInProgress = false;
        animator.cancelAll();

        playerLocation = target;
        World.getPlayer().setLocation(target);
    }

    /**
     * Send the movement command to the server.
     *
     * @param direction the direction of the requested move
     * @param mode the mode of the requested move
     */
    private void sendMoveToServer(@NotNull Direction direction, @NotNull CharMovementMode mode) {
        CharacterId playerId = player.getPlayerId();
        if (playerId == null) {
            log.error(marker, "Send move to server while ID is not known.");
            return;
        }
        MoveCmd cmd = new MoveCmd(playerId, mode, direction);
        lastSendMoveCommand = cmd;
        log.debug(marker, "Sending move command to server: {}", cmd);
        World.getNet().sendCommand(cmd);
    }

    private void resendMoveToServer() {
        if (lastSendMoveCommand != null) {
            log.debug(marker, "Re-Sending move command to server: {}", lastSendMoveCommand);
            World.getNet().sendCommand(lastSendMoveCommand);
        } else {
            log.warn(marker, "Tried to resend a move command to the server, but there was no old move command.");
        }
    }

    private void sendTurnToServer(@NotNull Direction direction) {
        if (player.getCharacter().getDirection() != direction) {
            TurnCmd cmd = new TurnCmd(direction);
            log.debug(marker, "Sending turn command to server: {}", cmd);
            World.getNet().sendCommand(cmd);
        }
    }

    /**
     * Notify the handler that everything is ready to request the next step from the server.
     */
    void reportReadyForNextStep() {
        log.debug("Reported ready for the next step.");
        stepInProgress = false;
        update();
    }

    /**
     * This function triggers the lifecycle run of the movement handler. Its executed automatically as the movement
     * handler sees fit. How ever for special events that might require a action of the movement handler this function
     * can be triggered.
     * <p/>
     * Calling it too often causes no harm. The handler checks internally if any actual operations need to be executed
     * or not.
     */
    public void update() {
        if (Thread.currentThread().getName().startsWith(THEAD_NAME_HEADER)) {
            updateImpl();
        } else {
            executorService.submit(this::updateImpl);
        }
    }

    private void updateImpl() {
        if (playerLocation == null) {
            // We are not ready set to do anything. Let's wait.
            log.debug("Received early update on the movement system. Can't do much yet. Standing by.");
            return;
        }
        if (stepInProgress) {
            return;
        }
        MovementHandler handler = activeHandler;
        if (handler != null) {
            long start = System.currentTimeMillis();
            StepData nextStep = handler.getNextStep(playerLocation);
            if (log.isDebugEnabled(marker)) {
                log.debug(marker, "Requesting new step data from handler: {} (took {} milliseconds)", nextStep,
                          System.currentTimeMillis() - start);
            }
            if (nextStep != null) {
                if (nextStep.getDirection() != null) {
                    switch (nextStep.getMovementMode()) {
                        case None:
                            sendTurnToServer(nextStep.getDirection());
                            scheduleEarlyTurn(nextStep.getDirection());
                            break;
                        default:
                            stepInProgress = true;
                            sendMoveToServer(nextStep.getDirection(), nextStep.getMovementMode());
                            scheduleEarlyTurn(nextStep.getDirection());
                            scheduleEarlyMove(nextStep.getMovementMode(), nextStep.getDirection());
                    }
                }

                if (nextStep.getPostStepAction() != null) {
                    nextStep.getPostStepAction().run();
                }
            }
        }
    }

    @NotNull
    @Contract(pure = true)
    private ServerCoordinate getTargetLocation(@NotNull CharMovementMode mode, @NotNull Direction direction) {
        if (playerLocation == null) {
            throw new IllegalStateException("The current player location is not known yet.");
        }
        switch (mode) {
            case Run:
                return new ServerCoordinate(playerLocation,
                        direction.getDirectionVectorX() * 2,
                        direction.getDirectionVectorY() * 2, 0);
            case Walk:
                return new ServerCoordinate(playerLocation, direction);
            case Push:
                return new ServerCoordinate(playerLocation, direction);
            default:
                throw new IllegalArgumentException("Invalid movement mode for selecting the target location");
        }
    }

    @NotNull
    @Contract(pure = true)
    Player getPlayer() {
        return player;
    }

    @Contract(pure = true)
    public boolean isMovementModePossible(@NotNull CharMovementMode mode) {
        return (mode != CharMovementMode.Run) ||
                World.getPlayer().getCharacter().isAnimationAvailable(CharAnimations.RUN);
    }

    @NotNull
    @Contract(pure = true)
    public CharMovementMode getDefaultMovementMode() {
        return defaultMovementMode;
    }

    public void setDefaultMovementMode(@NotNull CharMovementMode defaultMovementMode) {
        this.defaultMovementMode = defaultMovementMode;
    }

    @NotNull
    @Contract(pure = true)
    public MouseMovementHandler getFollowMouseHandler() {
        return followMouseHandler;
    }

    @NotNull
    @Contract(pure = true)
    public KeyboardMovementHandler getKeyboardHandler() {
        return keyboardHandler;
    }

    @NotNull
    @Contract(pure = true)
    public TargetMovementHandler getTargetMovementHandler() {
        return targetMovementHandler;
    }

    @NotNull
    @Contract(pure = true)
    public MouseTargetMovementHandler getTargetMouseMovementHandler() {
        return targetMouseMovementHandler;
    }

    @NotNull
    @Contract(pure = true)
    public TargetTurnHandler getTargetTurnHandler() {
        return targetTurnHandler;
    }

    public void shutdown() {
        activeHandler = null;
        executorService.shutdown();
    }
}
