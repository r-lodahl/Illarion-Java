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

import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import org.jetbrains.annotations.NotNull;

/**
 * This item is a ingredient to a craft.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CraftingIngredientItem {
    /**
     * The ID of the ingredient.
     */
    @NotNull
    private final ItemId itemId;

    /**
     * The amount of items required.
     */
    @NotNull
    private final ItemCount count;

    /**
     * Constructor used to set the item.
     *
     * @param itemId the ID of the item
     * @param count the amount of items required
     */
    public CraftingIngredientItem(@NotNull ItemId itemId, @NotNull ItemCount count) {
        this.itemId = itemId;
        this.count = count;
    }

    /**
     * The ID of this ingredient item.
     *
     * @return the item id
     */
    @NotNull
    public ItemId getItemId() {
        return itemId;
    }

    /**
     * The count of items.
     *
     * @return the amount of items
     */
    @NotNull
    public ItemCount getCount() {
        return count;
    }
}
