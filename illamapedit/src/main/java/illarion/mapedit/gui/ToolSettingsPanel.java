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
package illarion.mapedit.gui;

import illarion.mapedit.events.ToolSelectedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author Tim
 */
public class ToolSettingsPanel extends JPanel {
    public static final Logger LOGGER = LogManager.getLogger();
    private static final int WIDTH = 200;
    @Nullable
    private JComponent lastChild;

    public ToolSettingsPanel() {
        super(new BorderLayout());
        AnnotationProcessor.process(this);
        setPreferredSize(new Dimension(WIDTH, 0));
    }

    @SuppressWarnings("unused")
    @EventSubscriber(eventClass = ToolSelectedEvent.class)
    public void onToolSelected(@NotNull ToolSelectedEvent e) {
        LOGGER.debug("Tool Selected {}", e.getTool());
        removeAll();
        lastChild = e.getTool().getSettingsPanel();
        if (lastChild != null) {
            lastChild.setVisible(true);
            add(lastChild, BorderLayout.CENTER);
        }
        validate();
        repaint();
    }
}
