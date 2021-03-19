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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import org.illarion.engine.EngineException;
import org.illarion.engine.Window;
import org.illarion.engine.assets.*;
import org.illarion.engine.graphic.Scene;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.WorldMapDataProvider;
import org.jetbrains.annotations.NotNull;


/**
 * This is the asset manager that fetches its data from libGDX.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxAssets implements Assets {
    /**
     * The texture manager that is used.
     */
    @NotNull
    private final GdxTextureManager textureManager;

    /**
     * The font manager that is used.
     */
    @NotNull
    private final GdxFontManager fontManager;

    /**
     * The cursor manager used to load the mouse cursors.
     */
    @NotNull
    private final CursorManager cursorManager;

    /**
     * The sounds manager used to load and store the sound effects.
     */
    @NotNull
    private final GdxSoundsManager soundsManager;

    /**
     * The sprite factory of the libGDX backend.
     */
    @NotNull
    private final GdxSpriteFactory spriteFactory;

    /**
     * The effect manager that creates the graphic effects.
     */
    @NotNull
    private final GdxEffectManager effectManager;

    /**
     * The backends window reference.
     */
    @NotNull
    private final Window window;

    /**
     * Create a new instance of the libGDX assets management.
     */
    GdxAssets(Graphics graphics, Files files, Audio audio, @NotNull Window window) {
        textureManager = new GdxTextureManager();
        fontManager = new GdxFontManager(files, textureManager);
        cursorManager = new GdxCursorManager(graphics, files);
        soundsManager = new GdxSoundsManager(files, audio);
        spriteFactory = new GdxSpriteFactory();
        effectManager = new GdxEffectManager(files);
        this.window = window;
    }

    @NotNull
    @Override
    public GdxTextureManager getTextureManager() {
        return textureManager;
    }

    @NotNull
    @Override
    public FontManager getFontManager() {
        return fontManager;
    }

    @NotNull
    @Override
    public CursorManager getCursorManager() {
        return cursorManager;
    }

    @NotNull
    @Override
    public SoundsManager getSoundsManager() {
        return soundsManager;
    }

    @NotNull
    @Override
    public GdxSpriteFactory getSpriteFactory() {
        return spriteFactory;
    }

    @NotNull
    @Override
    public Scene createNewScene() {
        return new GdxScene(window);
    }

    @NotNull
    @Override
    public WorldMap createWorldMap(@NotNull WorldMapDataProvider provider) throws EngineException {
        return new GdxWorldMap(provider);
    }

    @NotNull
    @Override
    public EffectManager getEffectManager() {
        return effectManager;
    }
}
