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
package illarion.easynpc.data;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * This enumerator contains a list of all possible calculation operators that
 * are usable in a LUA and a easyNPC script.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum CalculationOperators {
    /**
     * The addition operator with all required data to detect it in a easyNPC
     * script and to write it into a LUA script.
     */
    add("+", "^\\s*(\\+[=]*)\\s*$"),

    /**
     * The set operator with all required data to detect it in a easyNPC
     * script and to write it into a LUA script.
     */
    set("=", "^\\s*=\\s*$"),

    /**
     * The subtraction operator with all required data to detect it in a easyNPC
     * script and to write it into a LUA script.
     */
    subtract("-", "^\\s*(-[=]*)\\s*$");

    /**
     * The LUA representation for this operator.
     */
    private final String luaOp;

    /**
     * The RegExp pattern to identify operator in the string.
     */
    @NotNull
    private final Pattern regexpOp;

    /**
     * Constructor for the calculation operators.
     *
     * @param lua the LUA representation of this operator
     * @param regexp the RegExp pattern to identify this operator
     */
    CalculationOperators(String lua, @NotNull String regexp) {
        luaOp = lua;
        regexpOp = Pattern.compile(regexp);
    }

    /**
     * Get the LUA representation of this operator.
     *
     * @return the LUA representation
     */
    public String getLuaOp() {
        return luaOp;
    }

    /**
     * Get the RegExp pattern usable to identify the operator in the easyNPC
     * script.
     *
     * @return the pattern to find this operator
     */
    public Pattern getRegexpPattern() {
        return regexpOp;
    }
}
