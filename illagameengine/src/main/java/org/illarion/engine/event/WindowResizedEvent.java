package org.illarion.engine.event;

public class WindowResizedEvent {
    public final int width, height;

    public WindowResizedEvent(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
