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
package illarion.easynpc.parsed.talk.consequences;

import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaRequireTable;
import illarion.easynpc.writer.LuaWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the introduce consequence.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConsequenceIntroduce implements TalkConsequence {

    /**
     * The LUA code needed to be included for a introduce consequence.
     */
    private static final String LUA_CODE = "talkEntry:addConsequence(%1$s())" + LuaWriter.NL;

    /**
     * The LUA module needed for this consequence to work.
     */
    private static final String LUA_MODULE = BASE_LUA_MODULE + "introduce";

    /**
     * Get the module that is needed for this consequence to work.
     */
    @NotNull
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(@NotNull Writer target, @NotNull LuaRequireTable requires) throws IOException {
        target.write(String.format(LUA_CODE, requires.getStorage(LUA_MODULE)));
    }
}
