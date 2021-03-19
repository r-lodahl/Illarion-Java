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
import org.jetbrains.annotations.NotNull;


/**
 * Client Command: Dragging an item from one container to another ({@link CommandList#CMD_DRAG_SC_SC}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DragScScCmd extends AbstractDragCommand {
    /**
     * The source container of the dragging event.
     */
    private final short sourceContainer;

    /**
     * The source container item of the dragging event.
     */
    private final short sourceContainerItem;

    /**
     * The target container of the dragging event.
     */
    private final short targetContainer;

    /**
     * The target container item of the dragging event.
     */
    private final short targetContainerItem;

    /**
     * The default constructor of this DragScScCmd.
     *
     * @param sourceContainer the container that is the source
     * @param sourceSlot the slot in the container that is the source
     * @param destinationContainer the container that is the destination
     * @param destinationSlot the slot in the container that is the destination
     * @param count the amount of items to move
     */
    public DragScScCmd(
            int sourceContainer,
            int sourceSlot,
            int destinationContainer,
            int destinationSlot,
            @NotNull ItemCount count) {
        super(CommandList.CMD_DRAG_SC_SC, count);

        this.sourceContainer = (short) sourceContainer;
        sourceContainerItem = (short) sourceSlot;
        targetContainer = (short) destinationContainer;
        targetContainerItem = (short) destinationSlot;
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) {
        writer.writeUByte(sourceContainer);
        writer.writeUByte(sourceContainerItem);
        writer.writeUByte(targetContainer);
        writer.writeUByte(targetContainerItem);
        getCount().encode(writer);
    }

    @NotNull
    @Override
    public String toString() {
        return toString(
                "Source: " + sourceContainer + '/' + sourceContainerItem + " Destination: " + targetContainer + '/' +
                        targetContainerItem + ' ' + getCount());
    }
}
