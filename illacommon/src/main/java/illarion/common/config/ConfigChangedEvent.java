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
package illarion.common.config;


import org.jetbrains.annotations.NotNull;

/**
 * This class defines the event that is sent to the application using the event bus once an entry of the
 * configuration is altered.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConfigChangedEvent {
    /**
     * The configuration that is used.
     */
    @NotNull
    private final ConfigReader config;

    /**
     * The key inside the configuration that was altered.
     */
    @NotNull
    private final String key;

    /**
     * Constructor that allows to set the config that triggered this event as well as the key that was changed.
     *
     * @param config the config that triggered this event
     * @param key the key that was changed
     */
    public ConfigChangedEvent(@NotNull ConfigReader config, @NotNull String key) {
        this.config = config;
        this.key = key;
    }

    /**
     * Get the configuration that is changed.
     *
     * @return the configuration that triggered this event
     */
    @NotNull
    public ConfigReader getConfig() {
        return config;
    }

    /**
     * Get the key that was changed inside the configuration.
     *
     * @return the key that was changed
     */
    @NotNull
    public String getKey() {
        return key;
    }
}
