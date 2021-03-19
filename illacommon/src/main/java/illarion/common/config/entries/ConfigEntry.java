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
package illarion.common.config.entries;

import illarion.common.config.Config;
import org.jetbrains.annotations.NotNull;


/**
 * This interface describes how a configuration entry is supposed work like in
 * public. The configuration entries are the things shown in the configuration
 * dialog next to the names of the lines.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@FunctionalInterface
public interface ConfigEntry {
    /**
     * Set the configuration handled by this configuration entry.
     *
     * @param config the configuration that is supposed to be handled by this
     * configuration entry
     */
    void setConfig(@NotNull Config config);
}
