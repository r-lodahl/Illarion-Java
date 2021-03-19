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
 * This class is used to store a walking radius in the parsed NPC.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedWalkingRadius implements ParsedData {
    /**
     * The walking range that is defined by this command.
     */
    private final int range;

    /**
     * Constructor to create new blank instances of this class.
     */
    public ParsedWalkingRadius(int newRange) {
        range = newRange;
    }

    /**
     * No effect on the SQL query.
     */
    @Override
    public void buildSQL(@NotNull SQLBuilder builder) {
        // nothing to add to the query.
    }

    @Override
    public boolean effectsLuaWritingStage(@NotNull WritingStage stage) {
        return false;
    }

    @NotNull
    @Override
    public Collection<String> getRequiredModules() {
        return Collections.emptyList();
    }

    @Override
    public void writeLua(
            @NotNull Writer target, @NotNull LuaRequireTable requires, @NotNull WritingStage stage) throws IOException {
        // not implemented yet.
    }
}
