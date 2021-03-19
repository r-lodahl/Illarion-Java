/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.easynpc.docu;

import illarion.easynpc.Lang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author Fredrik K
 */
public class DocuLeaf implements DocuEntry {
    @NotNull
    private final String docuDesc;
    @NotNull
    private final String docuEx;
    @NotNull
    private final String docuSyntax;
    @NotNull
    private final String docuTitle;

    public DocuLeaf(String type, String name) {
        docuTitle = String.format("illarion.easynpc.parser.%s.Docu.%s.title", type, name);
        docuDesc = String.format("illarion.easynpc.parser.%s.Docu.%s.description", type, name);
        docuEx = String.format("illarion.easynpc.parser.%s.Docu.%s.example", type, name);
        docuSyntax = String.format("illarion.easynpc.parser.%s.Docu.%s.syntax", type, name);
    }

    public DocuLeaf(String type) {
        docuTitle = String.format("illarion.easynpc.parser.%s.Docu.title", type);
        docuDesc = String.format("illarion.easynpc.parser.%s.Docu.description", type);
        docuEx = String.format("illarion.easynpc.parser.%s.Docu.example", type);
        docuSyntax = String.format("illarion.easynpc.parser.%s.Docu.syntax", type);
    }

    @NotNull
    @Override
    public DocuEntry getChild(int index) {
        throw new IllegalArgumentException("There are no childs to request.");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String getDescription() {
        return Lang.getMsg(docuDesc);
    }

    @Nullable
    @Override
    public String getExample() {
        return Lang.getMsg(docuEx);
    }

    @Nullable
    @Override
    public String getSyntax() {
        return Lang.getMsg(docuSyntax);
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(docuTitle);
    }
}
