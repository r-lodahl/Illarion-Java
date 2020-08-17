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
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.illarion.engine.DesktopGameContainer;
import org.illarion.engine.GameListener;
import org.illarion.engine.MouseCursor;
import org.illarion.engine.graphic.GraphicResolution;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * The game container that is using the libGDX backend to handle the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ApplicationGameContainer implements DesktopGameContainer {
    /**
     * The configuration used to create the application.
     */
    @Nonnull
    private final Lwjgl3ApplicationConfiguration config;
    /**
     * The game listener that receives the updates regarding the game.
     */
    @Nonnull
    private final GameListener gameListener;
    /**
     * The libGDX application that contains the game.
     */
    @Nullable
    private GdxLwjglApplication gdxApplication;
    /**
     * The graphic resolutions that can be applied to the game.
     */
    @Nullable
    private GraphicResolution[] graphicResolutions;

    /**
     * Get the engine that drives the game in this container.
     */
    @Nullable
    private GdxEngine engine;

    /**
     * The count of render calls that were performed during the rendering of the last frame.
     */
    private int lastFrameRenderCalls;

    /**
     * The width of the application in windowed mode.
     */
    private int windowWidth;

    /**
     * The height of the application in windowed mode.
     */
    private int windowHeight;

    private boolean isFullscreen;

    /**
     * The graphic resolution that applied in full screen mode.
     */
    @Nonnull
    private GraphicResolution fullScreenResolution;

    /**
     * Create a new desktop game that is drawn using libGDX.
     *
     * @param gameListener the game listener that receives the updates regarding the game
     * @param width the width of the game container
     * @param height the height of the game container
     * @param fullScreen the full screen flag of the container
     */
    public ApplicationGameContainer(@Nonnull GameListener gameListener, int width, int height, boolean fullScreen) {
        this.gameListener = gameListener;
        config = new Lwjgl3ApplicationConfiguration();

        config.useVsync(true);
        config.setIdleFPS(10);

        isFullscreen = fullScreen;
        config.setResizable(!isFullscreen);

        Graphics.Monitor monitor = Lwjgl3ApplicationConfiguration.getPrimaryMonitor();
        DisplayMode[] displayModes = Lwjgl3ApplicationConfiguration.getDisplayModes(monitor);

        DisplayMode mode = Arrays.stream(displayModes)
                .filter(x -> x.width == width && x.height == height)
                .max(Comparator.comparingInt(x -> x.bitsPerPixel))
                .orElse(Lwjgl3ApplicationConfiguration.getDisplayMode(monitor));

        fullScreenResolution = new GraphicResolution(mode.width, mode.height, mode.bitsPerPixel, mode.refreshRate);

        if (height == 0 && width == 0) {
            windowHeight = mode.height;
            windowWidth = mode.width;
        } else {
            windowHeight = height;
            windowWidth = width;
        }

        if (isFullscreen) {
            config.setFullscreenMode(mode);
        } else {
            config.setWindowedMode(windowWidth, windowHeight);
        }
    }

    @Override
    public int getHeight() {
        if (gdxApplication == null) {
            return isFullScreen() ? fullScreenResolution.getHeight() : windowHeight;
        }
        return gdxApplication.getGraphics().getHeight();
    }

    @Override
    public int getWidth() {
        if (gdxApplication == null) {
            return isFullScreen() ? fullScreenResolution.getWidth() : windowWidth;
        }
        return gdxApplication.getGraphics().getWidth();
    }

    @Nonnull
    @Override
    public GdxEngine getEngine() {
        if (engine == null) {
            throw new IllegalStateException("Game is not launched yet.");
        }
        return engine;
    }

    @Override
    public void setMouseCursor(@Nullable MouseCursor cursor) {
        if (engine == null) {
            return;
        }
        if (cursor instanceof GdxCursor) {
            engine.getGraphics().setCursor((GdxCursor) cursor);
        } else {
            engine.getGraphics().setCursor(null);
        }
    }

    @Override
    public void startGame() {
        // This will not return until the main game loops stops
        new GdxLwjglApplication(new ListenerApplication(gameListener, this), config);
    }

    public void setGdxApplication(@Nonnull GdxLwjglApplication application) {
        gdxApplication = application;
    }

    @Override
    public void exitGame() {
        if (gdxApplication != null) {
            gdxApplication.shutdownGame();
        }
    }

    @Override
    public int getFPS() {
        if (gdxApplication == null) {
            return 0;
        }
        return gdxApplication.getGraphics().getFramesPerSecond();
    }

    @Nonnull
    @Override
    public CharSequence[] getDiagnosticLines() {
        return new CharSequence[]{"Render calls: " + lastFrameRenderCalls};
    }

    @Override
    public void setTitle(@Nonnull String title) {
        config.setTitle(title);
        if (gdxApplication != null) {
            gdxApplication.getGraphics().setTitle(title);
        }
    }

    @Override
    public void setWindowSize(int width, int height) {
        windowWidth = width;
        windowHeight = height;
        if (!isFullScreen() && (gdxApplication != null)) {
            gdxApplication.getGraphics().setWindowedMode(width, height);
        }
    }

    @Override
    public void setFullScreenResolution(@Nonnull GraphicResolution resolution) {
        fullScreenResolution = resolution;

        if (isFullScreen() && (gdxApplication != null)) {
            DisplayMode[] modes = gdxApplication.getGraphics().getDisplayModes();
            for (@Nullable DisplayMode mode : modes) {
                if (mode == null) {
                    continue;
                }
                if ((mode.width != resolution.getWidth()) ||
                        (mode.height != resolution.getHeight()) ||
                        (mode.bitsPerPixel != resolution.getBPP())) {
                    continue;
                }

                if ((resolution.getRefreshRate() == -1) || (resolution.getRefreshRate() == mode.refreshRate)) {
                    gdxApplication.getGraphics().setFullscreenMode(mode);
                    return;
                }
            }
        }
    }

    @Nonnull
    @Override
    public GraphicResolution[] getFullScreenResolutions() {
        if (graphicResolutions != null) return graphicResolutions;

        DisplayMode[] displayModes;
        boolean ignoreRefreshRate;

        if (gdxApplication == null) {
            displayModes = Lwjgl3ApplicationConfiguration.getDisplayModes();
            ignoreRefreshRate = true;
        } else {
            displayModes = gdxApplication.getGraphics().getDisplayModes();
            ignoreRefreshRate = false;
        }

        graphicResolutions = Arrays.stream(displayModes)
                    .filter(x ->
                        x != null &&
                        x.width >= 800 &&
                        x.height >= 600 &&
                        x.bitsPerPixel >= 24 &&
                        (ignoreRefreshRate || x.refreshRate >= 50))
                    .map(x -> new GraphicResolution(x.width, x.height, x.bitsPerPixel, x.refreshRate))
                    .toArray(GraphicResolution[]::new);

        return graphicResolutions;
    }

    @Override
    public boolean isResizeable() {
        return !isFullScreen();
    }

    @Override
    public void setResizeable(boolean resizeable) {
        if (gdxApplication == null) {
            config.setResizable(resizeable);
        }
    }

    @Override
    public boolean isFullScreen() {
        return isFullscreen;
    }

    @Override
    public void setFullScreen(boolean fullScreen) {
        isFullscreen = fullScreen;

        config.setResizable(!fullScreen);

        if (gdxApplication != null) {
            if (fullScreen) {
                setFullScreenResolution(fullScreenResolution);
            } else {
                setWindowSize(windowWidth, windowHeight);
            }
        }
    }

    void createEngine() {
        assert gdxApplication != null;
        engine = new GdxEngine(gdxApplication, this);
    }

    void setLastFrameRenderCalls(int calls) {
        lastFrameRenderCalls = calls;
    }

    @Override
    public void setIcons(@Nonnull String... icons) {
        config.setWindowIcon(FileType.Internal, icons);
    }
}
