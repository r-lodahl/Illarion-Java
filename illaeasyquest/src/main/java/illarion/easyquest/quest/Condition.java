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
package illarion.easyquest.quest;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;

@SuppressWarnings("serial")
public class Condition implements Serializable {
    @NotNull
    private final String type;
    @NotNull
    private final Object[] parameters;

    public Condition(@NotNull String type, @NotNull Object... parameters) {
        this.type = type;
        this.parameters = Arrays.copyOf(parameters, parameters.length);
    }

    @NotNull
    public String getType() {
        return type;
    }

    @NotNull
    public Object[] getParameters() {
        return parameters;
    }
}