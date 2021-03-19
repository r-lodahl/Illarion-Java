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
 * This class is used to store a empty line in the parsed NPC. This is needed to ensure to keep the formatting of the
 * NPC script.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedEmptyLine implements ParsedData {
    /**
     * Empty lines do not effect the query.
     */
    @Override
    public void buildSQL(@NotNull SQLBuilder builder) {
        // nothing to add to the query.
    }

    /**
     * Get the stages effected by the free lines.
     *
     * @return {@code false} always because no stages are effected
     */
    @Override
    public boolean effectsLuaWritingStage(@NotNull WritingStage stage) {
        return false;
    }

    /**
     * Get the modules needed by the empty lines.
     *
     * @return {@code null} always
     */
    @NotNull
    @Override
    public Collection<String> getRequiredModules() {
        return Collections.emptyList();
    }

    /**
     * Write the LUA code for those empty lines. Empty lines are discarded by
     * default.
     */
    @Override
    public void writeLua(
            @NotNull Writer target, @NotNull LuaRequireTable requires, @NotNull WritingStage stage) throws IOException {
        // nothing to do
    }
}
