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

import illarion.common.types.ItemCount;
import org.jetbrains.annotations.NotNull;

/**
 * This abstract command contains the shared code of all dragging operations.
 *
 * @author Martin Karing &lt;nitram@illlarion.org&gt;
 */
public abstract class AbstractDragCommand extends AbstractCommand {
    /**
     * The amount of items that are supposed to be moved.
     */
    @NotNull
    private final ItemCount count;

    /**
     * The constructor of a command. This is used to set the ID of the command.
     *
     * @param commId the ID of the command
     * @param count the amount of items to drag at once
     */
    protected AbstractDragCommand(int commId, @NotNull ItemCount count) {
        super(commId);

        this.count = count;
    }

    /**
     * Get the amount of items that are supposed to be moved.
     *
     * @return the items to be moved
     */
    @NotNull
    protected final ItemCount getCount() {
        return count;
    }
}
