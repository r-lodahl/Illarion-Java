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
package org.illarion.engine.backend.shared;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.assets.TextureManager;
import org.illarion.engine.graphic.Texture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public abstract class AbstractTextureManager<T> implements TextureManager {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The list of known root directories. This list is used to locate
     */
    @NotNull
    private final List<String> rootDirectories;

    /**
     * This stores the values if a directory is done loading or currently loading.
     */
    @NotNull
    private final List<Boolean> directoriesLoaded;

    /**
     * The textures that are known to this manager.
     */
    @NotNull
    private final Map<String, Texture> textures;

    private boolean completeLoadingTriggered;

    /**
     * Creates a new texture loader.
     */
    protected AbstractTextureManager() {
        rootDirectories = new ArrayList<>();
        textures = new HashMap<>();
        directoriesLoaded = new ArrayList<>();
    }

    @Override
    public void loadAll(Executor executor) {
        if (completeLoadingTriggered) {
            return;
        }

        completeLoadingTriggered = true;

        // Prepare the parser factory for processing the XML files
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new IllegalStateException(e);
        }
        parserFactory.setNamespaceAware(false);
        parserFactory.setValidating(false);

        int directoryCount = rootDirectories.size();

        var parsedXmlDatas = new ArrayList<CompletableFuture<List<TextureAtlasXmlParserTask<T>.AtlasData>>>();

        // Loading starts here.
        for (int i = 0; i < directoryCount; i++) {
            if (directoriesLoaded.get(i)) {
                continue;
            }
            String directoryName = rootDirectories.get(i);
            var task = new TextureAtlasXmlParserTask<>(parserFactory, directoryName, this);

            parsedXmlDatas.add(CompletableFuture.supplyAsync(task, executor));
            directoriesLoaded.set(i, Boolean.TRUE);
        }

        for (var parsedXmlData : parsedXmlDatas) {
            try {
                loadTextureAtlasFromParsedXml(parsedXmlData.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Could not retrieve parsed xml atlas information while loading.");
            }
        }
    }

    private void loadTextureAtlasFromParsedXml(Iterable<TextureAtlasXmlParserTask<T>.AtlasData> atlasDatas) {
        for (var atlasData : atlasDatas) {
            var atlas = loadTexture(atlasData.atlasName, atlasData.textureData);

            if (atlas == null) {
                continue;
            }

            addTexture(atlasData.atlasName, atlas);

            for (var spriteData : atlasData.spriteList) {
                addTexture(
                        spriteData.spriteName(),
                        atlas.getSubTexture(
                                spriteData.posX(),
                                spriteData.posY(),
                                spriteData.width(),
                                spriteData.height()));
            }
        }
    }

    @NotNull
    private static String cleanTextureName(@NotNull String name) {
        if (name.endsWith(".png")) {
            return name.substring(0, name.length() - 4);
        }
        return name;
    }

    /**
     * Combine the path from a directory and the name.
     *
     * @param directory the directory
     * @param name the name of the new path element
     * @return the full path, properly merged
     */
    @NotNull
    private static String mergePath(@NotNull String directory, @NotNull String name) {
        if (directory.endsWith("/")) {
            return directory + name;
        }
        return directory + '/' + name;
    }

    /**
     * This function is supposed to load the texture data as far as possible outside of the graphics context thread.
     * Its very likely that the thread calling this function does not have the graphics context. The data prepared here
     * will be provided along with the final texture loading call.
     *
     * @param textureName the name of the texture file
     * @return the texture data
     */
    @Nullable
    protected abstract T loadTextureData(@NotNull String textureName);

    @Override
    public final void addTextureDirectory(@NotNull String directory) {
        rootDirectories.add(directory);
        directoriesLoaded.add(Boolean.FALSE);
    }

    /**
     * Get the index of a root directory.
     *
     * @param directory the directory
     * @return the index of the root directory or {@code -1} in case the directory could not be assigned to a root
     * directory
     */
    private int getDirectoryIndex(@NotNull String directory) {
        if (directory.endsWith("/")) {
            return rootDirectories.indexOf(directory.substring(0, directory.length() - 1));
        }
        return rootDirectories.indexOf(directory);
    }

    /**
     * Get the directory index for a file. The expected file name should start with the name of the directory.
     *
     * @param name the name of the file
     * @return the index of the directory or {@code -1} in case there is not matching directory
     */
    private int getFileDirectoryIndex(@NotNull String name) {
        for (int i = 0; i < rootDirectories.size(); i++) {
            if (name.startsWith(rootDirectories.get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    @Override
    public Texture getTexture(@NotNull String name) {
        int directoryIndex = getFileDirectoryIndex(name);
        if (directoryIndex >= 0) {
            return getTexture(directoryIndex, name);
        }
        return null;
    }

    @Nullable
    public Texture getTexture(int directoryIndex, @NotNull String name) {
        if (directoryIndex == -1) {
            return null;
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Texture {} requested from directory: {}", name, rootDirectories.get(directoryIndex));
        }

        String cleanName = cleanTextureName(name);

        // Checking if the texture is among already loaded textures.
        @Nullable Texture loadedTexture = textures.get(cleanName);
        if (loadedTexture != null) {
            LOGGER.trace("Found texture {} among the already loaded texture.", cleanName);
            return loadedTexture;
        }

        // Checking if the texture is located on a separated file.
        @Nullable T preLoadTextureData = loadTextureData(cleanName + ".png");
        if (preLoadTextureData != null) {
            @Nullable Texture directTexture = loadTexture(cleanName + ".png", preLoadTextureData);
            if (directTexture != null) {
                LOGGER.trace("Fetched texture {} by direct name.", cleanName);
                textures.put(cleanName, directTexture);
                return directTexture;
            }
        }

        // The file appears to be on a texture atlas that is not yet loaded.
        // No longer try a reload during runtime, as this is would be a core bug, to be handled in the initial loading
        if (!directoriesLoaded.get(directoryIndex) && !completeLoadingTriggered) {
            LOGGER.error("Tried to load a file {} whose texture atlas (directory index: {}) have not been loaded.", name, directoryIndex);

            String directoryName = rootDirectories.get(directoryIndex);
            directoriesLoaded.set(directoryIndex, Boolean.TRUE);

            LOGGER.trace("Started loading of directory {}.", directoryName);

            XmlPullParserFactory parserFactory;

            try {
                parserFactory = XmlPullParserFactory.newInstance();
            } catch (XmlPullParserException e) {
                LOGGER.error("Failed to create parser factory.", e);
                return null;
            }

            parserFactory.setNamespaceAware(false);
            parserFactory.setValidating(false);

            var task = new TextureAtlasXmlParserTask<>(parserFactory, directoryName, this);
            var atlasDatas = task.get();

            loadTextureAtlasFromParsedXml(atlasDatas);

            LOGGER.trace("Loading of directory {} is done.", directoryName);

            Texture result = textures.get(cleanName);

            if (result == null) {
                LOGGER.error("Failed to load texture: {} from directory: {}", cleanName, directoryName);
            }

            return result;
        }

        LOGGER.error(
                "Missing file {} for atlas with directory id {}, although loading is already finished.",
                name,
                directoryIndex);

        return null;
    }

    @Nullable
    @Override
    public Texture getTexture(@NotNull String directory, @NotNull String name) {
        return getTexture(getDirectoryIndex(directory), mergePath(directory, name));
    }

    /**
     * Load the texture from a specific resource.
     *
     * @param resource the path to the resource
     * @return the texture loaded or {@code null} in case loading is impossible
     */
    @Nullable
    protected abstract Texture loadTexture(@NotNull String resource, @NotNull T preLoadData);

    private void addTexture(@NotNull String textureName, @NotNull Texture texture) {
        textures.put(textureName, texture);
    }
}
