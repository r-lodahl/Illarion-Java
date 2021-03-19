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
package illarion.client.util;


/**
 * This interface defines a task that is executed by the {@link UpdateTaskManager}.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface UpdateTask {
    /**
     * This function is called during the text run of the update loop.
     *
     * @param delta the time since the last update in milliseconds
     */
    void onUpdateGame(int delta);
}
