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
package illarion.easynpc.parsed;

import illarion.easynpc.writer.LuaRequireTable;
import illarion.easynpc.writer.LuaWriter.WritingStage;
import illarion.easynpc.writer.SQLBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;

/**
 * This parsed data storage is able to store the hair or the beard ID of the NPC.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedHair implements ParsedData {
    /**
     * This enum contains the possible values for the type of the hair.
     */
    public enum HairType {
        /**
         * In case this type is set the ID applies to the beard.
         */
        Beard,

        /**
         * In case this ID is set the ID applied to the main hair.
         */
        Hair
    }

    /**
     * The ID of the hair.
     */
    private final int hairId;

    /**
     * The type of the hair.
     */
    private final HairType hairType;

    /**
     * The constructor for this parsed hair data.
     *
     * @param type the type of the hair
     * @param id the ID of the hair
     */
    public ParsedHair(HairType type, int id) {
        hairType = type;
        hairId = id;
    }

    /**
     * Add the information about the hair and the beard to the SQL query.
     */
    @Override
    public void buildSQL(@NotNull SQLBuilder builder) {
        switch (hairType) {
            case Beard:
                builder.setNpcBeard(hairId);
                break;
            case Hair:
                builder.setNpcHair(hairId);
                break;
        }
    }

    /**
     * Hair information are not written into the LUA script. No stage is effected.
     */
    @Override
    public boolean effectsLuaWritingStage(@NotNull WritingStage stage) {
        return false;
    }

    /**
     * No additional modules needed for this.
     */
    @NotNull
    @Override
    public Collection<String> getRequiredModules() {
        return Collections.emptyList();
    }

    /**
     * The LUA script is not effected by this data. This function does nothing at all in this case.
     */
    @Override
    public void writeLua(
            @NotNull Writer target, @NotNull LuaRequireTable requires, @NotNull WritingStage stage) throws IOException {
        // nothing
    }
}
