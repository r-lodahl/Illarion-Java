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
package illarion.easynpc.gui;

import illarion.easynpc.Lang;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mid2Low;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mirror;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * This ribbon band contains the searching utility that can be used in the editor.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SearchBand extends JRibbonBand {
    /**
     * The serialization UID of this search band.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The constructor of the search band that creates all contents of this band
     * properly.
     */
    public SearchBand(@NotNull MainFrame frame) {
        super(Lang.getMsg(SearchBand.class, "title"), null);

        startGroup(Lang.getMsg(SearchBand.class, "fastSearchGroup"));
        JTextField textBox = new JTextField();
        textBox.setPreferredSize(new Dimension(150, textBox.getPreferredSize().height));
        JRibbonComponent ribbonTextBox = new JRibbonComponent(textBox);
        addRibbonComponent(ribbonTextBox);

        ribbonTextBox.setRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "fastSearchTooltipTitle"),
                                                     Lang.getMsg(getClass(), "fastSearchTooltip")));

        textBox.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(@NotNull KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }

                Editor scriptEditor = frame.getCurrentScriptEditor();
                RSyntaxTextArea editor = scriptEditor.getEditor();

                SearchContext search = new SearchContext();
                search.setSearchFor(textBox.getText());
                search.setMatchCase(false);
                search.setRegularExpression(false);
                search.setSearchForward(true);

                SearchEngine.find(editor, search);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // nothing
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // nothing
            }
        });

        startGroup();

        JCommandButton findButton = new JCommandButton(Lang.getMsg(SearchBand.class, "advancedSearchButton"),
                                                       Utils.getResizableIconFromResource("find.png"));
        findButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "findButtonTooltipTitle"),
                                                        Lang.getMsg(getClass(), "findButtonTooltip")));
        findButton.addActionListener(new ActionListener() {
            @Nullable
            private SearchDialog dialog;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (dialog == null) {
                    dialog = new SearchDialog(frame);
                }
                dialog.setVisible(true);
            }
        });

        addCommandButton(findButton, RibbonElementPriority.TOP);

        List<RibbonBandResizePolicy> policies = new ArrayList<>();
        policies.add(new Mirror(getControlPanel()));
        policies.add(new Mid2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}
