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
 * This client command is used to stop the attack on a character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class StandDownCmd extends AbstractCommand {
    /**
     * The default constructor of the stand down command.
     */
    public StandDownCmd() {
        super(CommandList.CMD_STAND_DOWN);
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
