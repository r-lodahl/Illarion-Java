package org.illarion.engine.backend.gdx;

import org.illarion.engine.BackendBinding;
import org.illarion.engine.Window;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.input.Input;
import org.illarion.engine.sound.Sounds;
import org.illarion.engine.ui.UserInterface;

import java.nio.file.Files;

public class GdxBinding implements BackendBinding {
    private final Graphics graphics;
    private final Sounds sounds;
    private final Input input;
    private final Assets assets;
    private final Window window;
    private final UserInterface gui;

    public GdxBinding(Graphics graphics, Sounds sounds, Input input, Assets assets, Window window, UserInterface gui) {
        this.graphics = graphics;
        this.sounds = sounds;
        this.input = input;
        this.assets = assets;
        this.window = window;
        this.gui = gui;
    }

    @Override
    public Assets getAssets() {
        return assets;
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public Sounds getSounds() {
        return sounds;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public Window getWindow() {
        return window;
    }

    @Override
    public UserInterface getGui() {
        return gui;
    }
}
