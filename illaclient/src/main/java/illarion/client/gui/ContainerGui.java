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
package illarion.client.gui;

import illarion.client.world.items.ItemContainer;
import org.jetbrains.annotations.NotNull;


/**
 * This interface is used to control the display of item containers on the GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface ContainerGui {
    /**
     * Close the container with the specified ID.
     *
     * @param containerId the ID of the container to close
     */
    void closeContainer(int containerId);

    /**
     * Check if the specified container is currently visible.
     *
     * @param containerId the container to check
     * @return {@code true} in case the container is visible
     */
    boolean isVisible(int containerId);

    /**
     * Create a new container or update a existing container on the GUI and display the specified content.
     *
     * @param container the container to display
     */
    void showContainer(@NotNull ItemContainer container);

    /**
     * Show a tooltip for a specified slot in a specified container.
     *
     * @param containerId the ID of the container the slot belongs to
     * @param slotId the Id of the slot the tooltip belongs to
     * @param tooltip the tooltip
     */
    void showTooltip(int containerId, int slotId, @NotNull Tooltip tooltip);

    /**
     * Perform a update of the overlays related to the merchant dialogs.
     */
    void updateMerchantOverlay();
}
