package org.illarion.engine;

import org.illarion.engine.graphic.GraphicResolution;

public interface Window {
    boolean isFullscreen();
    GraphicResolution[] getFullscreenResolutions();
    int getWidth();
    int getHeight();
}
