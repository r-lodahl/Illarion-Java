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
package illarion.easynpc.parsed;

import illarion.easynpc.parsed.shared.ParsedItemData;
import illarion.easynpc.writer.LuaRequireTable;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.LuaWriter.WritingStage;
import illarion.easynpc.writer.SQLBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;

/**
 * This class enables the NPC to trade and stores a single item the NPC is trading.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ParsedTradeComplex extends AbstractParsedTrade {
    /**
     * The IDs of the items that are supposed to be traded.
     */
    private final int itemId;
    private final String textDe;
    private final String textEn;
    private final int price;
    private final int stackSize;
    private final int quality;
    private final ParsedItemData data;

    public ParsedTradeComplex(
            TradeMode tradeMode,
            int tradeItemId,
            String itemTextDe,
            String itemTextEn,
            int tradePrice,
            int tradeStackSize,
            int itemQuality,
            ParsedItemData itemData) {
        super(tradeMode);

        itemId = tradeItemId;
        textDe = itemTextDe;
        textEn = itemTextEn;
        price = tradePrice;
        stackSize = tradeStackSize;
        quality = itemQuality;
        data = itemData;
    }

    @Override
    public void buildSQL(@NotNull SQLBuilder builder) {
        // nothing to do
    }

    @Override
    public void writeLua(@NotNull Writer target, @NotNull LuaRequireTable requires, @NotNull WritingStage stage) throws IOException {
        if (stage == WritingStage.Trading) {
            target.write("tradingNPC:addItem(" + requires.getStorage("npc.base.trade") + ".tradeNPCItem(");
            target.write(Integer.toString(itemId));
            target.write(",");
            switch (getMode()) {
                case selling:
                    target.write("\"sell\"");
                    break;
                case buyingPrimary:
                    target.write("\"buyPrimary\"");
                    break;
                case buyingSecondary:
                    target.write("\"buySecondary\"");
                    break;
            }
            target.write(",");

            if (textDe != null) {
                target.write(String.format("\"%1$s\",", textDe));
            } else {
                target.write("nil,");
            }
            if (textEn != null) {
                target.write(String.format("\"%1$s\",", textEn));
            } else {
                target.write("nil,");
            }
            if (price > 0) {
                target.write(String.format("%1$d,", price));
            } else {
                target.write("nil,");
            }
            if (stackSize > 0) {
                target.write(String.format("%1$d,", stackSize));
            } else {
                target.write("nil,");
            }
            if (quality > 0) {
                target.write(String.format("%1$d,", quality));
            } else {
                target.write("nil,");
            }
            if (data.hasValues()) {
                target.write(data.getLua());
            } else {
                target.write("nil");
            }
            target.write("))");
            target.write(LuaWriter.NL);
        }
    }
}
