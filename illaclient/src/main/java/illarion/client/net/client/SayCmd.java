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
import illarion.client.util.ChatHandler.SpeechMode;
import illarion.common.net.NetCommWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Client Command: Send a spoken text or a emote or a text command (
 * {@link CommandList#CMD_SAY}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SayCmd extends AbstractCommand {
    /**
     * The text that is send to the server.
     */
    @NotNull
    private final String text;

    /**
     * Default constructor for the say text command.
     */
    public SayCmd(@NotNull SpeechMode mode, @NotNull String text) {
        super(getCommandId(mode));

        this.text = text;
    }

    /**
     * Get the fitting command ID according to the speech mode.
     *
     * @param mode the speech mode
     * @return the command id
     * @throws IllegalArgumentException in case {@code mode} is not {@link SpeechMode#Normal} or
     * {@link SpeechMode#Shout} or {@link SpeechMode#Whisper}
     */
    private static int getCommandId(@NotNull SpeechMode mode) {
        switch (mode) {
            case Normal:
                return CommandList.CMD_SAY;
            case Shout:
                return CommandList.CMD_SHOUT;
            case Whisper:
                return CommandList.CMD_WHISPER;
            default:
                throw new IllegalArgumentException("Illegal speech mode supplied.");
        }
    }

    @NotNull
    @Override
    public String toString() {
        return toString("Text: " + text + " FrameAnimationMode: " + getId());
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) throws IOException {
        writer.writeString(text);
    }
}
