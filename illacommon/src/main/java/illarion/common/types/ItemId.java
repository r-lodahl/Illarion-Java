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

import illarion.common.net.NetCommReader;
import illarion.common.net.NetCommWriter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * This class is used to store the ID of a item.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ItemId implements Comparable<ItemId> {
    /**
     * The maximal value that is valid for the item ID.
     */
    public static final int MAX_VALUE = (1 << 16) - 1;

    /**
     * The minimal value that is valid for the item ID.
     */
    public static final int MIN_VALUE = 0;

    /**
     * The item count.
     */
    private final int value;

    /**
     * Constructor of this class used to set.
     *
     * @param value the value of the item ID
     * @throws IllegalArgumentException in case the value is less then {@link #MIN_VALUE} or larger then
     * {@link #MAX_VALUE}.
     */
    public ItemId(int value) {
        if ((value < MIN_VALUE) || (value > MAX_VALUE)) {
            throw new IllegalArgumentException("value is out of range.");
        }
        this.value = value;
    }

    /**
     * This constructor is used to decode the item ID from the network interface.
     *
     * @param reader the reader
     * @throws IOException in case the reading operation fails for some reason
     */
    public ItemId(@NotNull NetCommReader reader) throws IOException {
        value = reader.readUShort();
    }

    /**
     * Check if the ID is valid for a item. This means the ID has to be not {@code null} and its value has to be
     * greater then {@code 0}.
     *
     * @param id the ID to test
     * @return {@code true} in case the id is valid
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean isValidItem(@Nullable ItemId id) {
        return (id != null) && (id.getValue() > 0);
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj) || ((obj instanceof ItemId) && equals((ItemId) obj));
    }

    @Override
    @Contract(pure = true)
    public int hashCode() {
        return value;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public String toString() {
        return "Item ID: " + value;
    }

    /**
     * Encode the value of the item ID to the network interface.
     *
     * @param writer the writer that receives the value
     */
    public void encode(@NotNull NetCommWriter writer) {
        writer.writeUShort(value);
    }

    /**
     * Check if two item id instances are equal.
     *
     * @param obj the second instance to check
     * @return {@code true} in case both instances represent the same value
     */
    @Contract(value = "null -> false", pure = true)
    public boolean equals(@Nullable ItemId obj) {
        return (obj != null) && (value == obj.value);
    }

    /**
     * Check if two item id instances are equal.
     *
     * @param id1 the first instance to check
     * @param id2 the second instance to check
     * @return {@code true} in case both instances represent the same value
     */
    @Contract(value = "null,null -> true; !null,null -> false; null,!null -> false", pure = true)
    public static boolean equals(@Nullable ItemId id1, @Nullable ItemId id2) {
        return id1 == null ? id2 == null : id1.equals(id2);
    }

    /**
     * Get the value of the item count.
     *
     * @return the item count value
     */
    @Contract(pure = true)
    public int getValue() {
        return value;
    }

    @Override
    @Contract(pure = true)
    public int compareTo(@NotNull ItemId o) {
        if (value == o.value) {
            return 0;
        }
        if (getValue() < o.getValue()) {
            return -1;
        }
        return 1;
    }
}
