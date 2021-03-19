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
package illarion.easynpc.data;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.jetbrains.annotations.NotNull;


/**
 * This enumerator stores all possible types of magic types along with the
 * needed data about these flags to store them and work with them properly.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum CharacterMagicType {
    /**
     * Enumerator constant that stores the value used in case the character has
     * bard-magic.
     */
    bard(2, "bard", true),
    //$NON-NLS-1$

    /**
     * Enumerator constant that stores the value used in case the character has
     * druidic-magic.
     */
    druid(3, "druid", true),
    //$NON-NLS-1$

    /**
     * Enumerator constant that stores the value used in case the character has
     * magician-magic.
     */
    mage(0, "mage", true),
    //$NON-NLS-1$

    /**
     * Enumerator constant that stores the value used in case no magic type
     * applies. This only works on conditions, never on consequences.
     */
    nomagic(-1, "nomagic", false),
    //$NON-NLS-1$

    /**
     * Enumerator constant that stores the value used in case the character has
     * priest-magic.
     */
    priest(1, "priest", true); //$NON-NLS-1$

    /**
     * Parameter that stores if the magic type can be used as consequence or
     * not.
     */
    private final boolean magicTypeConsequence;

    /**
     * Parameter to store the used ID for this magic type.
     */
    private final int magicTypeId;

    /**
     * Parameter to store the name of this magic type that is used on the
     * easyNPC language.
     */
    private final String magicTypeName;

    /**
     * Default constructor that stores the data for each enumerator constant.
     *
     * @param id the ID of the magic type
     * @param name the name of the magic type
     * @param consequence the flag if the magic type can be a consequence or not
     */
    CharacterMagicType(
            int id, String name, boolean consequence) {
        magicTypeId = id;
        magicTypeName = name;
        magicTypeConsequence = consequence;
    }

    public boolean canByConsequence() {
        return magicTypeConsequence;
    }

    public int getMagicTypeId() {
        return magicTypeId;
    }

    public String getMagicTypeName() {
        return magicTypeName;
    }

    /**
     * Add this values to the highlighted tokens.
     *
     * @param map the map that stores the tokens
     */
    public static void enlistHighlightedWords(@NotNull TokenMap map) {
        for (CharacterMagicType magicType : CharacterMagicType.values()) {
            map.put(magicType.getMagicTypeName(), Token.VARIABLE);
        }
    }
}
