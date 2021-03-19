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

import illarion.client.input.CurrentMouseLocationEvent;
import illarion.client.world.World;
import illarion.client.world.characters.CharacterAttribute;
import illarion.common.memory.MemoryPools;
import illarion.common.types.DisplayCoordinate;
import org.illarion.engine.BackendBinding;
import org.illarion.engine.EngineException;
import org.illarion.engine.Window;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.assets.EffectManager;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.Scene;
import org.illarion.engine.graphic.effects.FogEffect;
import org.illarion.engine.graphic.effects.GrayScaleEffect;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * The map display manager stores and manages all objects displayed on the map. It takes care for rendering the objects
 * in the proper order, for animations of the entire map and it manages the current location of the avatar.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class MapDisplayManager implements AnimatedMove {
    /**
     * Offset of the tiles due the perspective of the map view.
     */
    public static final int TILE_PERSPECTIVE_OFFSET = 3;
    @NotNull
    private final FadingCorridor corridor;
    /**
     * The scene the game is displayed in.
     */
    @NotNull
    private final Scene gameScene;
    private boolean active;
    @Nullable
    private DisplayCoordinate origin;
    /**
     * This flag stores if the fog effect was already applied to the scene.
     */
    private boolean fogEnabled;
    /**
     * This flag stores if the gray scale filter that is applied in case the character is dead was already enabled.
     */
    private boolean deadViewEnabled;

    /**
     * The backend's window reference.
     */
    @NotNull
    private final Window window;

    public MapDisplayManager(@NotNull Assets assets, @NotNull Window window) {
        this.window = window;

        active = false;

        corridor = FadingCorridor.getInstance();

        gameScene = assets.createNewScene();
    }

    private int getMapCenterX() {
        return window.getWidth() >> 1;
    }

    private int getMapCenterY() {
        return window.getHeight() >> 1;
    }

    /**
     * Get the game scene that is managed by this display manager.
     *
     * @return the game scene
     */
    @NotNull
    @Contract(pure = true)
    public Scene getGameScene() {
        return gameScene;
    }

    public int getWorldX(int x) {
        if (origin == null) {
            throw new IllegalStateException("Origin of the map display is not set.");
        }
        return (x - getMapCenterX()) + origin.getX();
    }

    public int getWorldY(int y) {
        if (origin == null) {
            throw new IllegalStateException("Origin of the map display is not set.");
        }
        return (y - getMapCenterY()) + origin.getY();
    }

    public boolean isActive() {
        return active && (origin != null);
    }

    /**
     * Set the map display as active.
     *
     * @param active the active flag
     */
    public void setActive(boolean active) {
        if (!active) {
            origin = null;
        }
        this.active = active;
    }

    /**
     * Update the display entries.
     *
     * @param delta the time in milliseconds since the last update
     */
    public void update(BackendBinding binding, int delta) {
        if (!isActive()) {
            return;
        }
        assert origin != null;

        Window window = binding.getWindow();

        int centerX = window.getWidth() >> 1;
        int centerY = window.getHeight() >> 1;

        int offX = centerX - origin.getX();
        int offY = centerY - origin.getY();

        Avatar av = World.getPlayer().getCharacter().getAvatar();
        if (av != null) {
            corridor.setCorridor(av);
        }

        Camera.getInstance().setViewport(-offX, -offY, window.getWidth(), window.getHeight());

        CurrentMouseLocationEvent event = MemoryPools.get(CurrentMouseLocationEvent.class);
        event.set(binding.getInput().getMouseX(), binding.getInput().getMouseY());
        gameScene.publishEvent(event);
        gameScene.update(binding, delta);
        updateFog(binding.getAssets().getEffectManager());
        updateDeadView(binding.getAssets().getEffectManager());
    }

    /**
     * Update the graphical effects applied in case the character died.
     */
    private void updateDeadView(EffectManager effectManager) {
        int hitPoints = World.getPlayer().getCharacter().getAttribute(CharacterAttribute.HitPoints);
        if (hitPoints == 0) {
            if (!deadViewEnabled) {
                try {
                    GrayScaleEffect effect = effectManager.getGrayScaleEffect(true);
                    gameScene.addEffect(effect);
                    deadViewEnabled = true;
                } catch (EngineException e) {
                    // error activating gray scale
                }
            }
        } else {
            if (deadViewEnabled) {
                try {
                    GrayScaleEffect effect = effectManager.getGrayScaleEffect(true);
                    gameScene.removeEffect(effect);
                    deadViewEnabled = false;
                } catch (EngineException e) {
                    // error activating gray scale
                }
            }
        }
    }

    /**
     * Update the graphical effect that shows the fog on the map.
     */
    private void updateFog(EffectManager effectManager) {
        float fog = World.getWeather().getFog();
        if (fog > 0.f) {
            try {
                FogEffect effect = effectManager.getFogEffect(true);
                effect.setDensity(fog);
                if (!fogEnabled) {
                    gameScene.addEffect(effect);
                    fogEnabled = true;
                }
            } catch (EngineException e) {
                // error activating fog
            }
        } else if (fogEnabled) {
            try {
                FogEffect effect = effectManager.getFogEffect(true);
                gameScene.removeEffect(effect);
                fogEnabled = false;
            } catch (EngineException e) {
                // error activating fog
            }
        }
    }

    /**
     * Render all visible map items
     */
    public void render(Graphics graphics) {
        if (!isActive()) {
            return;
        }

        Camera camera = Camera.getInstance();
        gameScene.render(graphics, camera.getViewportOffsetX(), camera.getViewportOffsetY());
    }

    /**
     * Move the map origin to a new location
     *
     * @param location the location on the map the view is focused on
     */
    public void setLocation(@NotNull DisplayCoordinate location) {
        origin = location;
    }

    @Override
    public void animationStarted() {
    }

    /**
     * Map movement is complete
     */
    @Override
    public void animationFinished(boolean finished) {
    }

    /**
     * Animation implementation. Does the same as {@link #setLocation}
     */
    @Override
    public void setPosition(@NotNull DisplayCoordinate position) {
        setLocation(position);
    }
}
