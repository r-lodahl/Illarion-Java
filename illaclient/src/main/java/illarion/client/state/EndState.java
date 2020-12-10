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
package illarion.client.state;

import org.illarion.engine.BackendBinding;

/**
 * This state is activated during the shutdown of the game.
 */
public final class EndState implements GameState {
    @Override
    public void create(BackendBinding binding) {
        //Util.loadXML(nifty, "illarion/client/gui/xml/shutdown.xml");
    }

    @Override
    public void dispose() { }

    @Override
    public void update(int delta) { }

    @Override
    public void render() { }

    @Override
    public boolean isClosingGame() {
        return false;
    }

    @Override
    public void enterState() {
        //nifty.gotoScreen("clientEnd");
    }

    @Override
    public void leaveState() { }
}
