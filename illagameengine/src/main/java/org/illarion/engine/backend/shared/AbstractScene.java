/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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

import illarion.common.memory.MemoryPools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.BackendBinding;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.Scene;
import org.illarion.engine.graphic.SceneElement;
import org.illarion.engine.graphic.SceneEvent;
import org.illarion.engine.graphic.effects.SceneEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is the abstract implementation of a scene that takes care for the sorting and storing of the scene elements
 * as this is the same for all the implementations.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractScene<T extends SceneEffect> implements Scene, Comparator<SceneElement> {
    /**
     * The logger of this class.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * This list of elements in the scene. This list is kept sorted.
     */
    @NotNull
    private final List<SceneElement> sceneElements;

    /**
     * This is the queue of events that are published during the updates.
     */
    @NotNull
    private final Queue<SceneEvent> eventQueue;

    /**
     * The list of effects applied to this scene.
     */
    @NotNull
    private final List<T> sceneEffects;

    /**
     * This is the snapshot array that is taken and filled shortly before the update calls. Is then used to render
     * and update the scene.
     */
    @Nullable
    private SceneElement[] workingArray;

    /**
     * Create a new scene and setup the internal structures.
     */
    protected AbstractScene() {
        sceneElements = new ArrayList<>();
        eventQueue = new ConcurrentLinkedQueue<>();
        sceneEffects = new ArrayList<>();
    }

    @Nullable
    private static <T> T getFromArray(@NotNull T[] array, int index) {
        if (index >= array.length) {
            return null;
        }
        return array[index];
    }

    @Override
    public int compare(@NotNull SceneElement o1, @NotNull SceneElement o2) {
        return Integer.compare(o2.getOrder(), o1.getOrder());
    }

    @Override
    public final void addElement(@NotNull SceneElement element) {
        synchronized (sceneElements) {
            int insertIndex = Collections.binarySearch(sceneElements, element, this);
            if (insertIndex < 0) {
                sceneElements.add(-insertIndex - 1, element);
            } else {
                sceneElements.add(insertIndex, element);
            }
        }
    }

    @Override
    public final void updateElementLocation(@NotNull SceneElement element) {
        synchronized (sceneElements) {
            // If element is not found, insertIndex = (where the element should be added * -1) - 1
            int insertIndex = Collections.binarySearch(sceneElements, element, this);
            // If the item wasn't found, set checkIndex = the proper location
            int checkIndex = (insertIndex < 0) ? ((insertIndex + 1) * -1) : insertIndex;
            // If checkIndex is outside our ArrayList, set it to the last element
            checkIndex = (checkIndex >= sceneElements.size()) ? (checkIndex = sceneElements.size() - 1) : checkIndex;
            SceneElement testElement = sceneElements.get(checkIndex);
            if (!Objects.equals(testElement, element)) {
                removeElement(element);
                addElement(element);
            }
        }
    }

    @Override
    public final void removeElement(@NotNull SceneElement element) {
        synchronized (sceneElements) {
            sceneElements.remove(element);
        }
    }

    /**
     * This function performs the actual calling of the update functions for all scene elements.
     *
     * @param delta the time since the last update that is reported to the elements
     */
    protected final void updateScene(BackendBinding binding, int delta) {
        SceneElement[] sceneElementArray = workingArray;
        int sceneElementCount;
        synchronized (sceneElements) {
            sceneElementCount = sceneElements.size();
            if (sceneElementArray == null) {
                sceneElementArray = new SceneElement[sceneElementCount];
            }
            sceneElementArray = sceneElements.toArray(sceneElementArray);
        }
        workingArray = sceneElementArray;

        @Nullable SceneEvent event;
        while ((event = eventQueue.poll()) != null) {
            boolean processed = false;
            for (int i = sceneElementCount - 1; i >= 0; i--) {
                SceneElement element = sceneElementArray[i];
                if (element.isEventProcessed(binding, delta, event)) {
                    processed = true;
                    break;
                }
            }
            if (!processed) {
                event.notHandled();
            }

            // Recycle event so it can be reused
            MemoryPools.free(event);
        }

        for (int i = 0; i < sceneElementCount; i++) {
            SceneElement element = workingArray[i];
            element.update(binding, delta);
        }
    }

    /**
     * This function performs the actual render operation for all elements of the scene.
     *
     * @param graphics the graphics instance that is used to render the game
     */
    protected final void renderScene(@NotNull Graphics graphics) {
        SceneElement[] sceneElementArray = workingArray;
        if (sceneElementArray == null) {
            return;
        }

        int i = 0;
        SceneElement element;
        while ((element = getFromArray(sceneElementArray, ++i)) != null) {
            element.render(graphics);
        }
    }

    @Override
    public final void publishEvent(@NotNull SceneEvent event) {
        eventQueue.offer(event);
    }

    @Override
    public void addEffect(@NotNull SceneEffect effect) {
        try {
            @SuppressWarnings("unchecked") T sceneEffect = (T) effect;
            if (!sceneEffects.contains(sceneEffect)) {
                sceneEffects.add(sceneEffect);
            }
        } catch (ClassCastException e) {
            // illegal type
        }
    }

    @Override
    public void removeEffect(@NotNull SceneEffect effect) {
        try {
            @SuppressWarnings("unchecked") T sceneEffect = (T) effect;
            sceneEffects.remove(sceneEffect);
        } catch (ClassCastException e) {
            // illegal type
        }
    }

    @Override
    public void clearEffects() {
        sceneEffects.clear();
    }

    @Override
    public int getElementCount() {
        return sceneElements.size();
    }

    /**
     * Get a scene effect applied to a specific image.
     *
     * @param index the index of the effect
     * @return the scene effect
     */
    @NotNull
    protected final T getEffect(int index) {
        return Objects.requireNonNull(sceneEffects.get(index));
    }

    /**
     * Get the amount of scene effects allowed.
     *
     * @return the scene effects
     */
    protected final int getEffectCount() {
        return sceneEffects.size();
    }
}
