package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import org.illarion.engine.EventBus;
import org.illarion.engine.event.GameExitRequestedEvent;

public class WindowListener implements Lwjgl3WindowListener {
    @Override
    public void created(Lwjgl3Window window) { }

    @Override
    public void iconified(boolean isIconified) { }

    @Override
    public void maximized(boolean isMaximized) { }

    @Override
    public void focusLost() { }

    @Override
    public void focusGained() { }

    @Override
    public boolean closeRequested() {
        EventBus.INSTANCE.post(new GameExitRequestedEvent());
        return false;
    }

    @Override
    public void filesDropped(String[] files) { }

    @Override
    public void refreshRequested() { }
}
