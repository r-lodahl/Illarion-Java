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
package illarion.common.types;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class contains the game money. It can be used to split the components of the money (copper, silver, gold).
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class Money implements Comparable<Money> {
    /**
     * The conversation factor from copper to silver coins.
     */
    private static final long COPPER_TO_SILVER = 100;

    /**
     * The conversation factor from copper to gold coins.
     */
    private static final long COPPER_TO_GOLD = 10000;

    /**
     * The copper coins stored in this money object.
     */
    private final long copperCoins;

    /**
     * Create a new money object and store the copper coins.
     *
     * @param copper the copper coins
     */
    public Money(long copper) {
        copperCoins = copper;
    }

    /**
     * Create a new money object and store the price in gold, silver and copper.
     *
     * @param gold the gold coins
     * @param silver the silver coins
     * @param copper the copper coins
     */
    public Money(int gold, int silver, int copper) {
        this((gold * COPPER_TO_GOLD) + (silver * COPPER_TO_SILVER) + copper);
    }

    /**
     * Get the copper coins component of the money.
     *
     * @return the copper coin component
     */
    @Contract(pure = true)
    public int getCopper() {
        return (int) (copperCoins % COPPER_TO_SILVER);
    }

    /**
     * Get the silver coins component of the money.
     *
     * @return the silver coin component
     */
    @Contract(pure = true)
    public int getSilver() {
        return (int) ((copperCoins % COPPER_TO_GOLD) / COPPER_TO_SILVER);
    }

    /**
     * Get the gold coins component of the money.
     *
     * @return the gold coin component
     */
    @Contract(pure = true)
    public int getGold() {
        return (int) (copperCoins / COPPER_TO_GOLD);
    }

    /**
     * Get the entire amount of this money in copper coins.
     *
     * @return the money in copper coins
     */
    @Contract(pure = true)
    public long getTotalCopper() {
        return copperCoins;
    }

    /**
     * Get the entire amount of this money in silver coins.
     *
     * @return the money in silver coins
     */
    @Contract(pure = true)
    public long getTotalSilver() {
        return copperCoins / COPPER_TO_SILVER;
    }

    /**
     * Get the entire amount of this money in gold coins.
     *
     * @return the money in gold coins
     */
    @Contract(pure = true)
    public long getTotalGold() {
        return copperCoins / COPPER_TO_GOLD;
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof Money) && equals((Money) obj);
    }

    @Contract(value = "null -> false", pure = true)
    public boolean equals(@Nullable Money money) {
        return (money != null) && (money.copperCoins == copperCoins);
    }

    @Override
    @Contract(pure = true)
    public int hashCode() {
        return Long.valueOf(copperCoins).hashCode();
    }

    @Override
    @Contract(pure = true)
    public int compareTo(@NotNull Money o) {
        return Long.compare(copperCoins, o.copperCoins);
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public String toString() {
        return getGold() + "g " + getSilver() + "s " + getCopper() + 'c';
    }
}
