/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.easynpc.parsed.talk;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used to store a advanced number value that is possibly used by
 * the easyNPC language. Such a number can contain a normal number, a reference
 * to the last said number or a formula.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AdvancedNumber {
    /**
     * This enumerator contains the possible values for a advanced number.
     */
    private enum AdvancedNumberType {
        /**
         * This constant means that the number is a expression.
         */
        Expression,

        /**
         * This constant means that the number is the normal number.
         */
        Normal,

        /**
         * This constant means that the number is a reference to the spoken number of the user.
         */
        SaidNumber
    }

    /**
     * The expression stored in this number. This is only used if the type of this number is
     * {@link AdvancedNumberType#Expression}.
     */
    private String expression;

    /**
     * The type of this number.
     */
    @NotNull
    private final AdvancedNumberType type;

    /**
     * The value of this number. This is used in case the type is
     * {@link {@link AdvancedNumberType#Normal}.
     */
    private int value;

    /**
     * The default constructor causes this number to be a reference to the number the player spoke last.
     */
    public AdvancedNumber() {
        type = AdvancedNumberType.SaidNumber;
    }

    /**
     * This constructor causes this number to refer to a simple number value.
     *
     * @param number the number value
     */
    public AdvancedNumber(int number) {
        type = AdvancedNumberType.Normal;
        value = number;
    }

    /**
     * This constructor causes this number to refer to a expression string.
     *
     * @param expressionString the expression string
     */
    public AdvancedNumber(String expressionString) {
        type = AdvancedNumberType.Expression;
        expression = expressionString;
    }

    /**
     * Get the LUA representation of this advanced number value.
     *
     * @return the LUA representation of the advanced number
     */
    @Nullable
    public String getLua() {
        switch (type) {
            case Normal:
                return Integer.toString(value);
            case SaidNumber:
                return "\"%NUMBER\"";
            case Expression:
                return "function(number) return (" + expression.replace("%NUMBER", "number") + "); end";
        }
        return null;
    }
}
