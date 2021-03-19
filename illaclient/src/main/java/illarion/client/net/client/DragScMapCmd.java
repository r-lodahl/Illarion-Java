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
 * Client Command: Dragging an item from a container to the map (CommandList#CMD_DRAG_SC_MAP}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DragScMapCmd extends AbstractDragCommand {
    /**
     * The source container of the dragging event.
     */
    private final short sourceContainer;

    /**
     * The source container item of the dragging event.
     */
    private final short sourceContainerItem;

    /**
     * The target location of the dragging event.
     */
    @NotNull
    private final ServerCoordinate targetLocation;

    /**
     * The default constructor of this DragScMapCmd.
     *
     * @param sourceContainer the container that is the source
     * @param sourceSlot the slot in the container that is the source
     * @param destination the location on the map that is the destination of the drag
     * @param count the amount of items to move
     */
    public DragScMapCmd(
            int sourceContainer,
            int sourceSlot,
            @NotNull ServerCoordinate destination,
            @NotNull ItemCount count) {
        super(CommandList.CMD_DRAG_SC_MAP, count);

        this.sourceContainer = (short) sourceContainer;
        sourceContainerItem = (short) sourceSlot;
        targetLocation = destination;
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) {
        writer.writeUByte(sourceContainer);
        writer.writeUByte(sourceContainerItem);
        targetLocation.encode(writer);
        getCount().encode(writer);
    }

    @NotNull
    @Override
    public String toString() {
        return toString("Source: " + sourceContainer + '/' + sourceContainerItem + " Destination: " + targetLocation +
                                ' ' + getCount());
    }
}
