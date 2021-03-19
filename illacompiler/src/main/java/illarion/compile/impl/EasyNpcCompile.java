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
package illarion.compile.impl;

import illarion.easynpc.ParsedNpc;
import illarion.easynpc.Parser;
import illarion.easynpc.ScriptWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static illarion.easynpc.EasyNpcScript.DEFAULT_CHARSET;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class EasyNpcCompile extends AbstractCompile {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public int compileFile(@NotNull Path file) {
        Objects.requireNonNull(file);
        Path targetDir = Objects.requireNonNull(getTargetDir());
        try {
            ensureTargetDir();
            ParsedNpc npc = Parser.parse(file);
            if (npc.hasErrors()) {
                if (LOGGER.isErrorEnabled()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < npc.getErrorCount(); i++) {
                        ParsedNpc.Error error = npc.getError(i);
                        sb.append("\t Line ").append(error.getLine()).append(": ");
                        sb.append(error.getMessage()).append('\n');
                    }
                    LOGGER.error("Parsing the NPC {} failed with {} errors\n{}", file.getFileName(),
                                 npc.getErrorCount(), sb);
                }
                return -1;
            }

            String moduleName = ParsedNpc.convertToModuleName(file.getFileName().toString().replace(".npc", ""));
            npc.setModuleName(moduleName);

            ScriptWriter writer = new ScriptWriter();
            writer.setSource(npc);
            writer.setGenerated(true);
            try (Writer write = Files.newBufferedWriter(targetDir.resolve(npc.getLuaFilename()), DEFAULT_CHARSET)) {
                writer.setWritingTarget(write);
                writer.write();
                write.flush();
            }
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
            return -1;
        }
        return 0;
    }

    @Override
    public int compileStream(@NotNull InputStream in, @NotNull OutputStream out) {
        try {
            ensureTargetDir();
            ParsedNpc npc = Parser.parse(new InputStreamReader(in, DEFAULT_CHARSET));
            if (npc.hasErrors()) {
                LOGGER.error("Parsing the NPC failed with {} errors", npc.getErrorCount());
                for (int i = 0; i < npc.getErrorCount(); i++) {
                    ParsedNpc.Error error = npc.getError(i);
                    LOGGER.error("\t Line {}: {}", error.getLine(), error.getMessage());
                }
                return -1;
            }
            ScriptWriter writer = new ScriptWriter();
            writer.setSource(npc);
            writer.setGenerated(true);
            Writer write = new OutputStreamWriter(out, DEFAULT_CHARSET);
            writer.setWritingTarget(write);
            writer.write();
            write.flush();
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
            return -1;
        }
        return 0;
    }
}
