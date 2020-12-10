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

import org.illarion.engine.BackendBinding;

/**
 * This game state is used to display the login and character selection dialog. Also the option dialog is displayed in
 * this state.
 */
public class LoginState implements GameState {
    /**
     * The screen controller that takes care for the login screen.
     */
    //private LoginScreenController loginScreenController;

    @Override
    public void create(BackendBinding binding) {
        //loginScreenController = new LoginScreenController(stateManager, container.getEngine());
        /*nifty.registerScreenController(loginScreenController, new CharScreenController(game),
                                       new CreditsStartScreenController(container.getEngine()));

        Util.loadXML(nifty, "illarion/client/gui/xml/login.xml");
        Util.loadXML(nifty, "illarion/client/gui/xml/charselect.xml");
        Util.loadXML(nifty, "illarion/client/gui/xml/options.xml");
        Util.loadXML(nifty, "illarion/client/gui/xml/credits.xml");*/
    }

    @Override
    public void dispose() { }

    @Override
    public void update(int delta) {
        //if (loginScreenController != null) {
        //    loginScreenController.update();
        //}
    }

    @Override
    public void render() { }

    @Override
    public boolean isClosingGame() {
        return true;
    }

    @Override
    public void enterState() {
        //nifty.gotoScreen("login");
    }

    @Override
    public void leaveState() { }
}
