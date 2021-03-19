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
package illarion.easyquest.gui;

import illarion.easyquest.Lang;
import org.jetbrains.annotations.NotNull;
import org.pushingpixels.flamingo.api.common.CommandToggleButtonGroup;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mid2Low;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mirror;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

final class GraphBand extends JRibbonBand {
    /**
     * The serialization UID of this ribbon band.
     */
    private static final long serialVersionUID = 1L;

    @NotNull
    private final JCommandButton propertiesButton;
    @NotNull
    private final JCommandToggleButton nodeButton;
    @NotNull
    private final JCommandToggleButton transitionButton;

    /**
     * Default constructor that prepares the buttons displayed on this band.
     */
    public GraphBand() {
        super(Lang.getMsg(GraphBand.class, "title"), null);

        propertiesButton = new JCommandButton(Lang.getMsg(getClass(), "properties"),
                                              Utils.getResizableIconFromResource("properties.png"));
        nodeButton = new JCommandToggleButton(Lang.getMsg(getClass(), "state"),
                                              Utils.getResizableIconFromResource("state.png"));
        transitionButton = new JCommandToggleButton(Lang.getMsg(getClass(), "transition"),
                                                    Utils.getResizableIconFromResource("transition.png"));

        propertiesButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "propertiesTooltipTitle"),
                                                              Lang.getMsg(getClass(), "propertiesTooltip")));
        nodeButton.setActionRichTooltip(
                new RichTooltip(Lang.getMsg(getClass(), "nodeTooltipTitle"), Lang.getMsg(getClass(), "nodeTooltip")));
        transitionButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "transitionTooltipTitle"),
                                                              Lang.getMsg(getClass(), "transitionTooltip")));

        String idRequestTitle = Lang.getMsg(getClass(), "idRequestTitle");
        String idRequest = Lang.getMsg(getClass(), "idRequest");
        ActionListener propertiesAction = e -> {
            boolean validID = false;
            int id = MainFrame.getInstance().getCurrentQuestEditor().getQuestID();
            while (!validID) {
                validID = true;
                String input = (String) JOptionPane
                        .showInputDialog(null, idRequest, idRequestTitle, JOptionPane.QUESTION_MESSAGE, null, null,
                                id);
                if (input != null) {
                    try {
                        id = Integer.parseInt(input);
                        MainFrame.getInstance().getCurrentQuestEditor().setQuestID(id);
                    } catch (NumberFormatException exc) {
                        validID = false;
                    }
                }
            }
        };

        ActionListener nodeAction = e -> {
            if (nodeButton.getActionModel().isSelected()) {
                MainFrame.getInstance().setCreateType(MainFrame.CREATE_STATUS);
            } else {
                MainFrame.getInstance().setCreateType(MainFrame.CREATE_NOTHING);
            }
        };

        ActionListener transitionAction = e -> {
            if (transitionButton.getActionModel().isSelected()) {
                MainFrame.getInstance().setCreateType(MainFrame.CREATE_TRIGGER);
            } else {
                MainFrame.getInstance().setCreateType(MainFrame.CREATE_NOTHING);
            }
        };

        propertiesButton.addActionListener(propertiesAction);
        nodeButton.addActionListener(nodeAction);
        transitionButton.addActionListener(transitionAction);

        CommandToggleButtonGroup graphElements = new CommandToggleButtonGroup();
        graphElements.add(nodeButton);
        graphElements.add(transitionButton);

        addCommandButton(propertiesButton, RibbonElementPriority.TOP);
        addCommandButton(nodeButton, RibbonElementPriority.TOP);
        addCommandButton(transitionButton, RibbonElementPriority.TOP);

        List<RibbonBandResizePolicy> policies = new ArrayList<>();
        policies.add(new Mirror(getControlPanel()));
        policies.add(new Mid2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}
