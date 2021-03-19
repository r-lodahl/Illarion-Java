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

/**
 * This enumerator contains a list of all possible compare operators that are
 * usable in a LUA and a easyNPC script.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum CompareOperators {
    equal("="),
    greater(">"),
    greaterEqual("=>"),
    lesser("<"),
    lesserEqual("=<"),
    notEqual("~=");

    /**
     * The lua representation for this comparator.
     */
    @NotNull
    private final String luaComp;

    /**
     * Constructor for the compare operators.
     *
     * @param lua the LUA representation of this operator
     */
    CompareOperators(@NotNull String lua) {
        luaComp = lua;
    }

    /**
     * Get the LUA representation of this operator.
     *
     * @return the LUA representation
     */
    @NotNull
    public String getLuaComp() {
        return luaComp;
    }
}
