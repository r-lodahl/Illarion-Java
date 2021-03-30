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
package illarion.client.graphics;

import illarion.client.IllaClient;
import illarion.client.input.AbstractMouseLocationEvent;
import illarion.client.input.ClickOnMapEvent;
import illarion.client.input.CurrentMouseLocationEvent;
import illarion.client.input.DoubleClickOnMapEvent;
import illarion.client.resources.CharacterFactory;
import illarion.client.resources.data.AvatarTemplate;
import illarion.client.util.Lang;
import illarion.client.world.Char;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.client.world.interactive.InteractiveChar;
import illarion.common.gui.AbstractMultiActionHelper;
import illarion.common.types.ServerCoordinate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.BackendBinding;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.ImmutableColor;
import org.illarion.engine.graphic.SceneEvent;
import org.illarion.engine.input.Button;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Avatar extends AvatarEntity {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The minimal alpha value of a avatar that is needed to show the name tag above the avatar graphic.
     */
    private static final int HIDE_NAME_ALPHA = 127;
    private static final Color COLOR_UNHARMED = new ImmutableColor(0, 255, 0);
    private static final Color COLOR_SLIGHTLY_HARMED = new ImmutableColor(127, 255, 0);
    private static final Color COLOR_HARMED = new ImmutableColor(255, 255, 0);
    private static final Color COLOR_BADLY_HARMED = new ImmutableColor(255, 127, 0);
    private static final Color COLOR_NEAR_DEATH = new ImmutableColor(255, 0, 0);
    private static final Color COLOR_DEAD = new ImmutableColor(173, 173, 173);
    private static final int NAME_COLOR_UNHARMED = 10000;
    private static final int NAME_COLOR_SLIGHTLY_HARMED = 8000;
    private static final int NAME_COLOR_HARMED = 5000;
    private static final int NAME_COLOR_BADLY_HARMED = 2000;

    /**
     * The text tag is the small text box shown above the avatar that contains
     * the name of the avatar.
     */
    @NotNull
    private final AvatarTextTag avatarTextTag;

    /**
     * The character that created this avatar.
     */
    @NotNull
    private final Char parentChar;

    @Nullable
    private final AbstractMultiActionHelper delayedWalkingHandler;

    /**
     * Stores if the name shall be rendered or not. It is checked at every
     * update if this flag is valid or not.
     */
    private boolean isNameRenderingActive;

    private Avatar(@NotNull AvatarTemplate template, @NotNull Char parentChar) {
        super(template, false);

        avatarTextTag = new AvatarTextTag();
        avatarTextTag.setAvatarHeight(template.getSprite().getHeight());

        this.parentChar = parentChar;

        if (parentChar.isHuman() || parentChar.isNPC()) {
            int interval = IllaClient.getConfig().getInteger("doubleClickInterval");
            delayedWalkingHandler = new AbstractMultiActionHelper(interval, 2) {
                @Override
                public void executeAction(int count) {
                    if (count == 1) {
                        walkToCharacter();
                    }
                }
            };
        } else {
            delayedWalkingHandler = null;
        }
    }

    /**
     * Create a avatar from the avatar factory. This either creates a new instance of the avatar class or it takes a
     * existing instance from the list of currently unused instances.
     *
     * @param avatarID the ID of the character that identifies the name and the sex and the direction of the avatar
     * that is needed
     * @return a instance of the needed avatar type
     */
    @Nullable
    public static Avatar create(int avatarID, @NotNull Char parent) {
        try {
            AvatarTemplate template = CharacterFactory.getInstance().getTemplate(avatarID);
            return new Avatar(template, parent);
        } catch (Exception ex) {
            LOGGER.error("Requesting new Avatar with ID {} for {} failed.", avatarID, parent);
        }
        return null;
    }

    /**
     * This function is triggered in case a animation that is not looped finished.
     *
     * @param finished set true in case the animation is really done
     */
    @Override
    public void animationFinished(boolean finished) {
        // Do not reset the character if the parent character is not shown anymore.
        // This may happen in case a currently animated character changes it's avatar.
        if (isShown()) {
            parentChar.resetAnimation(finished);
        }
    }

    @Override
    public int getTargetAlpha() {
        ServerCoordinate target = parentChar.getVisibleLocation();
        if (target == null) {
            return Color.MAX_INT_VALUE;
        }
        MapTile mapTileOfChar = World.getMap().getMapAt(target);
        if (mapTileOfChar == null) {
            return Color.MAX_INT_VALUE;
        } else {
            Tile tileOfChar = mapTileOfChar.getTile();
            return (tileOfChar == null) ? Color.MAX_INT_VALUE : tileOfChar.getTargetAlpha();
        }
    }

    @Override
    public boolean isEventProcessed(BackendBinding binding, int delta, @NotNull SceneEvent event) {
        if (event instanceof ClickOnMapEvent) {
            return isEventProcessed(binding, delta, (ClickOnMapEvent) event);
        }
        if (parentChar.isNPC()) {
            if (event instanceof CurrentMouseLocationEvent) {
                CurrentMouseLocationEvent moveEvent = (CurrentMouseLocationEvent) event;
                if (!isMouseInInteractionRect(moveEvent.getX(), moveEvent.getY())) {
                    return false;
                }

                setHighlight(1);
                InteractiveChar interactiveChar = parentChar.getInteractive();
                if (interactiveChar.isInUseRange()) {
                    setHighlight(2);
                }
                return true;
            }
        }

        if (event instanceof DoubleClickOnMapEvent) {
            if (delayedWalkingHandler != null) {
                delayedWalkingHandler.reset();
            }
            return isEventProcessed(binding, delta, (DoubleClickOnMapEvent) event);
        }

        return super.isEventProcessed(binding, delta, event);
    }

    /**
     * Draw the avatar to the game screen. Calling this function causes the light value to approach the target light
     * in case the light values are different. It also draws the name above the avatar in case it needs to be shown.
     */
    @Override
    public void render(@NotNull Graphics graphics) {
        super.render(graphics);

        if (performRendering() && isNameRenderingActive) {
            avatarTextTag.render(graphics);
        }
    }

    @Override
    public void update(@NotNull BackendBinding binding, int delta) {
        if (!isShown()) {
            return;
        }

        Input input = binding.getInput();

        if (World.getPlayer().getCombatHandler().isAttacking(parentChar)) {
            setAttackMarkerState(AvatarAttackMarkerState.Attacking);
        } else if (World.getPlayer().getCombatHandler().isGoingToAttack(parentChar)) {
            setAttackMarkerState(AvatarAttackMarkerState.AttackStarting);
        } else if (isMouseInInteractionRect(input) && World.getPlayer().getCombatHandler().canBeAttacked(parentChar)) {
            setAttackMarkerState(AvatarAttackMarkerState.AttackPossible);
        } else {
            setAttackMarkerState(AvatarAttackMarkerState.Hidden);
        }

        super.update(binding, delta);

        if (World.getPlayer().isPlayer(parentChar.getCharId())) {
            isNameRenderingActive = false;
        } else if (getAlpha() > HIDE_NAME_ALPHA) {
            isNameRenderingActive = World.getPeople().isAvatarTagShown(parentChar.getCharId())
                    || input.isKeyDown(Key.RightAlt)
                    || isMouseInInteractionRect(input);
        }

        if (isNameRenderingActive) {
            avatarTextTag.setDisplayLocation(getDisplayCoordinate());
            avatarTextTag.update(delta);
        }
    }

    /**
     * This function handles click events on the avatars.
     *
     * @param delta the time since the last update
     * @param event the event that actually happened
     * @return {@code true} in case the event was handled
     */
    @SuppressWarnings("UnusedParameters")
    private boolean isEventProcessed(BackendBinding binding, int delta, @NotNull ClickOnMapEvent event) {
        if (event.getKey() != Button.Left
                || World.getPlayer().isPlayer(parentChar.getCharId())
                || !isMouseInInteractiveOrOnTag(event)) {
            return false;
        }

        if (event.getKey() == Button.Right) {
            World.getPlayer().getCombatHandler().toggleAttackOnCharacter(parentChar);
            return true;
        }

        if (event.getKey() == Button.Left) {
            var input = binding.getInput();

            if (input.isAnyKeyDown(Key.LeftAlt, Key.RightAlt)) {
                var target = parentChar.getLocation();
                LOGGER.debug("Single alt-click on character {} at {}", parentChar, target);

                if (target != null) {
                    var turnHandler = World.getPlayer().getMovementHandler().getTargetTurnHandler();
                    turnHandler.turnTo(target);
                    turnHandler.assumeControl();
                }

                return true;
            }

            if (delayedWalkingHandler != null) {
                delayedWalkingHandler.pulse();
            } else {
                walkToCharacter();
            }
            return true;
        }

        return false;
    }

    private void walkToCharacter() {
        ServerCoordinate target = parentChar.getLocation();
        if (target != null) {
            LOGGER.debug("Walking to the character {}", parentChar);
            var movementHandler = World.getPlayer().getMovementHandler().getTargetMovementHandler();
            movementHandler.walkTo(target, 1);
            movementHandler.assumeControl();
        } else {
            LOGGER.warn("Failed to walk to the character {} because it does not have a location.", parentChar);
        }
    }

    /**
     * This function handles double click events on the avatars.
     *
     * @param delta the time since the last update
     * @param event the event that actually happened
     * @return {@code true} in case the event was handled
     */
    @SuppressWarnings("UnusedParameters")
    private boolean isEventProcessed(BackendBinding binding, int delta, @NotNull DoubleClickOnMapEvent event) {
        if (event.getKey() != Button.Left
                || World.getPlayer().isPlayer(parentChar.getCharId())
                || !isMouseInInteractionRect(event.getX(), event.getY())) {
            return false;
        }

        if (parentChar.isHuman()) {
            World.getUpdateTaskManager().addTaskForLater(
                    d -> World.getGameGui().getDialogInputGui().showNamingDialog(parentChar));
            return true;
        }

        var interactiveChar = parentChar.getInteractive();

        if (interactiveChar.isInUseRange()) {
            LOGGER.debug("Using the character {}", interactiveChar);
            interactiveChar.use();
            return true;
        }

        var target = parentChar.getLocation();
        if (target != null) {
            if (binding.getInput().isAnyKeyDown(Key.LeftAlt, Key.RightAlt)) {
                LOGGER.debug("Double alt-click to turn to character {} at {}", parentChar, target);
                var turnHandler = World.getPlayer().getMovementHandler().getTargetTurnHandler();
                turnHandler.turnTo(target);
                turnHandler.assumeControl();
            } else {
                LOGGER.debug("Walking to and using the character {} at {}", interactiveChar, target);
                var movementHandler = World.getPlayer().getMovementHandler().getTargetMovementHandler();
                movementHandler.walkTo(target, 1);
                movementHandler.setTargetReachedAction(interactiveChar::use);
                movementHandler.assumeControl();
            }
        } else {
            LOGGER.debug("Walking to and using the character {} doesn't work, because it has no location.",
                    interactiveChar);
        }

        return true;
    }

    /**
     * Check if a mouse event points at the interactive area of a avatar or on its tag.
     *
     * @param event the mouse event
     * @return {@code true} in case the mouse is on the interactive area of the avatar or on its tag
     */
    private boolean isMouseInInteractiveOrOnTag(@NotNull AbstractMouseLocationEvent event) {
        int mouseXonDisplay = event.getX() + Camera.getInstance().getViewportOffsetX();
        int mouseYonDisplay = event.getY() + Camera.getInstance().getViewportOffsetY();
        if (isNameRenderingActive && avatarTextTag.getDisplayRect().isInside(mouseXonDisplay, mouseYonDisplay)) {
            return true;
        }

        return isMouseInInteractionRect(event.getX(), event.getY());
    }

    /**
     * Set the name that is displayed in the tag above the avatar graphic.
     *
     * @param charName the name that is displayed above the character graphic
     */
    public void setName(@NotNull String charName) {
        if (charName.isEmpty()) {
            avatarTextTag.setCharacterName("unknown");
        } else {
            avatarTextTag.setCharacterName(charName);
        }
        avatarTextTag.setCharNameColor(Color.YELLOW);
    }

    /**
     * Set the color of the text that is shown above the avatar that is shown.
     *
     * @param color the color that is used for the font of the the text that is
     * shown above the character and shows the name of the character
     */
    public void setNameColor(@NotNull  Color color) {
        avatarTextTag.setCharNameColor(color);
    }

    public void setHealthPoints(int value) {
        //noinspection IfStatementWithTooManyBranches
        if (value == NAME_COLOR_UNHARMED) {
            avatarTextTag.setHealthState(Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("char.health.unharmed"));
            avatarTextTag.setHealthStateColor(COLOR_UNHARMED);
        } else if (value > NAME_COLOR_SLIGHTLY_HARMED) {
            avatarTextTag.setHealthState(Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("char.health.slightlyHarmed"));
            avatarTextTag.setHealthStateColor(COLOR_SLIGHTLY_HARMED);
        } else if (value > NAME_COLOR_HARMED) {
            avatarTextTag.setHealthState(Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("char.health.harmed"));
            avatarTextTag.setHealthStateColor(COLOR_HARMED);
        } else if (value > NAME_COLOR_BADLY_HARMED) {
            avatarTextTag.setHealthState(Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("char.health.badlyHarmed"));
            avatarTextTag.setHealthStateColor(COLOR_BADLY_HARMED);
        } else if (value > 0) {
            avatarTextTag.setHealthState(Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("char.health.nearDead"));
            avatarTextTag.setHealthStateColor(COLOR_NEAR_DEATH);
        } else {
            avatarTextTag.setHealthState(Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("char.health.dead"));
            avatarTextTag.setHealthStateColor(COLOR_DEAD);
        }
    }

    @Override
    @NotNull
    public String toString() {
        return "Avatar of " + parentChar;
    }
}
