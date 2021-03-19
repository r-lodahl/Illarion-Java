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
package illarion.easyquest.quest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConditionTemplate implements Comparable<ConditionTemplate> {
    private final String name;
    @Nullable
    private String title;
    @Nullable
    private String condition;
    private final List<TemplateParameter> parameters;

    public ConditionTemplate(String name) {
        this.name = name;
        title = null;
        condition = null;
        parameters = new ArrayList<>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Nullable
    public String getCondition() {
        return condition;
    }

    public int size() {
        return parameters.size();
    }

    public TemplateParameter getParameter(int number) {
        return parameters.get(number);
    }

    public void addParameter(TemplateParameter parameter) {
        parameters.add(parameter);
    }

    public boolean isComplete() {
        return (title != null) && (condition != null) && !parameters.isEmpty();
    }

    @Override
    @NotNull
    public String toString() {
        return (title == null) ? "" : title;
    }

    @Override
    public int compareTo(@NotNull ConditionTemplate o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (super.equals(o)) {
            return true;
        }
        if (o instanceof ConditionTemplate) {
            ConditionTemplate otherTemplate = (ConditionTemplate) o;
            return otherTemplate.toString().equals(toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}