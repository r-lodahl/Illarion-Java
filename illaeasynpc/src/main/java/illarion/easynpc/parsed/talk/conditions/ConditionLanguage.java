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
package illarion.easynpc.parsed.talk.conditions;

import illarion.easynpc.data.PlayerLanguage;
import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.writer.LuaRequireTable;
import illarion.easynpc.writer.LuaWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the language condition.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConditionLanguage implements TalkCondition {
    /**
     * The LUA code needed for this consequence to work.
     */
    private static final String LUA_CODE = "talkEntry:addCondition(%1$s(\"%2$s\"))" + LuaWriter.NL;

    /**
     * The LUA module required for this condition to work.
     */
    private static final String LUA_MODULE = BASE_LUA_MODULE + "language";

    /**
     * The language the player language is compared against.
     */
    private final PlayerLanguage lang;

    /**
     * The constructor that allows setting the location that applies to this condition.
     *
     * @param newLang the language that the player language is tested against
     */
    public ConditionLanguage(PlayerLanguage newLang) {
        lang = newLang;
    }

    /**
     * Get the LUA module needed for this condition.
     */
    @NotNull
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Write the LUA code needed for this language condition.
     */
    @Override
    public void writeLua(@NotNull Writer target, @NotNull LuaRequireTable requires) throws IOException {
        target.write(String.format(LUA_CODE, requires.getStorage(LUA_MODULE), lang.name()));
    }
}
