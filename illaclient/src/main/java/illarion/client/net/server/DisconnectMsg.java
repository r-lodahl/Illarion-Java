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

import illarion.client.IllaClient;
import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.util.Lang;
import illarion.common.net.NetCommReader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Server message: Disconnect by server.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_DISCONNECT)
public final class DisconnectMsg implements ServerReply {
    /**
     * The ID of the logout reason.
     */
    private short reason;

    @Override
    public void decode(@NotNull NetCommReader reader) throws IOException {
        reason = reader.readUByte();
    }

    @NotNull
    @Override
    public ServerReplyResult execute() {
        IllaClient.sendDisconnectEvent(Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("logout") + '\n' + Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("logout.reason") + ' ' +
                getMessageForReason(reason), true);
        return ServerReplyResult.Success;
    }

    @NotNull
    @Contract(pure = true)
    @SuppressWarnings({"SpellCheckingInspection", "OverlyComplexMethod"})
    private static String getMessageForReason(int reason) {
        switch (reason) {
            case 1:
                return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("logout.old_client");
            case 2:
                return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("logout.already_logged_in");
            case 3:
                return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("logout.wrong_pw");
            case 4:
                return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("logout.server_shutdown");
            case 5:
                return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("logout.kicked");
            //6
            case 7:
                return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("logout.no_place");
            case 8:
                return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("logout.not_found");
            //9
            case 10:
                return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("logout.unstable");
            case 11:
                return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("logout.no_account");
            case 12:
                return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("logout.no_skillpack");
            case 13:
                return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("logout.corrupt_inventory");
            default:
                return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("logout.unknown") + ' ' + Integer.toHexString(reason);
        }
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(DisconnectMsg.class, getMessageForReason(reason));
    }
}
