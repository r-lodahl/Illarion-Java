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
package illarion.client.state;

import illarion.client.IllaClient;
import illarion.client.world.World;
import org.illarion.engine.BackendBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

/**
 * This state is activated during the shutdown of the game.
 */
public final class LogoutState implements GameState {
    /**
     * The logger that is used for the logging output of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(LogoutState.class);

    private int logoutDelay;

    @Override
    public void create(BackendBinding binding) {
        log.trace("Creating logout state.");
        //Util.loadXML(nifty, "illarion/client/gui/xml/logout.xml");
    }

    @Override
    public void dispose() {
    }

    @Override
    public void update(int delta) {
        if (logoutDelay == 0) {
            World.shutdownWorld();
        }
        logoutDelay += delta;
        if (logoutDelay > 1000) {
            IllaClient.returnToLogin(null);
        }
    }

    @Override
    public void render() { }

    @Override
    public boolean isClosingGame() {
        return false;
    }

    @Override
    public void enterState() {
        logoutDelay = 0;
        //nifty.gotoScreen("clientLogout");
    }

    @Override
    public void leaveState() { }
}
