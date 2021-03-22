package org.illarion.engine.backend.gdx.events;

import illarion.common.config.ConfigReader;
import org.jetbrains.annotations.NotNull;

public class ResolutionChangedEvent {
    @NotNull public final ConfigReader config;

    public ResolutionChangedEvent(@NotNull ConfigReader config) {
        this.config = config;
    }
}
