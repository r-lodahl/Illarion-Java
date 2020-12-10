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
package org.illarion.engine;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This is the container that is displaying the game. Offering platform and backend independent functions for
 * the main IllaClient to use, the implementing class in itself should be platform and backend dependent.
 */
@NotThreadSafe
public interface GameContainer {
    /**
     * Calling this function will activate the game, starting the lifecycle of the game,
     * creating the required window and so on.
     *
     * @throws EngineException in case the launch of the application fails
     */
    void startGame(GameStateManager stateManager, Diagnostics diagnostics) throws EngineException;

    /**
     * Set the application icons of this game container. This function has no effect in case the game container does
     * not support icons.
     *
     * @param icons the icons (in different sizes) to load as application icons
     */
    void setIcons(@Nonnull String... icons);

    /**
     * Set the title of the game. This text is displayed in the title bar of the application.
     *
     * @param title the title of the game
     */
    void setTitle(@Nonnull String title);
}
