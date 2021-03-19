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
 * This command is send to tell the server that the character is not introducing itself. All characters in range get
 * its name.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class IntroduceCmd extends AbstractCommand {
    /**
     * Default constructor for the introduce message.
     */
    public IntroduceCmd() {
        super(CommandList.CMD_INTRODUCE);
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) {
    }

    @NotNull
    @Override
    public String toString() {
        return toString(null);
    }
}
