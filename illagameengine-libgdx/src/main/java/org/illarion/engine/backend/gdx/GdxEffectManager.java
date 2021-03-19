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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Files;
import org.illarion.engine.EngineException;
import org.illarion.engine.assets.EffectManager;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.effects.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * The effect manager of the libGDX backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxEffectManager implements EffectManager {
    /**
     * The file system handler used to load the effect data.
     */
    @NotNull
    private final Files files;

    /**
     * The shared instance of the fog effect.
     */
    @Nullable
    private GdxFogEffect sharedFogEffect;

    /**
     * The shared instance of the highlight effect.
     */
    @Nullable
    private GdxHighlightEffect sharedHighlightEffect;

    /**
     * The shared instance of the mini map effect.
     */
    @Nullable
    private GdxMiniMapEffect sharedMiniMapEffect;

    /**
     * The shared instance of the gray scale effect.
     */
    @Nullable
    private GdxGrayScaleEffect sharedGrayScaleEffect;

    /**
     * The shared instance of the tile light effect.
     */
    @Nullable
    private GdxTileLightEffect sharedTileLightEffect;

    /**
     * Create a new effect manager.
     *
     * @param files the file system handler that should be used to load the data
     */
    GdxEffectManager(@NotNull Files files) {
        this.files = files;
    }

    @NotNull
    @Override
    public MiniMapEffect getMiniMapEffect(@NotNull WorldMap worldMap, boolean sharedInstance) throws EngineException {
        if (sharedInstance) {
            if (sharedMiniMapEffect == null) {
                sharedMiniMapEffect = new GdxMiniMapEffect(files, worldMap);
            }
            return sharedMiniMapEffect;
        }
        return new GdxMiniMapEffect(files, worldMap);
    }

    @NotNull
    @Override
    public HighlightEffect getHighlightEffect(boolean sharedInstance) throws EngineException {
        if (sharedInstance) {
            if (sharedHighlightEffect == null) {
                sharedHighlightEffect = new GdxHighlightEffect(files);
            }
            return sharedHighlightEffect;
        }
        return new GdxHighlightEffect(files);
    }

    @NotNull
    @Override
    public FogEffect getFogEffect(boolean sharedInstance) throws EngineException {
        if (sharedInstance) {
            if (sharedFogEffect == null) {
                sharedFogEffect = new GdxFogEffect(files);
            }
            return sharedFogEffect;
        }
        return new GdxFogEffect(files);
    }

    @NotNull
    @Override
    public GrayScaleEffect getGrayScaleEffect(boolean sharedInstance) throws EngineException {
        if (sharedInstance) {
            if (sharedGrayScaleEffect == null) {
                sharedGrayScaleEffect = new GdxGrayScaleEffect(files);
            }
            return sharedGrayScaleEffect;
        }
        return new GdxGrayScaleEffect(files);
    }

    @NotNull
    @Override
    public TileLightEffect getTileLightEffect(boolean sharedInstance) throws EngineException {
        if (sharedInstance) {
            if (sharedTileLightEffect == null) {
                sharedTileLightEffect = new GdxTileLightEffect(files);
            }
            return sharedTileLightEffect;
        }
        return new GdxTileLightEffect(files);
    }
}
