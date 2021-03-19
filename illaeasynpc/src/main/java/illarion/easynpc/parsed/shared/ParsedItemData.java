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
package illarion.easynpc.parsed.shared;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The storage of the parsed item data values.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedItemData {
    /**
     * The data values.
     */
    @NotNull
    private final Map<String, String> dataValues;

    /**
     * Create a new instance of the parsed data values and set the values stored in this class.
     *
     * @param values the data values
     */
    public ParsedItemData(@NotNull Map<String, String> values) {
        dataValues = new HashMap<>(values);
    }

    /**
     * Check if there are any values encoded in this parsed data object.
     *
     * @return {@code true} in case there are data values encoded here
     */
    public boolean hasValues() {
        return !dataValues.isEmpty();
    }

    /**
     * Get the representation of the values formatted for the easyNPC language.
     *
     * @return the data values for easyNPC
     */
    @NotNull
    public String getEasyNPC() {
        if (!hasValues()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : dataValues.entrySet()) {
            sb.append('"').append(entry.getKey()).append("\" = \"").append(entry.getValue()).append("\", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    /**
     * Get the data values formatted for the LUA script.
     *
     * @return the data values for the lua script
     */
    @NotNull
    public String getLua() {
        if (!hasValues()) {
            return "nil";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (Entry<String, String> entry : dataValues.entrySet()) {
            sb.append("[\"").append(entry.getKey()).append("\"] = \"").append(entry.getValue()).append("\", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append('}');
        return sb.toString();
    }
}
