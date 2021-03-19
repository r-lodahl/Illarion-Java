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
package illarion.client.world.items;

import illarion.client.world.interactive.InteractiveInventorySlot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;


/**
 * This class is used to store the data of a single slot in the inventory.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InventorySlot extends AbstractItemSlot {
    /**
     * The interactive reference to this slot.
     */
    @NotNull
    private final InteractiveInventorySlot interactive;

    /**
     * The inventory slot this instance refers to
     */
    private final int slot;

    /**
     * Create a inventory slot for the inventory.
     *
     * @param itemSlot the inventory slot
     */
    public InventorySlot(int itemSlot) {
        slot = itemSlot;
        interactive = new InteractiveInventorySlot(this);
    }

    /**
     * Get the interactive inventory slot that refers to this inventory slot.
     *
     * @return the interactive inventory slot
     */
    @NotNull
    @Contract(pure = true)
    public InteractiveInventorySlot getInteractive() {
        return interactive;
    }

    /**
     * Get the slot of this inventory item.
     *
     * @return the slot of the item
     */
    @Contract(pure = true)
    public int getSlot() {
        return slot;
    }
}
