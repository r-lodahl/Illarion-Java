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
import com.badlogic.gdx.math.Vector2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.effects.TileLightEffect;
import org.jetbrains.annotations.NotNull;


/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class GdxTileLightEffect implements TileLightEffect, GdxTextureEffect {
    @NotNull
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Uniform shader variable name for the top left light.
     */
    @NotNull
    private static final String UNIFORM_TOP_LEFT = "u_topLeft";

    /**
     * Uniform shader variable name for the top right light.
     */
    @NotNull
    private static final String UNIFORM_TOP_RIGHT = "u_topRight";

    /**
     * Uniform shader variable name for the bottom left light.
     */
    @NotNull
    private static final String UNIFORM_BOTTOM_LEFT = "u_bottomLeft";

    /**
     * Uniform shader variable name for the bottom right light.
     */
    @NotNull
    private static final String UNIFORM_BOTTOM_RIGHT = "u_bottomRight";

    /**
     * Uniform shader variable name for the top light.
     */
    @NotNull
    private static final String UNIFORM_TOP = "u_top";

    /**
     * Uniform shader variable name for the bottom light.
     */
    @NotNull
    private static final String UNIFORM_BOTTOM = "u_bottom";

    /**
     * Uniform shader variable name for the left light.
     */
    @NotNull
    private static final String UNIFORM_LEFT = "u_left";

    /**
     * Uniform shader variable name for the right light.
     */
    @NotNull
    private static final String UNIFORM_RIGHT = "u_right";

    /**
     * Uniform shader variable name for the center light.
     */
    @NotNull
    private static final String UNIFORM_CENTER = "u_center";

    /**
     * Uniform shader variable name for the top left coordinates.
     */
    @NotNull
    private static final String UNIFORM_TOP_LEFT_COORDS = "u_topLeftCoords";

    /**
     * Uniform shader variable name for the bottom right coordinates.
     */
    @NotNull
    private static final String UNIFORM_BOTTOM_RIGHT_COORDS = "u_bottomRightCoords";

    /**
     * The pixel shader that is required for this effect.
     */
    @NotNull
    private final ShaderProgram shader;

    @NotNull
    private final com.badlogic.gdx.graphics.Color topLeft;
    @NotNull
    private final com.badlogic.gdx.graphics.Color topRight;
    @NotNull
    private final com.badlogic.gdx.graphics.Color bottomLeft;
    @NotNull
    private final com.badlogic.gdx.graphics.Color bottomRight;
    @NotNull
    private final com.badlogic.gdx.graphics.Color top;
    @NotNull
    private final com.badlogic.gdx.graphics.Color bottom;
    @NotNull
    private final com.badlogic.gdx.graphics.Color left;
    @NotNull
    private final com.badlogic.gdx.graphics.Color right;
    @NotNull
    private final com.badlogic.gdx.graphics.Color center;
    @NotNull
    private final Vector2 topLeftCoord;
    @NotNull
    private final Vector2 bottomRightCoord;

    GdxTileLightEffect(@NotNull Files files) {
        //noinspection SpellCheckingInspection
        shader = new ShaderProgram(files.internal("org/illarion/engine/backend/gdx/shaders/generic.vert"),
                                   files.internal("org/illarion/engine/backend/gdx/shaders/tileLight.frag"));

        if (!shader.isCompiled()) {
            LOGGER.error("Compiling shader failed: {}", shader.getLog());
        }

        topLeft = new com.badlogic.gdx.graphics.Color();
        topRight = new com.badlogic.gdx.graphics.Color();
        bottomLeft = new com.badlogic.gdx.graphics.Color();
        bottomRight = new com.badlogic.gdx.graphics.Color();
        top = new com.badlogic.gdx.graphics.Color();
        bottom = new com.badlogic.gdx.graphics.Color();
        left = new com.badlogic.gdx.graphics.Color();
        right = new com.badlogic.gdx.graphics.Color();
        center = new com.badlogic.gdx.graphics.Color();
        topLeftCoord = new Vector2();
        bottomRightCoord = new Vector2();
    }

    @Override
    public void setTopLeftColor(@NotNull Color color) {
        GdxGraphics.transferColor(color, topLeft);
    }

    @Override
    public void setTopRightColor(@NotNull Color color) {
        GdxGraphics.transferColor(color, topRight);
    }

    @Override
    public void setBottomLeftColor(@NotNull Color color) {
        GdxGraphics.transferColor(color, bottomLeft);
    }

    @Override
    public void setBottomRightColor(@NotNull Color color) {
        GdxGraphics.transferColor(color, bottomRight);
    }

    @Override
    public void setTopColor(@NotNull Color color) {
        GdxGraphics.transferColor(color, top);
    }

    @Override
    public void setBottomColor(@NotNull Color color) {
        GdxGraphics.transferColor(color, bottom);
    }

    @Override
    public void setLeftColor(@NotNull Color color) {
        GdxGraphics.transferColor(color, left);
    }

    @Override
    public void setRightColor(@NotNull Color color) {
        GdxGraphics.transferColor(color, right);
    }

    @Override
    public void setCenterColor(@NotNull Color color) {
        GdxGraphics.transferColor(color, center);
    }

    @Override
    public void activateEffect(@NotNull SpriteBatch batch) {
        if (shader.isCompiled()) {
            batch.setShader(shader);
            setUniform(shader, UNIFORM_TOP_LEFT, topLeft);
            setUniform(shader, UNIFORM_TOP_RIGHT, topRight);
            setUniform(shader, UNIFORM_BOTTOM_LEFT, bottomLeft);
            setUniform(shader, UNIFORM_BOTTOM_RIGHT, bottomRight);
            setUniform(shader, UNIFORM_TOP, top);
            setUniform(shader, UNIFORM_BOTTOM, bottom);
            setUniform(shader, UNIFORM_LEFT, left);
            setUniform(shader, UNIFORM_RIGHT, right);
            setUniform(shader, UNIFORM_CENTER, center);
            setUniform(shader, UNIFORM_TOP_LEFT_COORDS, topLeftCoord);
            setUniform(shader, UNIFORM_BOTTOM_RIGHT_COORDS, bottomRightCoord);
        }
    }

    private static void setUniform(
            @NotNull ShaderProgram shader, @NotNull String name, @NotNull com.badlogic.gdx.graphics.Color color) {
        if (shader.hasUniform(name)) {
            shader.setUniformf(name, color);
        }
    }

    private static void setUniform(@NotNull ShaderProgram shader, @NotNull String name, @NotNull Vector2 vector2) {
        if (shader.hasUniform(name)) {
            shader.setUniformf(name, vector2);
        }
    }

    @Override
    public void disableEffect(@NotNull SpriteBatch batch) {
        batch.setShader(null);
    }

    @Override
    public void setTopLeftCoordinate(float x, float y) {
        topLeftCoord.set(x, y);
    }

    @Override
    public void setBottomRightCoordinate(float x, float y) {
        bottomRightCoord.set(x, y);
    }
}
