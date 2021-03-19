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
import illarion.easyquest.quest.Condition;
import illarion.easyquest.quest.TriggerTemplate;
import illarion.easyquest.quest.TriggerTemplates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TriggerDialog extends JDialog {

    @NotNull
    private final JTextField name;
    @NotNull
    private final JComboBox<TriggerTemplate> trigger;
    @NotNull
    private final Box conditionPanels;
    @NotNull
    private final JButton okay;
    @NotNull
    private final JButton cancel;
    @NotNull
    private final JPanel main;

    public TriggerDialog(Frame owner) {
        super(owner);
        setTitle(Lang.getMsg(getClass(), "title"));

        JPanel header = new JPanel(new GridLayout(0, 2, 0, 5));
        main = new JPanel(new GridLayout(0, 1, 0, 5));
        conditionPanels = Box.createVerticalBox();
        Box body = Box.createVerticalBox();
        Box buttons = Box.createHorizontalBox();
        JLabel labelName = new JLabel(Lang.getMsg(getClass(), "name") + ':');
        JLabel labelType = new JLabel(Lang.getMsg(getClass(), "type") + ':');
        name = new JTextField(17);
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        trigger = new JComboBox<>();
        okay = new JButton(Lang.getMsg(getClass(), "ok"));
        cancel = new JButton(Lang.getMsg(getClass(), "cancel"));

        for (int i = 0; i < TriggerTemplates.getInstance().size(); ++i) {
            trigger.addItem(TriggerTemplates.getInstance().getTemplate(i));
        }

        trigger.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                main.removeAll();
                TriggerTemplate template = (TriggerTemplate) e.getItem();
                main.add(new ParameterPanel(template.getId()));
                for (int i = 0; i < template.size(); ++i) {
                    main.add(new ParameterPanel(template.getParameter(i)));
                }
                pack();
                validate();
            }
        });

        trigger.setSelectedIndex(-1);
        trigger.setSelectedIndex(0);

        setResizable(false);

        header.add(labelName);
        header.add(name);
        header.add(labelType);
        header.add(trigger);
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        buttons.add(Box.createHorizontalGlue());
        buttons.add(okay);
        buttons.add(Box.createHorizontalStrut(5));
        buttons.add(cancel);
        buttons.setBorder(BorderFactory.createEmptyBorder(20, 5, 5, 5));

        main.setBorder(BorderFactory.createTitledBorder(Lang.getMsg(getClass(), "parameters")));

        conditionPanels.setBorder(BorderFactory.createTitledBorder(Lang.getMsg(getClass(), "conditions")));

        body.add(main);
        body.add(conditionPanels);

        getRootPane().setDefaultButton(okay);

        add(header, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
        add(buttons, BorderLayout.PAGE_END);

        pack();
    }

    @Override
    public String getName() {
        return name.getText();
    }

    @Override
    public void setName(String value) {
        name.setText(value);
    }

    public Object getId() {
        Component c = main.getComponent(0);
        return ((ParameterPanel) c).getParameter();
    }

    public void setId(Object value) {
        Component c = main.getComponent(0);
        ((ParameterPanel) c).setParameter(value);
    }

    public String getTriggerType() {
        return ((TriggerTemplate) trigger.getSelectedItem()).getName();
    }

    public void setTriggerType(String type) {
        trigger.setSelectedItem(TriggerTemplates.getInstance().getTemplate(type));
    }

    @NotNull
    public Object[] getParameters() {
        int count = main.getComponentCount() - 1;
        Object[] parameters = new Object[count];
        for (int i = 1; i <= count; ++i) {
            Component c = main.getComponent(i);
            parameters[i - 1] = ((ParameterPanel) c).getParameter();
        }
        return parameters;
    }

    public void setParameters(@Nullable Object[] parameters) {
        int count = main.getComponentCount() - 1;

        if (parameters != null) {
            for (int i = 1; i <= count; ++i) {
                Component c = main.getComponent(i);
                ((ParameterPanel) c).setParameter(parameters[i - 1]);
            }
        } else {
            for (int i = 1; i <= count; ++i) {
                Component c = main.getComponent(i);
                ((ParameterPanel) c).setParameter(null);
            }
        }
    }

    @NotNull
    public Condition[] getConditions() {
        int count = (conditionPanels.getComponentCount() + 1) / 2;
        List<Condition> conditions = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            ConditionPanel cp = (ConditionPanel) conditionPanels.getComponent(2 * i);
            Condition c = cp.getCondition();
            if (c != null) {
                conditions.add(c);
            }
        }
        return conditions.toArray(new Condition[conditions.size()]);
    }

    public void setConditions(@Nullable Condition[] conditions) {
        conditionPanels.removeAll();

        if (conditions != null && conditions.length > 0) {
            conditionPanels.add(new ConditionPanel(this, conditions[0]));
            for (int i = 1; i < conditions.length; ++i) {
                conditionPanels.add(new JSeparator());
                conditionPanels.add(new ConditionPanel(this, conditions[i]));
            }
        } else {
            conditionPanels.add(new ConditionPanel(this, null));
        }

        pack();
        validate();
    }

    public void addCondition() {
        conditionPanels.add(new JSeparator());
        conditionPanels.add(new ConditionPanel(this, null));
        pack();
        validate();
    }

    public void removeCondition(ConditionPanel condition) {
        if (conditionPanels.getComponentCount() > 1) {
            int z = conditionPanels.getComponentZOrder(condition);
            if (z != 0) {
                conditionPanels.remove(z - 1);
            } else {
                conditionPanels.remove(z + 1);
            }
            conditionPanels.remove(condition);
        } else {
            ((ConditionPanel) conditionPanels.getComponent(0)).clearSelection();
        }

        pack();
        validate();
    }

    public void addOkayListener(ActionListener listener) {
        okay.addActionListener(listener);
    }

    public void addCancelListener(ActionListener listener) {
        cancel.addActionListener(listener);
    }
}