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
package illarion.client.util;

import illarion.client.resources.Resource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;


/**
 * This utility class is used to wrap a object together with a ID.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class IdWrapper<T> implements Resource {
    /**
     * The object that is stored in this instance.
     */
    @NotNull
    private final T object;

    /**
     * The ID that is connected with the object.
     */
    private final int id;

    /**
     * Constructor that allows to set the ID and the object that are supposed to
     * be linked together.
     *
     * @param id the ID
     * @param object the object
     */
    public IdWrapper(int id, @NotNull T object) {
        this.id = id;
        this.object = object;
    }

    /**
     * Get the ID of this object.
     *
     * @return the Id
     */
    @Contract(pure = true)
    public int getId() {
        return id;
    }

    /**
     * Get the wrapped object.
     *
     * @return the object
     */
    @NotNull
    @Contract(pure = true)
    public T getObject() {
        return object;
    }
}
