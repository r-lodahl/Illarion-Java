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
import java.text.NumberFormat;
import java.util.Locale;

/**
 * This class is used to store the stack size of a item.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ItemCount implements Comparable<ItemCount> {
    /**
     * The maximal value that is valid for the item count.
     */
    public static final int MAX_VALUE = (1 << 16) - 1;

    /**
     * The minimal value that is valid for the item count.
     */
    public static final int MIN_VALUE = 0;

    /**
     * Stack instance for the count value 0.
     */
    @NotNull
    public static final ItemCount ZERO = new ItemCount(0);

    /**
     * Static instance for the count value 1.
     */
    @NotNull
    public static final ItemCount ONE = new ItemCount(1);

    /**
     * The item count.
     */
    private final int value;

    /**
     * Fetch a instance of item count.
     *
     * @param value the value of the item count
     * @return the new instance of the item count representing the value
     * @throws IllegalArgumentException in case the value is less then {@link #MIN_VALUE} or larger then
     * {@link #MAX_VALUE}.
     */
    @NotNull
    @Contract(pure = true)
    public static ItemCount getInstance(int value) {
        switch (value) {
            case 0:
                return ZERO;
            case 1:
                return ONE;
            default:
                return new ItemCount(value);
        }
    }

    /**
     * Get a new instance from item count.
     *
     * @param reader the network reader that is used to fetch the value
     * @return the new instance of the item count representing the value
     * @throws IllegalArgumentException in case the value is less then {@link #MIN_VALUE} or larger then
     * {@link #MAX_VALUE}.
     * @throws IOException in case the reading operation fails
     */
    @NotNull
    public static ItemCount getInstance(@NotNull NetCommReader reader) throws IOException {
        return getInstance(reader.readUShort());
    }

    /**
     * Constructor of this class used to set.
     *
     * @param value the value of the item count
     * @throws IllegalArgumentException in case the value is less then {@link #MIN_VALUE} or larger then
     * {@link #MAX_VALUE}.
     */
    private ItemCount(int value) {
        if ((value < MIN_VALUE) || (value > MAX_VALUE)) {
            throw new IllegalArgumentException("value is out of range.");
        }
        this.value = value;
    }

    @Contract(value = "null -> false", pure = true)
    public static boolean isGreaterZero(@Nullable ItemCount count) {
        return (count != null) && (count.getValue() > 0);
    }

    @Contract(value = "null -> false", pure = true)
    public static boolean isGreaterOne(@Nullable ItemCount count) {
        return (count != null) && (count.getValue() > 1);
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj) || ((obj instanceof ItemCount) && equals((ItemCount) obj));
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
        return "Item count: " + value;
    }

    /**
     * Get the item number as short formatted text. This shortens all value greater then 999.
     *
     * @param locale the locale used for the format
     * @return the string
     */
    @NotNull
    @Contract(pure = true)
    public String getShortText(@NotNull Locale locale) {
        if (value < 1000) {
            return Integer.toString(value);
        }
        NumberFormat formatter = NumberFormat.getNumberInstance(locale);
        if (value < 10000) {
            formatter.setMaximumFractionDigits(1);
        } else {
            formatter.setMaximumFractionDigits(0);
        }
        return formatter.format(value / 1000.0) + 'k';
    }

    /**
     * Encode the value of the item count to the network interface.
     *
     * @param writer the writer that receives the value
     */
    public void encode(@NotNull NetCommWriter writer) {
        writer.writeUShort(value);
    }

    /**
     * Check if two item count instances are equal.
     *
     * @param obj the second instance to check
     * @return {@code true} in case both instances represent the same value
     */
    @Contract(value = "null -> false", pure = true)
    public boolean equals(@Nullable ItemCount obj) {
        return (obj != null) && (value == obj.value);
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
    public int compareTo(@Nullable ItemCount o) {
        int value = getValue();
        int otherValue = (o == null) ? 0 : o.getValue();
        return Integer.compare(value, otherValue);
    }
}
