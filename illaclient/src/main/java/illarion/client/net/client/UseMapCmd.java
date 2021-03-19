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
 * This command is used to tell the server that the player is using a object on the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class UseMapCmd extends AbstractCommand {
    /**
     * The map location that is used.
     */
    @NotNull
    private final ServerCoordinate usedLocation;

    /**
     * Default constructor for the use command.
     *
     * @param location the location that is used
     */
    public UseMapCmd(@NotNull ServerCoordinate location) {
        super(CommandList.CMD_USE);

        usedLocation = location;
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) {
        writer.writeUByte((short) 1); // MAP REFERENCE
        usedLocation.encode(writer);
    }

    @NotNull
    @Override
    public String toString() {
        return toString("Location: " + usedLocation);
    }
}
