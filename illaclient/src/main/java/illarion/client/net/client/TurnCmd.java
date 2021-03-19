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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.common.net.NetCommWriter;
import illarion.common.types.Direction;
import org.jetbrains.annotations.NotNull;

/**
 * This command is used to inform the server that the character turns towards a specified direction.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class TurnCmd extends AbstractCommand {
    /**
     * The direction the character is supposed to turn to.
     */
    @NotNull
    private final Direction direction;

    /**
     * Default constructor for the turn message.
     *
     * @param direction the direction to turn to
     */
    public TurnCmd(@NotNull Direction direction) {
        super(CommandList.CMD_TURN);

        this.direction = direction;
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) {
        direction.encode(writer);
    }

    @NotNull
    @Override
    public String toString() {
        return toString("Direction: " + direction);
    }
}
