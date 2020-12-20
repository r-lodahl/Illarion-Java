package org.illarion.engine;

import org.illarion.engine.graphic.ResolutionManager;

public interface Window {
    boolean isFullscreen();
    ResolutionManager getResolutionManager();
    int getWidth();
    int getHeight();
}
