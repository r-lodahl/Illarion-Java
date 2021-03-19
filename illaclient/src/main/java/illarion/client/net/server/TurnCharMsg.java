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
import illarion.client.world.Char;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.CharacterId;
import illarion.common.types.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Server message: Turn a character
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_TURN_CHAR)
public final class TurnCharMsg implements ServerReply {
    @NotNull
    private static final Logger log = LogManager.getLogger();

    /**
     * The ID of the character that is turned.
     */
    @Nullable
    private CharacterId charId;

    /**
     * The new direction of the character.
     */
    @Nullable
    private Direction dir;

    @Override
    public void decode(@NotNull NetCommReader reader) throws IOException {
        dir = Direction.decode(reader);
        charId = new CharacterId(reader);
    }

    @NotNull
    @Override
    public ServerReplyResult execute() {
        if (charId == null) {
            throw new NotDecodedException();
        }

        if (dir == null) {
            log.error("Decoding of the direction from the server failed.");
            return ServerReplyResult.Failed;
        }

        if (World.getPlayer().isPlayer(charId)) { // turn player
            World.getPlayer().getMovementHandler().executeServerRespTurn(dir);
            return ServerReplyResult.Success;
        } else { // turn char
            Char chara = World.getPeople().getCharacter(charId);
            if (chara != null) {
                chara.setDirection(dir);
                return ServerReplyResult.Success;
            }
            // char is outside of the view. Does not matter.
            return ServerReplyResult.Success;
        }
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(TurnCharMsg.class, charId, "To: " + dir);
    }
}
