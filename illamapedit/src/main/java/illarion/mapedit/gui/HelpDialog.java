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
package illarion.mapedit.gui;

import illarion.mapedit.Lang;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Tim
 * @author Fredrik K
 */
public class HelpDialog extends JDialog implements HyperlinkListener {
    private static final Logger LOGGER = LogManager.getLogger();

    public HelpDialog(JFrame frame) {
        super(frame, Lang.getMsg("gui.docu.Name"), false);
        setLayout(new BorderLayout());

        JEditorPane html = new JEditorPane(new HTMLEditorKit().getContentType(), "");

        html.setEditable(false);
        setMinimumSize(new Dimension(300, 400));
        setPreferredSize(new Dimension(500, 500));
        html.addHyperlinkListener(this);
        URL url = HelpDialog.class.getResource(
                String.format("/docu/%s/mapeditor_docu.html", (Lang.getInstance().isGerman()) ? "de" : "en"));
        html.setContentType("text/html");
        try {
            html.setPage(url);
        } catch (IOException e) {
            html.setContentType("text/plain");
            html.setText(Lang.getMsg("gui.docu.IOError"));
            LOGGER.warn(Lang.getMsg("gui.docu.IOError"), e);
        }
        add(new JScrollPane(html, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
            BorderLayout.CENTER);
        pack();
    }

    @Override
    public void hyperlinkUpdate(@NotNull HyperlinkEvent e) {
        if (e.getEventType() != EventType.ACTIVATED) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (@NotNull IOException | URISyntaxException e1) {
                    LOGGER.warn("Can't launch browser: ", e1);
                }
            }
        });
    }
}
