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

import illarion.easyquest.quest.IntegerRelation;
import illarion.easyquest.quest.Relation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class IntegerRelationParameter extends JPanel implements Parameter {
    private final JComboBox<Relation> relation;
    private final JFormattedTextField integer;
    private static final Relation EQUAL = new Relation(Relation.EQUAL);
    private static final Relation NOTEQUAL = new Relation(Relation.NOTEQUAL);
    private static final Relation LESSER = new Relation(Relation.LESSER);
    private static final Relation GREATER = new Relation(Relation.GREATER);
    private static final Relation LESSEROREQUAL = new Relation(Relation.LESSEROREQUAL);
    private static final Relation GREATEROREQUAL = new Relation(Relation.GREATEROREQUAL);
    private static final Map<Integer, Relation> relationMap = new HashMap<Integer, Relation>() {{
        put(Relation.EQUAL, EQUAL);
        put(Relation.NOTEQUAL, NOTEQUAL);
        put(Relation.LESSER, LESSER);
        put(Relation.GREATER, GREATER);
        put(Relation.LESSEROREQUAL, LESSEROREQUAL);
        put(Relation.GREATEROREQUAL, GREATEROREQUAL);
    }};

    public IntegerRelationParameter() {
        super(new BorderLayout(5, 0));
        relation = new JComboBox<>();
        relation.addItem(EQUAL);
        relation.addItem(NOTEQUAL);
        relation.addItem(LESSER);
        relation.addItem(GREATER);
        relation.addItem(LESSEROREQUAL);
        relation.addItem(GREATEROREQUAL);
        integer = new JFormattedTextField();
        add(relation, BorderLayout.WEST);
        add(integer, BorderLayout.CENTER);
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(format);
        DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
        integer.setFormatterFactory(factory);
        integer.setHorizontalAlignment(JFormattedTextField.RIGHT);
        setParameter(new IntegerRelation());
    }

    @Override
    public void setParameter(@Nullable Object parameter) {
        IntegerRelation rel;
        if (parameter != null) {
            rel = (IntegerRelation) parameter;
        } else {
            rel = new IntegerRelation();
        }
        integer.setValue(rel.getInteger());
        relation.setSelectedItem(relationMap.get(rel.getRelation().getType()));
    }

    @Override
    @NotNull
    public Object getParameter() {
        return new IntegerRelation((Relation) relation.getSelectedItem(), (Long) integer.getValue());
    }
}