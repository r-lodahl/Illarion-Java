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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.google.common.eventbus.Subscribe;
import illarion.common.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.*;
import org.illarion.engine.backend.gdx.events.ResolutionChangedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * The game container that is using the libGDX backend to handle the game.
 */
public class ApplicationGameContainer implements GameContainer {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The configuration used to create the application.
     */
    @NotNull
    private final Lwjgl3ApplicationConfiguration applicationConfiguration;

    /**
     * Create a new desktop game that is drawn using libGDX.
     *
     * @param config a ConfigReader containing display values
     */
    public ApplicationGameContainer(ConfigReader config) {
        var isFullscreen = config.getBoolean(Option.fullscreen);

        applicationConfiguration = new Lwjgl3ApplicationConfiguration();

        applicationConfiguration.setWindowListener(new WindowListener());

        applicationConfiguration.useVsync(true);
        applicationConfiguration.setIdleFPS(10);

        applicationConfiguration.setResizable(!isFullscreen);

        if (isFullscreen) {
            applicationConfiguration.setFullscreenMode(loadFullscreenResolution(config));
        } else {
            applicationConfiguration.setWindowedMode(
                    config.getInteger(Option.windowWidth),
                    config.getInteger(Option.windowHeight));
        }
    }

    @Override
    public void startGame(GameStateManager stateManager, Diagnostics diagnostics) {
        EventBus.INSTANCE.register(this);
        // This will not return until the main game loops stops
        new Lwjgl3Application(new ListenerApplication(stateManager, diagnostics), applicationConfiguration);
    }

    @Override
    public void setTitle(@NotNull String title) {
        applicationConfiguration.setTitle(title);
    }

    @Override
    public void setIcons(@NotNull String... icons) {
        applicationConfiguration.setWindowIcon(FileType.Internal, icons);
    }

    @Subscribe
    public void OnResolutionChanged(ResolutionChangedEvent event) {
        var config = event.config;

        if (config.getBoolean(Option.fullscreen)) {
            Gdx.graphics.setResizable(false);
            Gdx.graphics.setFullscreenMode(loadFullscreenResolution(config));
        } else {
            Gdx.graphics.setWindowedMode(config.getInteger(Option.windowWidth), config.getInteger(Option.windowHeight));

            var window = ((Lwjgl3Graphics) Gdx.graphics).getWindow();
            window.setPosition(window.getPositionX(), window.getPositionY() + 64);

            Gdx.graphics.setResizable(true);
        }
    }

    private static Graphics.DisplayMode loadFullscreenResolution(ConfigReader config) {
        var display = config.getString(Option.deviceName);
        var monitor = Arrays.stream(Lwjgl3ApplicationConfiguration.getMonitors())
                .filter(monitorCandidate -> monitorCandidate.name.equals(display))
                .findFirst()
                .orElse(Lwjgl3ApplicationConfiguration.getPrimaryMonitor());

        var width = config.getInteger(Option.fullscreenWidth);
        var height = config.getInteger(Option.fullscreenHeight);
        var bitsPerPoint = config.getInteger(Option.fullscreenBitsPerPoint);
        var refreshRate = config.getInteger(Option.fullscreenRefreshRate);

        return Arrays.stream(Lwjgl3ApplicationConfiguration.getDisplayModes(monitor))
                .filter(mode -> mode.width == width
                        && mode.height == height
                        && mode.refreshRate == refreshRate
                        && mode.bitsPerPixel == bitsPerPoint)
                .findFirst()
                .orElse(Lwjgl3ApplicationConfiguration.getDisplayMode(monitor));
    }
}
