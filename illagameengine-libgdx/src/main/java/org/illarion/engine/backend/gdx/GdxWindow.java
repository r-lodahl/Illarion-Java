package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.illarion.engine.Window;
import org.illarion.engine.graphic.ResolutionManager;

public class GdxWindow implements Window {
    private final ResolutionManager resolutionManager;

    public GdxWindow() {
        resolutionManager = new ResolutionManager();

        Graphics.Monitor monitor = Lwjgl3ApplicationConfiguration.getPrimaryMonitor();
        Graphics.DisplayMode[] displayModes = Lwjgl3ApplicationConfiguration.getDisplayModes(monitor);
        for (Graphics.DisplayMode displayMode : displayModes) {
            resolutionManager.addResolution(
                    displayMode.width,
                    displayMode.height,
                    displayMode.bitsPerPixel,
                    displayMode.refreshRate
            );
        }
    }

    @Override
    public boolean isFullscreen() {
        return Gdx.graphics.isFullscreen();
    }

    @Override
    public ResolutionManager getResolutionManager() {
        return resolutionManager;
    }

    @Override
    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    @Override
    public int getHeight() {
        return Gdx.graphics.getHeight();
    }
}
