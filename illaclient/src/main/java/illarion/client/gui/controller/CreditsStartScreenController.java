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
package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.resources.SongFactory;
import illarion.client.util.AudioPlayer;
import org.illarion.engine.Engine;
import org.illarion.engine.sound.Music;
import org.jetbrains.annotations.NotNull;


/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CreditsStartScreenController implements ScreenController, KeyInputHandler {
    private Nifty nifty;

    @NotNull
    private final Engine engine;

    public CreditsStartScreenController(@NotNull Engine engine) {
        this.engine = engine;
    }

    @Override
    public void bind(@NotNull Nifty nifty, @NotNull Screen screen) {
        this.nifty = nifty;
    }

    @Override
    public void onStartScreen() {
        Music creditsMusic = SongFactory.getInstance().getSong(2, engine.getAssets().getSoundsManager());
        if (creditsMusic != null) {
            AudioPlayer audioPlayer = AudioPlayer.getInstance();
            audioPlayer.initAudioPlayer(engine.getSounds());
            if (!audioPlayer.isCurrentMusic(creditsMusic)) {
                audioPlayer.playMusic(creditsMusic);
            }
        }
        nifty.gotoScreen("creditsSingles");
    }

    @Override
    public void onEndScreen() {
        // nothing
    }

    @Override
    public boolean keyEvent(@NotNull NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyStandardInputEvent.Escape) {
            nifty.gotoScreen("login");
            return true;
        }
        return false;
    }
}
