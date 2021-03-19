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
 * This command is used to craft a item from a crafting dialog.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class CraftItemCmd extends AbstractCommand {
    /**
     * The ID of the dialog to interact with.
     */
    private final int dialogId;

    /**
     * The index of the item in the crafting list that is referred to.
     */
    private final int craftingIndex;

    /**
     * The amount of items to be crafted.
     */
    private final int amount;

    /**
     * Default constructor for the trade item command.
     *
     * @param dialogId the dialog ID of the dialog to craft a item from
     * @param craftingIndex the index of the item to craft
     * @param amount the amount of items to create as a batch
     */
    public CraftItemCmd(int dialogId, int craftingIndex, int amount) {
        super(CommandList.CMD_CRAFT_ITEM);

        this.dialogId = dialogId;
        this.craftingIndex = craftingIndex;
        this.amount = amount;
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) {
        writer.writeInt(dialogId);
        writer.writeByte((byte) 1);
        writer.writeUByte((short) craftingIndex);
        writer.writeUByte((short) amount);
    }

    @NotNull
    @Override
    public String toString() {
        return toString("dialog ID: " + dialogId);
    }
}
