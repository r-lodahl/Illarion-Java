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
package illarion.mapedit.tools.panel.components;

import illarion.mapedit.events.ItemInspectorSelectedEvent;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author Fredrik K
 */
public class ItemDataPanel extends JPanel {
    @NotNull
    private final ItemDataFields dataFields;
    @NotNull
    private final ItemDataTable dataTable;

    public ItemDataPanel() {
        super(new BorderLayout());
        AnnotationProcessor.process(this);

        dataFields = new ItemDataFields();
        dataTable = new ItemDataTable();

        add(dataFields, BorderLayout.NORTH);
        add(dataTable, BorderLayout.CENTER);
    }

    public void clearDataList() {
        dataTable.clearDataList();
        dataFields.clearFields();
    }

    @EventSubscriber
    public void onItemInspectorSelected(@NotNull ItemInspectorSelectedEvent e) {
        dataTable.setDataList(e.getItem());
        dataFields.setData(e.getItem());
    }
}
