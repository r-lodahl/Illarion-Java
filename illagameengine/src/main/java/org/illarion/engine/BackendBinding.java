package org.illarion.engine;

import org.illarion.engine.assets.Assets;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.input.Input;
import org.illarion.engine.sound.Sounds;
import org.illarion.engine.ui.UserInterface;

import java.nio.file.Files;

public interface BackendBinding {
    Assets getAssets();
    Input getInput();
    Sounds getSounds();
    Graphics getGraphics();
    Window getWindow();
    UserInterface getGui();
}
