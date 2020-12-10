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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import org.illarion.engine.BackendBinding;
import org.illarion.engine.Window;
import org.illarion.engine.backend.shared.AbstractScene;
import org.illarion.engine.graphic.Graphics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.Binding;

/**
 * This is the scene implementation for libGDX.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxScene extends AbstractScene<GdxSceneEffect> {
    /**
     * The first of two frame buffers that can be used to buffer the scene to apply the post processing effects.
     */
    @Nullable
    private FrameBuffer processImage0;

    /**
     * The second of two frame buffers that can be used to buffer the scene to apply the post processing effects.
     */
    @Nullable
    private FrameBuffer processImage1;

    /**
     * The last frame buffer that was requested. This variable is required to flip-flop the textures.
     */
    private int lastFrameBuffer = 1;

    /**
     * The camera that is used to render the scene.
     */
    @Nonnull
    private final OrthographicCamera camera;

    /**
     * The window reference to the backend.
     */
    @Nonnull
    private final Window window;

    /**
     * Create a new render scene for libGDX.
     *
     */
    GdxScene(@Nonnull Window window) {
        this.window = window;
        camera = new OrthographicCamera();
        camera.setToOrtho(true);
    }

    @Override
    public void update(@Nonnull BackendBinding binding, int delta) {
        updateScene(binding, delta);

        int effectCount = getEffectCount();
        for (int i = 0; i < effectCount; i++) {
            getEffect(i).update(delta);
        }

        camera.setToOrtho(true, window.getWidth(), window.getHeight());
        camera.update();
    }

    @Override
    public void render(@Nonnull Graphics graphics, int offsetX, int offsetY) {
        if (!(graphics instanceof GdxGraphics)) {
            throw new IllegalArgumentException("Illegal graphics implementation.");
        }

        GdxGraphics gdxGraphics = (GdxGraphics) graphics;

        int effectCount = getEffectCount();
        if (effectCount == 0) {
            gdxGraphics.applyOffset(offsetX, offsetY);
            renderScene(graphics);
            gdxGraphics.resetOffset();
        } else {
            gdxGraphics.endFrame();
            FrameBuffer currentFrameBuffer = getNextFrameBuffer(window.getWidth(), window.getHeight());
            currentFrameBuffer.begin();
            gdxGraphics.beginFrame();
            gdxGraphics.applyOffset(offsetX, offsetY);
            renderScene(graphics);
            gdxGraphics.resetOffset();
            gdxGraphics.flushAll();
            currentFrameBuffer.end();

            SpriteBatch renderBatch = gdxGraphics.getSpriteBatch();
            renderBatch.setProjectionMatrix(camera.combined);
            renderBatch.setColor(Color.WHITE);
            FrameBuffer lastFrameBuffer = currentFrameBuffer;
            for (int i = 0; i < effectCount; i++) {
                currentFrameBuffer = getNextFrameBuffer(window.getWidth(), window.getHeight());
                currentFrameBuffer.begin();
                renderBatch.begin();
                GdxSceneEffect effect = getEffect(i);
                effect.activateEffect(renderBatch, window.getWidth(), window.getHeight(),
                                      currentFrameBuffer.getColorBufferTexture().getWidth(),
                                      currentFrameBuffer.getColorBufferTexture().getHeight());
                renderBatch.draw(lastFrameBuffer.getColorBufferTexture(), 0.f, 0.f);
                effect.disableEffect(renderBatch);
                renderBatch.end();
                currentFrameBuffer.end();
                lastFrameBuffer = currentFrameBuffer;
            }

            renderBatch.setColor(Color.WHITE);
            renderBatch.setProjectionMatrix(camera.combined);
            renderBatch.begin();
            renderBatch.draw(lastFrameBuffer.getColorBufferTexture(), 0.f, 0.f);
            renderBatch.end();
        }
    }

    /**
     * Get the next frame buffer that can be used as render target.
     *
     * @param width the width the image is supposed to have
     * @param height the height the image is supposed to have
     * @return the image that can be used now
     */
    @Nonnull
    private FrameBuffer getNextFrameBuffer(int width, int height) {
        if (lastFrameBuffer == 1) {
            processImage0 = validateFrameBuffer(width, height, processImage0);
            lastFrameBuffer = 0;
            return processImage0;
        }
        processImage1 = validateFrameBuffer(width, height, processImage1);
        lastFrameBuffer = 1;
        return processImage1;
    }

    /**
     * Check if a frame buffer is fitting the requirements.
     *
     * @param width the width the image needs to have
     * @param height the height the image needs to have
     * @param original the original image, if this is {@code null} a new image will be created
     * @return the image fitting the requirements
     */
    @Nonnull
    private static FrameBuffer validateFrameBuffer(
            int width, int height, @Nullable FrameBuffer original) {
        if (original == null) {
            return new FrameBuffer(Format.RGBA8888, width, height, false);
        }
        if ((original.getHeight() == height) && (original.getWidth() == width)) {
            return original;
        }
        original.dispose();
        return new FrameBuffer(Format.RGBA8888, width, height, false);
    }
}
