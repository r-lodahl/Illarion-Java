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
import org.jetbrains.annotations.NotNull;

/**
 * Client Command: Looking at a inventory slot ({@link CommandList#CMD_LOOKAT_INV}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LookatInvCmd extends AbstractCommand {
    /**
     * The inventory slot we are looking at.
     */
    private final short slot;

    /**
     * Default constructor for the look at inventory command.
     *
     * @param slot the inventory slot to look at
     */
    public LookatInvCmd(int slot) {
        super(CommandList.CMD_LOOKAT_INV);

        this.slot = (short) slot;
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) {
        writer.writeUByte(slot);
    }

    /**
     * Get the data of this look at inventory command as string.
     *
     * @return the data of this command as string
     */
    @NotNull
    @Override
    public String toString() {
        return toString("Slot: " + slot);
    }
}
