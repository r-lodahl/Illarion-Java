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
import illarion.client.util.ConnectionPerformanceClock;
import illarion.common.net.NetCommWriter;
import org.jetbrains.annotations.NotNull;

/**
 * Keep alive command to send to the server. This is used to notify the server that the client is still working fine.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class KeepAliveCmd extends AbstractCommand {
    /**
     * Default constructor for the keep alive command.
     */
    public KeepAliveCmd() {
        super(CommandList.CMD_KEEPALIVE);
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) {
        ConnectionPerformanceClock.notifyNetCommEncode();
    }

    @NotNull
    @Override
    public String toString() {
        return toString(null);
    }
}
