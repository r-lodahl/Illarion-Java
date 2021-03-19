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
import org.jetbrains.annotations.Nullable;


/**
 * This is a configuration entry that is used to a simple text entry in the
 * configuration.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class TextEntry implements ConfigEntry {
    /**
     * The configuration that is controlled by this text entry.
     */
    private Config cfg;

    /**
     * The key in the configuration that is handled by this configuration.
     */
    private final String configEntry;

    /**
     * Create a new configuration entry that is handled by this entry.
     *
     * @param entry the configuration key that is handled by this text entry
     */
    public TextEntry(String entry) {
        configEntry = entry;
    }

    /**
     * Get the value set in the configuration for this text entry.
     *
     * @return the configuration stored for this text entry
     */
    @Nullable
    public String getValue() {
        return cfg.getString(configEntry);
    }

    /**
     * Set the configuration handled by this configuration entry.
     *
     * @param config the configuration that is supposed to be handled by this
     * configuration entry
     */
    @Override
    public void setConfig(@NotNull Config config) {
        cfg = config;
    }

    /**
     * Set the new value of the configuration entry that is controlled by this.
     *
     * @param newValue the new configuration value
     */
    public void setValue(@NotNull String newValue) {
        cfg.set(configEntry, newValue);
    }
}
