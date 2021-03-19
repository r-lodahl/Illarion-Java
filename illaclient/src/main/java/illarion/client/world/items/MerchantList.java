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

import illarion.client.gui.DialogType;
import illarion.client.net.client.BuyTradingItem;
import illarion.client.net.client.CloseDialogTradingCmd;
import illarion.client.world.World;
import illarion.client.world.items.MerchantItem.MerchantItemType;
import illarion.common.types.ItemCount;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * This classes are used to store to information about the goods a merchant is trading.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class MerchantList {
    /**
     * This is the list of items the merchant is trading.
     */
    @NotNull
    private final List<MerchantItem> itemList;

    /**
     * This is the ID of the list.
     */
    private final int listId;

    /**
     * Create a new instance of this list with the specified ID.
     *
     * @param id the ID used to refer to this list
     */
    public MerchantList(int id) {
        listId = id;
        itemList = new ArrayList<>();
    }

    /**
     * Add a item to the list.
     *
     * @param item the item to add to the list
     */
    public void addItem(@NotNull MerchantItem item) {
        itemList.add(item);
    }

    /**
     * Get one item of this merchant list.
     *
     * @param index the index of the requested item
     * @return the merchant item at this entry
     * @throws ArrayIndexOutOfBoundsException in case the index is too large or too small
     */
    @NotNull
    public MerchantItem getItem(int index) {
        MerchantItem item = itemList.get(index);
        if (item == null) {
            throw new IllegalStateException("Item on the index " + index + " is not set yet.");
        }
        return item;
    }

    @Nullable
    public MerchantItem getItem(@NotNull MerchantItemType type, int index) {
        for (MerchantItem item : itemList) {
            if ((item != null) && (item.getType() == type) && (item.getIndex() == index)) {
                return item;
            }
        }
        return null;
    }

    /**
     * The amount of items in this list.
     *
     * @return the count of items
     */
    @Contract(pure = true)
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * Get the ID of this merchant list.
     *
     * @return the ID of the merchant list
     */
    @Contract(pure = true)
    public int getId() {
        return listId;
    }

    /**
     * Close this dialog by sending the command to the server that orders the dialog to close.
     */
    public void closeDialog() {
        World.getNet().sendCommand(new CloseDialogTradingCmd(listId));
        World.getPlayer().closeDialog(listId, EnumSet.of(DialogType.Merchant));
    }

    /**
     * Buy this item.
     *
     * @param item the index of the item to buy
     */
    public void buyItem(@NotNull MerchantItem item) {
        buyItem(item, item.getBundleSize());
    }

    /**
     * Buy this item.
     *
     * @param item the index of the item to buy
     */
    public void buyItem(@NotNull MerchantItem item, @NotNull ItemCount count) {
        if (!itemList.contains(item)) {
            throw new IllegalArgumentException("This item is not part of this merchant list");
        }
        buyItem(item.getIndex(), count);
    }

    /**
     * Buy this item.
     *
     * @param index the index of the item to buy
     */
    public void buyItem(int index) {
        buyItem(getItem(index));
    }

    /**
     * Buy this item.
     *
     * @param index the index of the item to buy
     * @param count the amount of items to buy
     */
    public void buyItem(int index, @NotNull ItemCount count) {
        World.getNet().sendCommand(new BuyTradingItem(listId, index, count));
    }
}
