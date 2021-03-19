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
package org.illarion.engine.input;


import org.jetbrains.annotations.NotNull;

/**
 * This is the interface of the input implementation.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Input {
    /**
     * This function should be called at least once during the updates to forward the input events to the application.
     */
    void poll();

    /**
     * Set the listener that is supposed to receive the input events.
     *
     * @param listener the input event listener
     */
    void setListener(@NotNull InputListener listener);

    /**
     * Check if the specified button is currently pressed down.
     *
     * @param button the button
     * @return {@code true} if the button is currently down
     */
    boolean isButtonDown(@NotNull Button button);

    /**
     * Check if the key is currently pressed down.
     *
     * @param key the key
     * @return {@code true} in case the key is pressed
     */
    boolean isKeyDown(@NotNull Key key);

    /**
     * Check if any mouse button is currently pressed.
     *
     * @return {@code true} if any mouse button is pressed
     */
    boolean isAnyButtonDown();

    /**
     * Check if any of the listed mouse buttons is pressed.
     *
     * @param buttons the list of mouse buttons to check
     * @return {@code true} if at least one of the buttons is pressed
     */
    boolean isAnyButtonDown(@NotNull Button... buttons);

    /**
     * Check if any keyboard key is pressed.
     *
     * @return {@code true} in case any keyboard key is pressed
     */
    boolean isAnyKeyDown();

    /**
     * Check if one of the listed keyboard keys is pressed.
     *
     * @param keys the keys to check
     * @return {@code true} in case one of those keys is pressed
     */
    boolean isAnyKeyDown(@NotNull Key... keys);

    /**
     * Get the X coordinate of the current mouse location.
     *
     * @return the X coordinate of the current mouse location
     */
    int getMouseX();

    /**
     * Get the Y coordinate of the current mouse location.
     *
     * @return the Y coordinate of the current mouse location
     */
    int getMouseY();

    /**
     * Set the location of the mouse cursor.
     *
     * @param x the x coordinate of the mouse cursor
     * @param y the y coordinate of the mouse cursor
     */
    void setMouseLocation(int x, int y);

    /**
     * Check if forwarding is enabled for the specified target.
     *
     * @param target the forwarding target
     * @return {@code true} in case forwarding is enabled for this target
     */
    boolean isForwardingEnabled(@NotNull ForwardingTarget target);

    /**
     * Enable forwarding for the specified target.
     *
     * @param target the forwarding target that is supposed to be enabled
     */
    void enableForwarding(@NotNull ForwardingTarget target);

    /**
     * Disable the forwarding for the specified target.
     *
     * @param target the forwarding target that is supposed to be disabled
     */
    void disableForwarding(@NotNull ForwardingTarget target);

    /**
     * Add a listener that monitors the forwarding state.
     *
     * @param listener the new forwarding listener
     */
    void addForwardingListener(@NotNull ForwardingListener listener);
}
