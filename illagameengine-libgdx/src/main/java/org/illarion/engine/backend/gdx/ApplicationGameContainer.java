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
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.illarion.engine.Diagnostics;
import org.illarion.engine.GameContainer;
import org.illarion.engine.GameStateManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;

/**
 * The game container that is using the libGDX backend to handle the game.
 */
public class ApplicationGameContainer implements GameContainer {
    /**
     * The configuration used to create the application.
     */
    @NotNull
    private final Lwjgl3ApplicationConfiguration config;

    /**
     * Create a new desktop game that is drawn using libGDX.
     *
     * @param width the width of the game container
     * @param height the height of the game container
     * @param fullScreen the full screen flag of the container
     */
    public ApplicationGameContainer(int width, int height, boolean fullScreen) {
        config = new Lwjgl3ApplicationConfiguration();

        config.setWindowListener(new WindowListener());

        config.useVsync(true);
        config.setIdleFPS(10);

        config.setResizable(!fullScreen);

        Graphics.Monitor monitor = Lwjgl3ApplicationConfiguration.getPrimaryMonitor();
        DisplayMode[] displayModes = Lwjgl3ApplicationConfiguration.getDisplayModes(monitor);

        DisplayMode mode = Arrays.stream(displayModes)
                .filter(x -> x.width == width && x.height == height)
                .max(Comparator.comparingInt(x -> x.bitsPerPixel))
                .orElse(Lwjgl3ApplicationConfiguration.getDisplayMode(monitor));

        int windowHeight, windowWidth;
        if (height == 0 && width == 0) {
            windowHeight = mode.height;
            windowWidth = mode.width;
        } else {
            windowHeight = height;
            windowWidth = width;
        }

        if (fullScreen) {
            config.setFullscreenMode(mode);
        } else {
            config.setWindowedMode(windowWidth, windowHeight);
        }
    }

    @Override
    public void startGame(GameStateManager stateManager, Diagnostics diagnostics) {
        // This will not return until the main game loops stops
        new Lwjgl3Application(new ListenerApplication(stateManager, diagnostics), config);
    }

    @Override
    public void setTitle(@NotNull String title) {
        config.setTitle(title);
    }

    @Override
    public void setIcons(@NotNull String... icons) {
        config.setWindowIcon(FileType.Internal, icons);
    }
}
