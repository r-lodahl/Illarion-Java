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
package org.illarion.nifty.controls.tooltip.builder;

import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.tools.Color;
import org.jetbrains.annotations.NotNull;


/**
 * Build the tooltip.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ToolTipBuilder extends ControlBuilder {
    public ToolTipBuilder() {
        super("tooltip");
    }

    public ToolTipBuilder(@NotNull String id) {
        super(id, "tooltip");
    }

    public void title(@NotNull String value) {
        set("title", value);
    }

    public void titleColor(@NotNull Color value) {
        titleColor(value.getColorString());
    }

    public void titleColor(@NotNull String value) {
        set("titleColor", value);
    }

    public void description(@NotNull String value) {
        set("description", value);
    }

    public void producer(@NotNull String value) {
        set("producer", value);
    }

    public void type(@NotNull String value) {
        set("itemtype", value);
    }

    public void level(int value) {
        set("level", Integer.toString(value));
    }

    public void levelColor(@NotNull Color value) {
        levelColor(value.getColorString());
    }

    public void levelColor(@NotNull String value) {
        set("levelColor", value);
    }

    public void weight(@NotNull String value) {
        set("weight", value);
    }

    public void worth(long value) {
        set("worth", Long.toString(value));
    }

    public void quality(@NotNull String value) {
        set("quality", value);
    }

    public void durability(@NotNull String value) {
        set("durability", value);
    }

    public void diamondLevel(int value) {
        set("diamondLevel", Integer.toString(value));
    }

    public void emeraldLevel(int value) {
        set("emeraldLevel", Integer.toString(value));
    }

    public void rubyLevel(int value) {
        set("rubyLevel", Integer.toString(value));
    }

    public void obsidianLevel(int value) {
        set("obsidianLevel", Integer.toString(value));
    }

    public void sapphireLevel(int value) {
        set("sapphireLevel", Integer.toString(value));
    }

    public void amethystLevel(int value) {
        set("amethystLevel", Integer.toString(value));
    }

    public void topazLevel(int value) {
        set("topazLevel", Integer.toString(value));
    }

    public void gemBonus(@NotNull String value) {
        set("gemBonus", value);
    }
}
