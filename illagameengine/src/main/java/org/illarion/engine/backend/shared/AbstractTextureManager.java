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

import illarion.common.util.PoolThreadFactory;
import illarion.common.util.ProgressMonitor;
import org.illarion.engine.assets.TextureManager;
import org.illarion.engine.graphic.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the shared code of the texture manager that is used by all backend implementations in a similar way.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public abstract class AbstractTextureManager<T> implements TextureManager {
    private static final Logger log = LoggerFactory.getLogger(AbstractTextureManager.class);

    /**
     * These are the progress monitors for each directory.
     */
    @Nonnull
    private final List<ProgressMonitor> directoryMonitors;

    /**
     * The list of known root directories. This list is used to locate
     */
    @Nonnull
    private final List<String> rootDirectories;

    /**
     * This stores the values if a directory is done loading or currently loading.
     */
    @Nonnull
    private final List<Boolean> directoriesLoaded;

    /**
     * The textures that are known to this manager.
     */
    @Nonnull
    private final Map<String, Texture> textures;

    /**
     * This is the progress monitor that can be used to keep track of the texture atlas loading.
     */
    @Nonnull
    private final ProgressMonitor progressMonitor;

    /**
     * This executor takes care for the tasks required to load the textures properly.
     */
    @Nullable
    private ExecutorService loadingExecutor;

    /**
     * This is a list of loading tasks. Once all entries in this list are cleared, the loading is considered done.
     */
    @Nullable
    private Deque<TextureAtlasTask> loadingTasks;

    /**
     * These are the tasks that are progressed in the graphics context.
     */
    @Nullable
    private Queue<Runnable> updateTasks;

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
    }

    @Override
    public void startLoading() {
        if (isLoadingDone()) {
            return;
        }
        if (loadingExecutor != null) {
            log.warn("Trying to load texture files while loading already in progress.");
            return;
        }

        loadingStarted = true;
        loadingTasks = new ConcurrentLinkedDeque<>();
        updateTasks = new ConcurrentLinkedQueue<>();

        // Prepare the parser factory for processing the XML files
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new IllegalStateException(e);
        }
        parserFactory.setNamespaceAware(false);
        parserFactory.setValidating(false);

        // Loading starts here. Firing up the executor.
        loadingExecutor = Executors.newFixedThreadPool(2, new PoolThreadFactory("TextureLoading", false));
        int directoryCount = rootDirectories.size();
        for (int i = 0; i < directoryCount; i++) {
            if (directoriesLoaded.get(i)) {
                continue;
            }
            String directoryName = rootDirectories.get(i);
            TextureAtlasListXmlLoadingTask<T> task =
                    new TextureAtlasListXmlLoadingTask<>(parserFactory, directoryName, this,
                            directoryMonitors.get(i), loadingExecutor);
            loadingExecutor.execute(task);
            loadingTasks.addFirst(task);
            directoriesLoaded.set(i, Boolean.TRUE);
        }
    }

    @Override
    public void update() {
        if (isLoadingDone()) {
            return;
        }
        if (updateTasks == null) {
            return;
        }

        long startTime = System.currentTimeMillis();

        do {
            Runnable task = updateTasks.poll();
            if (task == null) {
                return;
            }
            task.run();
        } while ((System.currentTimeMillis() - startTime) < 100);
    }

    void addLoadingTask(@Nonnull TextureAtlasTask task) {
        if (loadingTasks != null) {
            loadingTasks.add(task);
        }
    }

    void addUpdateTask(@Nonnull Runnable task) {
        if (updateTasks != null) {
            updateTasks.add(task);
        }
    }

    @Override
    @Nonnull
    public ProgressMonitor getProgress() {
        return progressMonitor;
    }

    @Nonnull
    private static String cleanTextureName(@Nonnull String name) {
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
    @Nonnull
    private static String mergePath(@Nonnull String directory, @Nonnull String name) {
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
    protected abstract T loadTextureData(@Nonnull String textureName);

    @Override
    public final void addTextureDirectory(@Nonnull String directory) {
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
    private int getDirectoryIndex(@Nonnull String directory) {
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
    protected int getFileDirectoryIndex(@Nonnull String name) {
        for (int i = 0; i < rootDirectories.size(); i++) {
            if (name.startsWith(rootDirectories.get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    @Override
    public Texture getTexture(@Nonnull String name) {
        int directoryIndex = getFileDirectoryIndex(name);
        if (directoryIndex >= 0) {
            return getTexture(directoryIndex, name);
        }
        return null;
    }

    @Nullable
    public Texture getTexture(int directoryIndex, @Nonnull String name) {
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

        // We reached a point where everything just turns to be crappy. The file appears to be on a texture atlas that
        // is not yet loaded.
        if (!directoriesLoaded.get(directoryIndex)) {
            String directoryName = rootDirectories.get(directoryIndex);
            log.trace("Start loading directory: {}", directoryName);
            XmlPullParserFactory parserFactory;
            try {
                parserFactory = XmlPullParserFactory.newInstance();
            } catch (XmlPullParserException e) {
                log.error("Failed to create parser factory.", e);
                return null;
            }
            parserFactory.setNamespaceAware(false);
            parserFactory.setValidating(false);

            TextureAtlasListXmlLoadingTask<T> task =
                    new TextureAtlasListXmlLoadingTask<>(parserFactory, directoryName, this,
                            directoryMonitors.get(directoryIndex), null);
            if (loadingTasks == null) {
                loadingTasks = new ConcurrentLinkedDeque<>();
                updateTasks = new ConcurrentLinkedQueue<>();
            }
            loadingTasks.add(task);
            task.run();
            directoriesLoaded.set(directoryIndex, Boolean.TRUE);

            loadingStarted = true;
            while (!isLoadingDone()) {
                update();
            }
            loadingStarted = false;

            log.trace("Loading of directory {} is done.", directoryName);
            Texture result = textures.get(cleanName);
            if (result == null) {
                log.error("Failed to load texture: {} from directory: {}", cleanName, directoryName);
            }
            return result;
        }

        return null;
    }

    @Nullable
    @Override
    public Texture getTexture(@Nonnull String directory, @Nonnull String name) {
        return getTexture(getDirectoryIndex(directory), mergePath(directory, name));
    }

    @Override
    public boolean isLoadingDone() {
        if (!loadingStarted) {
            return false;
        }

        if (loadingTasks == null) {
            return true;
        }

        while (!loadingTasks.isEmpty()) {
            TextureAtlasTask task = loadingTasks.peekFirst();
            if (task.isDone()) {
                loadingTasks.removeFirst();
            } else {
                return false;
            }
        }

        for (@Nonnull ProgressMonitor dirMonitor : directoryMonitors) {
            dirMonitor.setProgress(1.f);
        }

        if (loadingExecutor != null) {
            loadingExecutor.shutdown();
        }
        loadingExecutor = null;
        loadingTasks = null;
        return true;
    }

    /**
     * Load the texture from a specific resource.
     *
     * @param resource the path to the resource
     * @return the texture loaded or {@code null} in case loading is impossible
     */
    @Nullable
    protected abstract Texture loadTexture(@Nonnull String resource, @Nonnull T preLoadData);

    protected void addTexture(@Nonnull String textureName, @Nonnull Texture texture) {
        textures.put(textureName, texture);
    }
}
