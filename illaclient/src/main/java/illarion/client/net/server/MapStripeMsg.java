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
package illarion.client.net.server;

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.GameMap;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.Direction;
import illarion.common.types.ServerCoordinate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Server message: Map stripe
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_MAP_STRIPE)
public final class MapStripeMsg implements ServerReply {
    /**
     * Constant if the map stripe goes from top to bottom.
     */
    private static final int DIR_DOWN = 1;

    /**
     * Constant if the map stripe goes from left to right.
     */
    private static final int DIR_RIGHT = 0;

    /**
     * The list of tiles that are inside the update and all containing information.
     */
    @Nullable
    private List<TileUpdate> tiles;

    @Override
    public void decode(@NotNull NetCommReader reader) throws IOException {
        ServerCoordinate loc = new ServerCoordinate(reader);

        int dir = reader.readUByte();
        int count = reader.readUByte();
        tiles = Arrays.asList(new TileUpdate[count]);
        for (int i = 0; i < count; ++i) {
            tiles.set(i, new TileUpdate(loc, reader));
            if (dir == DIR_DOWN) {
                loc = new ServerCoordinate(loc, Direction.SouthWest);
            } else if (dir == DIR_RIGHT) {
                loc = new ServerCoordinate(loc, Direction.SouthEast);
            }
        }
    }

    @NotNull
    @Override
    public ServerReplyResult execute() {
        if (tiles == null) {
            throw new NotDecodedException();
        }

        GameMap map = World.getMap();
        map.updateTiles(tiles);
        if (World.getMapDisplay().isActive()) {
            map.checkInside();
        }
        return ServerReplyResult.Success;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(MapStripeMsg.class);
    }
}
