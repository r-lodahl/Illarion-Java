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

import illarion.easyquest.quest.Condition;
import illarion.easyquest.quest.ConditionTemplate;
import illarion.easyquest.quest.ConditionTemplates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;

@SuppressWarnings("serial")
public class ConditionPanel extends JPanel {
    @NotNull
    private final JComboBox<ConditionTemplate> conditionType;
    @NotNull
    private final JPanel parameterPanels;
    @NotNull
    private final JButton addCondition;
    @NotNull
    private final JButton removeCondition;
    private final TriggerDialog owner;

    public ConditionPanel(TriggerDialog owner, @Nullable Condition condition) {

        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);
        setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        this.owner = owner;
        ConditionTemplate[] templates = ConditionTemplates.getInstance().getTemplates();
        Arrays.sort(templates);
        conditionType = new JComboBox<>(templates);
        parameterPanels = new JPanel(new GridLayout(0, 1, 0, 5));
        addCondition = new JButton("+");
        removeCondition = new JButton("-");

        JPanel header = new JPanel();
        header.add(conditionType);
        header.add(removeCondition);
        header.add(addCondition);

        add(header);
        add(parameterPanels);

        TriggerDialog dialog = this.owner;
        conditionType.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                parameterPanels.removeAll();
                ConditionTemplate template = (ConditionTemplate) e.getItem();
                for (int i = 0; i < template.size(); ++i) {
                    ParameterPanel parameter = new ParameterPanel(template.getParameter(i));

                    parameterPanels.add(parameter);
                }
                dialog.pack();
                dialog.validate();
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                parameterPanels.removeAll();
                dialog.pack();
                dialog.validate();
            }
        });

        conditionType.setSelectedIndex(-1);
        if (condition != null) {
            conditionType.setSelectedItem(ConditionTemplates.getInstance().getTemplate(condition.getType()));
            Object[] parameters = condition.getParameters();
            for (int i = 0; i < parameters.length; ++i) {
                ParameterPanel panel = (ParameterPanel) parameterPanels.getComponent(i);
                panel.setParameter(parameters[i]);
            }
        }

        addCondition.addActionListener(e -> dialog.addCondition());

        ConditionPanel conditionPanel = this;
        removeCondition.addActionListener(e -> dialog.removeCondition(conditionPanel));
    }

    public void clearSelection() {
        conditionType.setSelectedIndex(-1);
    }

    @Nullable
    public Condition getCondition() {
        Condition condition = null;
        ConditionTemplate template = (ConditionTemplate) conditionType.getSelectedItem();
        if (template != null) {
            int count = parameterPanels.getComponentCount();
            Object[] parameters = new Object[count];
            for (int i = 0; i < count; ++i) {
                ParameterPanel p = (ParameterPanel) parameterPanels.getComponent(i);
                parameters[i] = p.getParameter();
            }

            condition = new Condition(template.getName(), parameters);
        }
        return condition;
    }
}