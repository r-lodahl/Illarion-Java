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
 * This command is used to inform the server that a crafting dialog was closed.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class CloseDialogCraftingCmd extends AbstractCommand {
    /**
     * The ID of the dialog to close. This ID is send by the server once the dialog is opened.
     */
    private final int dialogId;

    /**
     * Default constructor for the close crafting dialog command.
     *
     * @param dialogId the ID of the dialog to close
     */
    public CloseDialogCraftingCmd(int dialogId) {
        super(CommandList.CMD_CRAFT_ITEM);

        this.dialogId = dialogId;
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) {
        writer.writeInt(dialogId);
        writer.writeByte((byte) 0);
    }

    @NotNull
    @Override
    public String toString() {
        return toString("Dialog ID: " + dialogId);
    }
}
