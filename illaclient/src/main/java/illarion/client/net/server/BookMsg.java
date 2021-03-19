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
 * Server message: Show a book
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_BOOK)
public final class BookMsg implements ServerReply {
    /**
     * The book id that was sent.
     */
    private int bookId;

    @Override
    public void decode(@NotNull NetCommReader reader) throws IOException {
        bookId = reader.readUShort();
    }

    @NotNull
    @Override
    public ServerReplyResult execute() {
        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }
        World.getGameGui().getBookGui().showBook(bookId);
        return ServerReplyResult.Success;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(BookMsg.class, bookId);
    }
}
