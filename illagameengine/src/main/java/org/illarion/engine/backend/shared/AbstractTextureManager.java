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

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import illarion.common.util.PoolThreadFactory;
import illarion.common.util.ProgressMonitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.assets.TextureManager;
import org.illarion.engine.graphic.Texture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.*;
import java.util.concurrent.*;

public abstract class AbstractTextureManager<T> implements TextureManager {
    private static final Logger log = LogManager.getLogger();

    /**
     * These are the progress monitors for each directory.
     */
    @NotNull
    private final List<ProgressMonitor> directoryMonitors;

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

    /**
     * This is the progress monitor that can be used to keep track of the texture atlas loading.
     */
    @NotNull
    private final ProgressMonitor progressMonitor;

    /**
     * This executor takes care for the tasks required to load the textures properly.
     */
    @NotNull
    private final ListeningExecutorService loadingExecutor;

    /**
     * This is a list of loading tasks. Once all entries in this list are cleared, the loading is considered done.
     */
    @NotNull
    private final Deque<Runnable> loadingTasks;

    private boolean loadingStarted;

    /**
     * Creates a new texture loader.
     */
    protected AbstractTextureManager() {
        directoryMonitors = new ArrayList<>();
        rootDirectories = new ArrayList<>();
        textures = new HashMap<>();
        progressMonitor = new ProgressMonitor();
        directoriesLoaded = new ArrayList<>();
        loadingTasks = new ConcurrentLinkedDeque<>();
        loadingExecutor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2, new PoolThreadFactory("TextureLoading", false)));
    }

    @Override
    public void startLoading() {
        if (isLoadingDone()) {
            return;
        }

        // Prepare the parser factory for processing the XML files
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new IllegalStateException(e);
        }
        parserFactory.setNamespaceAware(false);
        parserFactory.setValidating(false);

        // Loading starts here.
        int directoryCount = rootDirectories.size();
        for (int i = 0; i < directoryCount; i++) {
            if (directoriesLoaded.get(i)) {
                continue;
            }
            String directoryName = rootDirectories.get(i);
            TextureAtlasListXmlLoadingTask<T> task =
                    new TextureAtlasListXmlLoadingTask<>(parserFactory, directoryName, this, directoryMonitors.get(i));
            submitLoadingTask(task);
            directoriesLoaded.set(i, Boolean.TRUE);
        }

        loadingStarted = true;
    }

    @Override
    public void update() {
        // Do nothing, tasks will now all be executed once possible
        // Retain update to keep compatibility with editor
    }

    void submitQueuedTasks() {
        while (!loadingTasks.isEmpty()) {
            submitLoadingTask(loadingTasks.poll());
        }
    }

    void submitLoadingTask(Runnable task) {
        loadingExecutor.submit(task).addListener(this::submitQueuedTasks, loadingExecutor);
    }

    void addLoadingTask(Runnable task) {
        if (loadingTasks.isEmpty()) {
            submitLoadingTask(task);
        } else {
            loadingTasks.add(task);
        }
    }

    @Override
    @NotNull
    public ProgressMonitor getProgress() {
        return progressMonitor;
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
        ProgressMonitor dirProgressMonitor = new ProgressMonitor();
        directoryMonitors.add(dirProgressMonitor);
        progressMonitor.addChild(dirProgressMonitor);
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
    protected int getFileDirectoryIndex(@NotNull String name) {
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

        if (log.isTraceEnabled()) {
            log.trace("Texture {} requested from directory: {}", name, rootDirectories.get(directoryIndex));
        }

        String cleanName = cleanTextureName(name);

        // Checking if the texture is among already loaded textures.
        @Nullable Texture loadedTexture = textures.get(cleanName);
        if (loadedTexture != null) {
            log.trace("Found texture {} among the already loaded texture.", cleanName);
            return loadedTexture;
        }

        // Checking if the texture is located on a separated file.
        @Nullable T preLoadTextureData = loadTextureData(cleanName + ".png");
        if (preLoadTextureData != null) {
            @Nullable Texture directTexture = loadTexture(cleanName + ".png", preLoadTextureData);
            if (directTexture != null) {
                log.trace("Fetched texture {} by direct name.", cleanName);
                textures.put(cleanName, directTexture);
                return directTexture;
            }
        }

        // The file appears to be on a texture atlas that is not yet loaded.
        // No longer try a reload during runtime, as this is would be a core bug, to be handled in the initial loading
        if (!directoriesLoaded.get(directoryIndex)) {
            log.error("Tried to load a file {} whose texture atlas (directory index: {}) have not been loaded.", name, directoryIndex);
        }

        return null;
    }

    @Nullable
    @Override
    public Texture getTexture(@NotNull String directory, @NotNull String name) {
        return getTexture(getDirectoryIndex(directory), mergePath(directory, name));
    }

    @Override
    public boolean isLoadingDone() {
        return loadingStarted && loadingTasks.isEmpty();
    }

    /**
     * Load the texture from a specific resource.
     *
     * @param resource the path to the resource
     * @return the texture loaded or {@code null} in case loading is impossible
     */
    @Nullable
    protected abstract Texture loadTexture(@NotNull String resource, @NotNull T preLoadData);

    protected void addTexture(@NotNull String textureName, @NotNull Texture texture) {
        textures.put(textureName, texture);
    }
}
