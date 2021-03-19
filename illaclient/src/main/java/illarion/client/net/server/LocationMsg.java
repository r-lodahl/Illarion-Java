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
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.ServerCoordinate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Server message: Current player position
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_LOCATION)
public final class LocationMsg implements ServerReply {
    /**
     * The location of the player.
     */
    @Nullable
    private ServerCoordinate location;

    @Override
    public void decode(@NotNull NetCommReader reader) throws IOException {
        location = new ServerCoordinate(reader);
    }

    @NotNull
    @Override
    public ServerReplyResult execute() {
        if (location == null) {
            throw new NotDecodedException();
        }
        World.getPlayer().getMovementHandler().executeServerLocation(location);
        return ServerReplyResult.Success;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(LocationMsg.class, location);
    }
}
