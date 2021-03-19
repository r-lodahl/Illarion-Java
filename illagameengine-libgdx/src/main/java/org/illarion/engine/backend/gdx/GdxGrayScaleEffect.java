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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import org.illarion.engine.graphic.effects.GrayScaleEffect;
import org.jetbrains.annotations.NotNull;


/**
 * This is the libGDX implementation of the gray scale effect.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxGrayScaleEffect implements GrayScaleEffect, GdxSceneEffect, GdxTextureEffect {
    /**
     * The pixel shader that is required for this effect.
     */
    @NotNull
    private final ShaderProgram shader;

    GdxGrayScaleEffect(@NotNull Files files) {
        //noinspection SpellCheckingInspection
        shader = new ShaderProgram(files.internal("org/illarion/engine/backend/gdx/shaders/generic.vert"),
                                   files.internal("org/illarion/engine/backend/gdx/shaders/grayScale.frag"));
    }

    @Override
    public void update(int delta) {
        // nothing to do
    }

    @Override
    public void activateEffect(
            @NotNull SpriteBatch batch, int screenWidth, int screenHeight, int textureWidth, int textureHeight) {
        batch.setShader(shader);
    }

    @Override
    public void activateEffect(@NotNull SpriteBatch batch) {
        batch.setShader(shader);
    }

    @Override
    public void disableEffect(@NotNull SpriteBatch batch) {
        batch.setShader(null);
    }

    @Override
    public void setTopLeftCoordinate(float x, float y) {
    }

    @Override
    public void setBottomRightCoordinate(float x, float y) {
    }
}
