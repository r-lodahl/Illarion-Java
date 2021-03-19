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

import illarion.client.world.World;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * This class is used to store the current inventory of the player character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Inventory {
    /**
     * The amount of available inventory slots.
     */
    public static final int SLOT_COUNT = 18;

    /**
     * The items stored in this inventory.
     */
    @NotNull
    private final InventorySlot[] slots;

    /**
     * Prepare the internal data structures to store the items in this
     * inventory.
     */
    public Inventory() {
        slots = new InventorySlot[SLOT_COUNT];
        for (int i = 0; i < SLOT_COUNT; i++) {
            slots[i] = new InventorySlot(i);
        }
    }

    /**
     * Get the inventory item at one slot.
     *
     * @param slot the slot
     * @return the item in the slot
     * @throws IndexOutOfBoundsException in case {@code slot} is outside of the valid range
     */
    @NotNull
    @Contract(pure = true)
    public InventorySlot getItem(int slot) {
        return slots[slot];
    }

    /**
     * Change a item in the inventory.
     *
     * @param slot the slot to change
     * @param id the ID of the new item
     * @param count the new item count
     */
    public void setItem(int slot, @Nullable ItemId id, @Nullable ItemCount count) {
        slots[slot].setData(id, count);
        if (World.getGameGui().isReady()) {
            World.getGameGui().getInventoryGui().setItemSlot(slot, id, count);
        }
    }
}
