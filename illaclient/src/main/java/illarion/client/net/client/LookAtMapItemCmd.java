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
import illarion.common.types.ServerCoordinate;
import org.jetbrains.annotations.NotNull;

/**
 * Client Command: Looking at a item on the map. ({@link CommandList#CMD_LOOK_AT_MAP_ITEM}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LookAtMapItemCmd extends AbstractCommand {
    /**
     * The position on the map we are going to look at.
     */
    @NotNull
    private final ServerCoordinate location;

    /**
     * The position of the item inside the stack.
     */
    private final short stackPosition;

    /**
     * Default constructor for the look at tile command.
     *
     * @param tileLocation the location of the tile to look at
     * @param position the position of the item inside the stack
     */
    public LookAtMapItemCmd(@NotNull ServerCoordinate tileLocation, int position) {
        super(CommandList.CMD_LOOK_AT_MAP_ITEM);
        if (position < 0) {
            throw new IllegalArgumentException("Position has to be 0 or more. Got: " + position);
        }
        location = tileLocation;
        stackPosition = (short) position;
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) {
        location.encode(writer);
        writer.writeUByte(stackPosition);
    }

    @NotNull
    @Override
    public String toString() {
        return toString(location + " Stack position: " + stackPosition);
    }
}
