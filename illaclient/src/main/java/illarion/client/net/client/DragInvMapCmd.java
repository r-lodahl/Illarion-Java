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
import illarion.common.types.ItemCount;
import illarion.common.types.ServerCoordinate;
import org.jetbrains.annotations.NotNull;

/**
 * Client Command: Dragging a item from a inventory slot to the game map ({@link CommandList#CMD_DRAG_INV_MAP}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DragInvMapCmd extends AbstractDragCommand {
    /**
     * The location on the map that is the target of the move operation.
     */
    @NotNull
    private final ServerCoordinate dstLoc;

    /**
     * Inventory position the drag starts at.
     */
    private final short srcPos;

    /**
     * Default constructor for the dragging from inventory to map command.
     */
    public DragInvMapCmd(int source, @NotNull ServerCoordinate destination, @NotNull ItemCount count) {
        super(CommandList.CMD_DRAG_INV_MAP, count);
        srcPos = (short) source;
        dstLoc = destination;
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) {
        writer.writeUByte(srcPos);
        dstLoc.encode(writer);
        getCount().encode(writer);
    }

    @NotNull
    @Override
    public String toString() {
        return toString("Source: " + srcPos + " Destination: " + dstLoc + ' ' + getCount());
    }
}
