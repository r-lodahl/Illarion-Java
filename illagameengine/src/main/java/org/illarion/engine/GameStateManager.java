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
package org.illarion.engine;

/**
 * This class needs to be implemented by the game itself. It provides the callbacks required to interact with the
 * lifecycle of the game.
 */
public interface GameStateManager {
    /**
     * This function is called once the game is created.
     */
    void create(BackendBinding binding);

    /**
     * This function is called at the destruction of the game. (Should be used for cleanups before the shutdown itself)
     */
    void dispose();

    /**
     * During the call of this function the application is supposed to perform the update of the game logic.
     *
     * @param delta the time since the last update call
     */
    void update(int delta);

    /**
     * During the call of this function the application is supposed to perform all rendering operations.
     */
    void render();

    /**
     * This function is called in case the game receives a request to be closed.
     *
     * @return {@code true} in case the game is supposed to shutdown, else the closing request is rejected
     */
    boolean isClosingGame();

    void enterState(State state);
}
