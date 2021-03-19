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

import illarion.common.util.CalledByReflection;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Relation implements Serializable {

    public static final int EQUAL = 0;
    public static final int NOTEQUAL = 1;
    public static final int LESSER = 2;
    public static final int GREATER = 3;
    public static final int LESSEROREQUAL = 4;
    public static final int GREATEROREQUAL = 5;

    private int type;

    public Relation() {
        type = EQUAL;
    }

    public Relation(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @CalledByReflection
    public void setType(int type) {
        this.type = type;
    }

    @Override
    @NotNull
    public String toString() {
        switch (type) {
            case EQUAL:
                return "=";
            case NOTEQUAL:
                return "≠";
            case LESSER:
                return "<";
            case GREATER:
                return ">";
            case LESSEROREQUAL:
                return "≤";
            case GREATEROREQUAL:
                return "≥";
            default:
                return "";
        }
    }

    @NotNull
    public String toLua() {
        switch (type) {
            case EQUAL:
                return "==";
            case NOTEQUAL:
                return "~=";
            case LESSER:
                return "<";
            case GREATER:
                return ">";
            case LESSEROREQUAL:
                return "<=";
            case GREATEROREQUAL:
                return ">=";
            default:
                return "";
        }
    }
}
