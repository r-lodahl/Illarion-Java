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
package illarion.mapedit.events.menu;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * @author Tim
 */
public class MapOpenEvent {

    @NotNull
    private final Path path;
    private final String name;

    public MapOpenEvent(@NotNull Path path, String name) {
        this.path = path;
        this.name = name;
    }

    @NotNull
    public Path getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public String toString() {
        return name;
    }
}
