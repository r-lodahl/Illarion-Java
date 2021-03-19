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
package illarion.client.net.server;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Some small helper functions for the messages.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class Utilities {
    private Utilities() {
    }

    @NotNull
    @Contract(pure = true)
    static <T extends ServerReply> String toString(@NotNull Class<T> baseClass) {
        return baseClass.getSimpleName();
    }

    @NotNull
    @Contract(pure = true)
    @SuppressWarnings("OverloadedVarargsMethod")
    static <T extends ServerReply> String toString(@NotNull Class<T> baseClass,
                                                   @NotNull Object... dataValues) {
        return toString(baseClass) + Arrays.toString(dataValues);
    }
}
