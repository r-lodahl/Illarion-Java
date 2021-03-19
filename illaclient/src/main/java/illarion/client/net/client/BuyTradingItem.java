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

import illarion.common.net.NetCommWriter;
import illarion.common.types.ItemCount;
import org.jetbrains.annotations.NotNull;

/**
 * This command is used to buy a item from a trader.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class BuyTradingItem extends AbstractTradeItemCmd {
    /**
     * The index of the item that is supposed to be bought.
     */
    private final short index;

    /**
     * The amount of items to buy.
     */
    @NotNull
    private final ItemCount amount;

    /**
     * The sub command ID for this command.
     */
    private static final int SUB_CMD_ID = 2;

    /**
     * Default constructor for the trade item command.
     *
     * @param dialogId the ID of the dialog to buy the item from
     * @param index the index of the item to buy
     * @param count the amount of items to buy
     */
    public BuyTradingItem(int dialogId, int index, @NotNull ItemCount count) {
        super(dialogId, SUB_CMD_ID);

        this.index = (short) index;
        amount = count;
    }

    @Override
    public void encode(@NotNull NetCommWriter writer) {
        super.encode(writer);
        writer.writeUByte(index);
        amount.encode(writer);
    }

    @NotNull
    @Override
    public String toString() {
        return toString(super.toString() + " Index: " + index + ' ' + amount);
    }
}
