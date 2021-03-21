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
package illarion.client.resources.loaders;

import illarion.client.resources.ResourceFactory;
import illarion.client.resources.data.EffectTemplate;
import illarion.common.util.TableLoaderEffects;
import illarion.common.util.TableLoaderSink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.assets.SpriteFactory;
import org.illarion.engine.graphic.Sprite;
import org.jetbrains.annotations.NotNull;


/**
 * This class is used to load the effect definitions from the resource table that was created using the configuration
 * tool. The class will create the required effect objects and send them to the effect factory that takes care for
 * distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class EffectLoader extends AbstractResourceLoader<EffectTemplate>
        implements TableLoaderSink<TableLoaderEffects> {
    /**
     * The logger that is used to report error messages.
     */
    @NotNull
    private static final Logger log = LogManager.getLogger();

    /**
     * The resource path to the effect graphics. All graphics need to be located at this path within the JAR-resource
     * files.
     */
    @NotNull
    private static final String EFFECTS_PATH = "effects/";

    /**
     * The assets of the game engine that are required to load the data needed for the effects.
     */
    @NotNull
    private final Assets assets;

    /**
     * Create a new effect loader.
     *
     * @param assets the assets instance of the game engine that is used to load the data
     */
    public EffectLoader(@NotNull Assets assets) {
        this.assets = assets;
    }

    /**
     * Trigger the loading sequence for this loader.
     */
    @NotNull
    @Override
    public ResourceFactory<EffectTemplate> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        ResourceFactory<EffectTemplate> factory = getTargetFactory();

        factory.init();
        new TableLoaderEffects(this);
        factory.loadingFinished();

        loadingDone();

        return factory;
    }

    /**
     * Handle a single line of the resource table.
     */
    @Override
    public boolean processRecord(int line, @NotNull TableLoaderEffects loader) {
        int effectID = loader.getEffectId();
        String name = loader.getResourceName();
        int frames = loader.getFrameCount();
        int offsetX = loader.getOffsetX();
        int offsetY = loader.getOffsetY();
        int speed = loader.getAnimationSpeed();
        int light = loader.getEffectLight();

        Sprite effectSprite = assets.getSpriteFactory()
                .createSprite(getTextures(assets.getTextureManager(), EFFECTS_PATH, name, frames), offsetX, offsetY,
                              SpriteFactory.CENTER, SpriteFactory.CENTER, false);

        EffectTemplate template = new EffectTemplate(effectID, effectSprite, frames, speed, light);
        try {
            getTargetFactory().storeResource(template);
        } catch (IllegalStateException ex) {
            log.error("Failed adding effect to internal factory. ID: {} - Filename: {}", effectID, name);
        }

        return true;
    }
}
