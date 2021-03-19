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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Server message: Attack message received in case the player is attacking someone.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_ATTACK)
public final class AttackMsg implements ServerReply {
    @Override
    public void decode(@NotNull NetCommReader reader) throws IOException {
        // nothing to decode
    }

    @NotNull
    @Override
    public ServerReplyResult execute() {
        World.getPlayer().getCombatHandler().confirmAttack();
        return ServerReplyResult.Success;
    }

    /**
     * Get the data of this simple message as string.
     *
     * @return the string that contains the values that were decoded for this  message
     */
    @NotNull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(AttackMsg.class);
    }
}
