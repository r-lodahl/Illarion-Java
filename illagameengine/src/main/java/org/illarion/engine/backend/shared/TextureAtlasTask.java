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
package org.illarion.engine.backend.shared;

/**
 * This interface defines a texture atlas task. These tasks need to report if they finished their task already or not.
 * This is required to detect if the loading is done or still pending.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@FunctionalInterface
public interface TextureAtlasTask {
    /**
     * Check if this task is done.
     *
     * @return {@code true} in case this task has finished
     */
    boolean isDone();
}
