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
package illarion.common.config.gui.entries.swing;

import illarion.common.config.entries.CheckEntry;
import illarion.common.config.entries.ConfigEntry;
import illarion.common.config.gui.entries.SavableEntry;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * This is a special implementation for the check box that is initialized with a
 * configuration entry. Its sole purpose is the use along with the configuration
 * system.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CheckEntrySwing extends JCheckBox implements SavableEntry {
    /**
     * The serialization UID of this text field.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The text entry used to initialize this instance.
     */
    @NotNull
    private final CheckEntry entry;

    /**
     * Create a instance of this check entry and set the configuration entry
     * that is used to setup this class.
     *
     * @param usedEntry the entry used to setup this class, the entry needs to
     * pass the check with the static method
     */
    public CheckEntrySwing(@NotNull ConfigEntry usedEntry) {
        if (!isUsableEntry(usedEntry)) {
            throw new IllegalArgumentException("ConfigEntry type illegal.");
        }
        entry = (CheckEntry) usedEntry;
        setSelected(entry.getValue());
    }

    /**
     * Text a entry if it is usable with this class or not.
     *
     * @param entry the entry to test
     * @return {@code true} in case this entry is usable with this class
     */
    public static boolean isUsableEntry(ConfigEntry entry) {
        return entry instanceof CheckEntry;
    }

    /**
     * Save the value in this text entry to the configuration.
     */
    @Override
    public void save() {
        entry.setValue(isSelected());
    }
}
