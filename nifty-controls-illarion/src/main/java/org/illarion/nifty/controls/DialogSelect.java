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
package org.illarion.nifty.controls;

import de.lessvoid.nifty.controls.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This interface is used to interact with a select dialog that is displayed inside the GUI.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public interface DialogSelect extends Window {
    /**
     * Get the amount of entries on the list.
     *
     * @return the entry count
     */
    int getEntryCount();

    /**
     * Get the item that was selected.
     *
     * @return the selected item or {@code null} in case no item is selected
     */
    @Nullable
    SelectListEntry getSelectedItem();

    /**
     * Get the selected index.
     *
     * @return the index that was selected or {@code -1} in case no item is selected
     */
    int getSelectedIndex();

    /**
     * Add a item to the list of items.
     *
     * @param entry the item to add
     */
    void addItem(@NotNull SelectListEntry entry);
}
