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

import illarion.common.util.CalledByReflection;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Handler implements Serializable {

    @NotNull
    private String type;
    @NotNull
    private Object[] parameters;

    public Handler() {
    }

    public Handler(@NotNull String type, @NotNull Object... parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    @CalledByReflection
    public void setParameters(@NotNull Object... parameters) {
        this.parameters = parameters;
    }

    @CalledByReflection
    public void setType(@NotNull String type) {
        this.type = type;
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