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
 * Client Command: Dragging an item from the map to a container ({@link CommandList#CMD_DRAG_MAP_SC}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DragMapScCmd extends AbstractDragCommand {
    /**
     * The location where the item that is moved is located at.
     */
    @NotNull
    private final ServerCoordinate srcLoc;

    /**
     * The target container of the dragging event.
     */
    private final short targetContainer;

    /**
     * The target slot of the container.
     */
    private final short targetContainerSlot;

    /**
     * The default constructor of this DragMapScCmd.
     *
     * @param source the location from where the item is taken
     * @param destinationContainer the container that is the destination
     * @param destinationSlot the slot in the container that is the destination
     * @param count the amount of items to move
     */
    public DragMapScCmd(
            @NotNull ServerCoordinate source,
            int destinationContainer,
            int destinationSlot,
            @NotNull ItemCount count) {
        super(CommandList.CMD_DRAG_MAP_SC, count);

        srcLoc = source;
        targetContainer = (short) destinationContainer;
        targetContainerSlot = (short) destinationSlot;
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) {
        srcLoc.encode(writer);
        writer.writeUByte(targetContainer);
        writer.writeUByte(targetContainerSlot);
        getCount().encode(writer);
    }

    @NotNull
    @Override
    public String toString() {
        return toString("Source: " + srcLoc + " Destination: " + targetContainer + '/' +
                                targetContainerSlot + ' ' + getCount());
    }
}
