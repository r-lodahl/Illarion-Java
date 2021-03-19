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
import illarion.common.net.NetCommReader;
import illarion.common.types.ServerCoordinate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Server message: Look at description of a tile
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_LOOKAT_TILE)
public final class LookAtTileMsg implements ServerReply {
    @NotNull
    private static final Logger log = LogManager.getLogger();

    /**
     * The location of the tile on the server map.
     */
    private transient ServerCoordinate loc;

    /**
     * The look at text for the tile.
     */
    private String text;

    @Override
    public void decode(@NotNull NetCommReader reader) throws IOException {
        loc = new ServerCoordinate(reader);
        text = reader.readString();
    }

    @NotNull
    @Override
    public ServerReplyResult execute() {
        log.warn("Received look at for a tile. That shouldn't happen! Received \"{}\" for {}", text, loc);
        return ServerReplyResult.Success;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(LookAtTileMsg.class, loc, text);
    }
}
