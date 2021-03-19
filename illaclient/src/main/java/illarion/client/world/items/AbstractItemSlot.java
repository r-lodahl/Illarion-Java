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

import illarion.client.resources.ItemFactory;
import illarion.client.resources.data.ItemTemplate;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import org.jetbrains.annotations.Nullable;


/**
 * This is the abstract item slot that contains all functions shared by the different item slots, like the inventory
 * slots or the container slots.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractItemSlot {
    /**
     * The count of items on this slot.
     */
    @Nullable
    private ItemCount count;

    /**
     * The ID of the item on this slot.
     */
    @Nullable
    private ItemId itemId;

    /**
     * Check if this slot stores a item.
     *
     * @return {@code true} in case this slot stores a item
     */
    public boolean containsItem() {
        return ItemId.isValidItem(itemId);
    }

    /**
     * Get the amount of items.
     *
     * @return the item count or {@code null} in case there is not item in this slot
     */
    @Nullable
    public ItemCount getCount() {
        return count;
    }

    /**
     * Get the ID of the item.
     *
     * @return the ID or {@code null} in case there is not item in this slot
     */
    @Nullable
    public ItemId getItemID() {
        return itemId;
    }

    /**
     * Get the template of the item that is located in this slot.
     *
     * @return the item or {@code null} in case there is not item in this slot
     */
    @Nullable
    public ItemTemplate getItemTemplate() {
        if (!containsItem()) {
            return null;
        }
        assert itemId != null;
        return ItemFactory.getInstance().getTemplate(itemId.getValue());
    }

    /**
     * Set the information's about this item.
     *
     * @param newId the ID of the item
     * @param newCount the amount of items
     */
    public void setData(@Nullable ItemId newId, @Nullable ItemCount newCount) {
        itemId = newId;
        count = newCount;
    }

    /**
     * Empty this slot.
     */
    public void clearSlot() {
        itemId = null;
        count = null;
    }
}
