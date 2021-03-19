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
package org.illarion.engine.backend.shared;

import illarion.common.util.ProgressMonitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.graphic.Texture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class TextureAtlasFinalizeTask<T> implements Runnable, TextureAtlasTask {
    /**
     * The logger that provides the logging output of this class.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    private static final class SpriteData {
        String spriteName;
        int posX;
        int posY;
        int width;
        int height;
    }

    @NotNull
    private final FutureTask<T> preLoadTask;
    @NotNull
    private final AbstractTextureManager<T> textureManager;
    @NotNull
    private final String atlasName;
    @NotNull
    private final List<SpriteData> spriteList;
    @NotNull
    private final ProgressMonitor monitor;
    private final float progressToAdd;
    private boolean done;

    public TextureAtlasFinalizeTask(
            @NotNull FutureTask<T> preLoadTask,
            @NotNull String atlasName,
            @NotNull AbstractTextureManager<T> textureManager,
            @NotNull ProgressMonitor monitor,
            float progressToAdd) {
        this.preLoadTask = preLoadTask;
        this.atlasName = atlasName;
        this.textureManager = textureManager;
        this.monitor = monitor;
        this.progressToAdd = progressToAdd;
        spriteList = new ArrayList<>();
        done = false;
    }

    public void addSprite(
            @NotNull String name, int posX, int posY, int width, int height) {
        SpriteData data = new SpriteData();
        data.spriteName = name;
        data.posX = posX;
        data.posY = posY;
        data.width = width;
        data.height = height;
        spriteList.add(data);
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void run() {
        try {
            @Nullable T preLoadData = preLoadTask.get();
            if (preLoadData == null) {
                LOGGER.warn("Failed to load texture data for atlas: {}", atlasName);
            } else {
                Texture atlasTexture = textureManager.loadTexture(atlasName, preLoadData);
                if (atlasTexture != null) {
                    textureManager.addTexture(atlasName, atlasTexture);
                    for (@NotNull SpriteData data : spriteList) {
                        Texture spriteTexture = atlasTexture
                                .getSubTexture(data.posX, data.posY, data.width, data.height);
                        textureManager.addTexture(data.spriteName, spriteTexture);
                    }
                }
            }
            monitor.setProgress(monitor.getProgress() + progressToAdd);
        } catch (@NotNull InterruptedException e) {
            LOGGER.error("Loading thread got interrupted.", e);
        } catch (@NotNull ExecutionException e) {
            LOGGER.error("Failure while loading texture data.", e);
        } finally {
            done = true;
        }
    }
}
