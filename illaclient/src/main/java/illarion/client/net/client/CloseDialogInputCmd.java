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

import java.io.IOException;

/**
 * Client Command: Send a text that was requested by the server and typed in by the player
 * ({@link CommandList#CMD_CLOSE_DIALOG_INPUT}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CloseDialogInputCmd extends AbstractCommand {
    /**
     * The ID that was send by the server to initiate text input.
     */
    private final int dialogID;

    /**
     * The text that is send to the server.
     */
    @NotNull
    private final String text;

    /**
     * The flag that stores if the input was confirmed or canceled.
     */
    private final boolean success;

    /**
     * Default constructor for the text response command.
     *
     * @param dialogID the ID of the dialog to close
     * @param text the text that contains the response
     * @param success {@code true} in case the dialog was confirmed
     */
    public CloseDialogInputCmd(int dialogID, @NotNull String text, boolean success) {
        super(CommandList.CMD_CLOSE_DIALOG_INPUT);
        this.dialogID = dialogID;
        this.text = text;
        this.success = success;
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) throws IOException {
        writer.writeInt(dialogID);
        if (success) {
            writer.writeUByte((byte) 0xFF);
        } else {
            writer.writeUByte((byte) 0x00);
        }
        writer.writeString(text);
    }

    @NotNull
    @Override
    public String toString() {
        return toString("Dialog ID: " + dialogID + " - Response: " + text + " successful: " + success);
    }
}
