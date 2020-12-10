package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.illarion.engine.Window;
import org.illarion.engine.graphic.GraphicResolution;

import java.util.Arrays;

public class GdxWindow implements Window {
    @Override
    public boolean isFullscreen() {
        return Gdx.graphics.isFullscreen();
    }

    @Override
    public GraphicResolution[] getFullscreenResolutions() {
        Graphics.Monitor monitor = Lwjgl3ApplicationConfiguration.getPrimaryMonitor();
        Graphics.DisplayMode[] displayModes = Lwjgl3ApplicationConfiguration.getDisplayModes(monitor);

        return Arrays.stream(displayModes)
                .map(x -> new GraphicResolution(x.width, x.height, x.bitsPerPixel, x.refreshRate))
                .toArray(GraphicResolution[]::new);
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
