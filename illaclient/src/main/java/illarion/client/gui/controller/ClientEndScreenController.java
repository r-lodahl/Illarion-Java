/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.IllaClient;
import org.jetbrains.annotations.NotNull;


/**
 * The controller for the last screen that is displayed before the client goes down.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ClientEndScreenController implements ScreenController, KeyInputHandler {
    @Override
    public void bind(@NotNull Nifty nifty, @NotNull Screen screen) {
    }

    @Override
    public void onStartScreen() {
        IllaClient.exit();
    }

    @Override
    public void onEndScreen() {
        // nothing
    }

    @Override
    public boolean keyEvent(@NotNull NiftyInputEvent inputEvent) {
        return false;
    }
}
