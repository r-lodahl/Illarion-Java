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
package org.illarion.engine.graphic;

import org.illarion.engine.BackendBinding;
import org.illarion.engine.graphic.effects.SceneEffect;
import org.jetbrains.annotations.NotNull;


/**
 * This class represents a 2D scene that is rendered to the screen. This should be used to render the games graphics.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Scene {
    /**
     * Add a element to this scene.
     *
     * @param element the element to add
     */
    void addElement(@NotNull SceneElement element);

    /**
     * This needs to be called for a existing element of the scene. This function has to ensure that the render order
     * for this element is correct. This function should be called in case the element changes its location in the
     * scene.
     *
     * @param element the element to check
     */
    void updateElementLocation(@NotNull SceneElement element);

    /**
     * Remove a element from the scene.
     *
     * @param element the element to remove
     */
    void removeElement(@NotNull SceneElement element);

    /**
     * Update the scene.
     *
     * @param delta the time since the last update
     */
    void update(@NotNull BackendBinding binding, int delta);

    /**
     * This function is called to render the scene. It does so by calling the {@link SceneElement#render(Graphics)}
     * function.
     *
     * @param graphics the graphics instance that is supposed to be used to render the element
     * @param offsetX the x coordinate of the offset that is applied to all rendered elements
     * @param offsetY the y coordinate of the offset that is applied to all rendered elements
     */
    void render(@NotNull Graphics graphics, int offsetX, int offsetY);

    /**
     * This function publishes events to the scene. The actual publishing is done during the call of the
     * {@link #update(BackendBinding, int)} function. This method is thread save.
     *
     * @param event the event to publish
     */
    void publishEvent(@NotNull SceneEvent event);

    /**
     * Add a effect to the scene.
     *
     * @param effect the effect applied to the scene
     */
    void addEffect(@NotNull SceneEffect effect);

    /**
     * Remove a effect from the scene.
     *
     * @param effect the effect to remove
     */
    void removeEffect(@NotNull SceneEffect effect);

    /**
     * Remove all effects from the scene.
     */
    void clearEffects();

    /**
     * Get the amount of elements in the scene.
     *
     * @return the amount of elements
     */
    int getElementCount();
}
