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
 * This is a configuration entry that is used to display a checkbox in the
 * configuration dialog. So a simple yes/no option.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CheckEntry implements ConfigEntry {
    /**
     * The configuration that is controlled by this text entry.
     */
    @Nullable
    private Config cfg;

    /**
     * The key in the configuration that is handled by this configuration.
     */
    @NotNull
    private final String configEntry;

    /**
     * Create a new configuration entry that is handled by this entry.
     *
     * @param entry the configuration key that is handled by this text entry
     */
    public CheckEntry(@NotNull String entry) {
        configEntry = entry;
    }

    /**
     * Get the value set in the configuration for this check entry.
     *
     * @return the configuration stored for this check entry
     */
    public boolean getValue() {
        if (cfg == null) {
            throw new IllegalStateException("There is no reference to the configuration system set.");
        }
        return cfg.getBoolean(configEntry);
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
    public void setValue(boolean newValue) {
        if (cfg == null) {
            throw new IllegalStateException("There is no reference to the configuration system set.");
        }
        cfg.set(configEntry, newValue);
    }
}
