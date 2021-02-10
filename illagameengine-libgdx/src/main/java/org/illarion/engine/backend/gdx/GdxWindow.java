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

        var monitors = Lwjgl3ApplicationConfiguration.getMonitors();
        for (var monitor : monitors) {
            var device = new ResolutionManager.Device(monitor.virtualX, monitor.virtualY, monitor.name);
            var displayModes = Lwjgl3ApplicationConfiguration.getDisplayModes(monitor);
            for (var displayMode : displayModes) {
                resolutionManager.addResolution(
                        device,
                        displayMode.width,
                        displayMode.height,
                        displayMode.bitsPerPixel,
                        displayMode.refreshRate
                );
            }
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
