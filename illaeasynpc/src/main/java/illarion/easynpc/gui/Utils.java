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
package illarion.easynpc.gui;

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.ScriptWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

/**
 * A small set of static utility functions that help at some points.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class Utils {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    private Utils() {
    }

    @Nullable
    public static ResizableIcon getResizableIconFromResource(String resource) {
        Image image;
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            image = ImageIO.read(cl.getResource(resource));
        } catch (IOException e) {
            LOGGER.error("Failed to read image: \"{}\"", resource);
            return null;
        }
        int height = image.getHeight(null);
        int width = image.getWidth(null);
        return ImageWrapperResizableIcon.getIcon(image, new Dimension(width, height));
    }

    /**
     * Parse the script shown in this editor but don't do anything with it but
     * showing the errors.
     *
     * @param editor the editor that contains the script to parse
     */
    public static void reparseSilent(@NotNull Editor editor) { // NO_UCD
        editor.getParsedData();
    }

    /**
     * Open a script from a file and display it in a new editor.
     *
     * @param file the file that is the source of this script
     */
    static void openScript(@NotNull MainFrame frame, @NotNull Path file) {
        try {
            int editorIndex = frame.alreadyOpen(file);
            if (editorIndex > -1) {
                frame.setCurrentEditorTab(editorIndex);
                return;
            }
            EasyNpcScript easyScript = new EasyNpcScript(file);
            frame.addNewScript().loadScript(easyScript);
            frame.setCurrentTabTitle(file.getFileName().toString());
            Config.getInstance().addLastOpenedFile(file);
        } catch (IOException e1) {
            LOGGER.error("Reading the script failed.", e1); //$NON-NLS-1$
        }
    }

    /**
     * Save a script in the editor as easyNPC script. This saves the current
     * state of the script and does not parse it again.
     *
     * @param editor the editor that supplies the script text
     */
    static void saveEasyNPC(@NotNull MainFrame frame, @NotNull Editor editor) {
        Path targetFile = editor.getScriptFile();
        if (targetFile == null) {
            selectAndSaveEasyNPC(frame, editor);
            return;
        }

        saveEasyNPCImpl(editor, targetFile);
        editor.saved();
    }

    /**
     * Save the script in the editor as a LUA script. This results in parsing
     * the script and in case this went fine the user is ordered to select a
     * location to save the LUA version of the script.
     *
     * @param editor the editor containing the original script.
     */
    static void saveLuaScript(@NotNull MainFrame frame, @NotNull Editor editor) {
        ParsedNpc npc = editor.getParsedData();
        if (npc == null) {
            JOptionPane.showMessageDialog(frame, Lang.getMsg(Utils.class, "saveLuaErrors"),
                                          Lang.getMsg(Utils.class, "saveLuaErrorsTitle"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileDiag = new JFileChooser();
        fileDiag.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(@NotNull File f) {
                return !f.isFile() || f.getName().endsWith(".lua"); //$NON-NLS-1$
            }

            @Override
            public String getDescription() {
                return Lang.getMsg(Utils.class, "luaScriptsFileType"); //$NON-NLS-1$
            }
        });
        fileDiag.setCurrentDirectory(new File(Config.getInstance().getLuaNpcFolder()));
        fileDiag.setSelectedFile(
                new File(Config.getInstance().getLuaNpcFolder() + File.separator + npc.getLuaFilename()));

        int fileReturn = fileDiag.showSaveDialog(frame);
        if (fileReturn == JFileChooser.APPROVE_OPTION) {
            File targetFile = fileDiag.getSelectedFile();
            File backupFile = new File(targetFile.getAbsolutePath() + ".bak");

            if (targetFile.exists()) {
                targetFile.renameTo(backupFile);
            }

            try {
                ScriptWriter writer = new ScriptWriter();
                writer.setSource(npc);
                Writer outputWriter = new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.ISO_8859_1);
                writer.setWritingTarget(outputWriter);
                writer.write();
                outputWriter.close();
                if (backupFile.exists()) {
                    backupFile.delete();
                }
            } catch (IOException ex) {
                if (backupFile.exists()) {
                    if (targetFile.exists()) {
                        targetFile.delete();
                    }
                    backupFile.renameTo(targetFile);
                }
            }
        }
    }

    /**
     * Display a file selection dialog to allow the player to select a easyNPC
     * script that is load after.
     */
    static void selectAndOpenScript(@NotNull MainFrame frame) {
        JFileChooser fileDiag = new JFileChooser();
        fileDiag.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(@NotNull File f) {
                return !f.isFile() || f.getName().endsWith(".npc"); //$NON-NLS-1$
            }

            @Override
            public String getDescription() {
                return Lang.getMsg(Utils.class, "easyNpcScriptsFileType"); //$NON-NLS-1$
            }
        });
        fileDiag.setCurrentDirectory(new File(Config.getInstance().getEasyNpcFolder()));

        int fileReturn = fileDiag.showOpenDialog(frame);
        if (fileReturn == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileDiag.getSelectedFile();
            openScript(frame, selectedFile.toPath());
        }
    }

    /**
     * Allow the user to select the folder and the file name where to store the
     * easyNPC script. Once selected the function saves the current state of the
     * script.
     *
     * @param editor the editor that supplies the script text
     */
    static void selectAndSaveEasyNPC(@NotNull MainFrame frame, @NotNull Editor editor) {
        JFileChooser fileDiag = new JFileChooser();
        fileDiag.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(@NotNull File f) {
                return !f.isFile() || f.getName().endsWith(".npc"); //$NON-NLS-1$
            }

            @Override
            public String getDescription() {
                return Lang.getMsg(Utils.class, "easyNpcScriptsFileType"); //$NON-NLS-1$
            }
        });
        fileDiag.setCurrentDirectory(new File(Config.getInstance().getEasyNpcFolder()));
        Path scriptFile = editor.getScriptFile();
        fileDiag.setSelectedFile((scriptFile == null) ? null : scriptFile.toFile());
        int fileReturn = fileDiag.showSaveDialog(frame);
        if (fileReturn == JFileChooser.APPROVE_OPTION) {
            String targetFile = fileDiag.getSelectedFile().getAbsolutePath();
            if (!targetFile.endsWith(".npc")) {
                targetFile += ".npc";
            }

            Path realTargetFile = Paths.get(targetFile);
            saveEasyNPCImpl(editor, realTargetFile);
            editor.setLoadScriptFile(realTargetFile);
            frame.setTabTitle(editor, realTargetFile.getFileName().toString());
        }
        editor.saved();
    }

    static void uploadLuaScript(@NotNull MainFrame frame, @NotNull Editor editor) {
        ParsedNpc npc = editor.getParsedData();
        if (npc == null) {
            JOptionPane.showMessageDialog(frame, Lang.getMsg(Utils.class, "uploadLuaErrors"),
                                          Lang.getMsg(Utils.class, "uploadLuaErrorsTitle"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            URL url = new URL("http://illarion.org/~nitram/test_npc.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            ScriptWriter writer = new ScriptWriter();
            writer.setSource(npc);
            StringWriter stringWriter = new StringWriter();
            writer.setWritingTarget(stringWriter);
            writer.write();
            String script = stringWriter.toString();

            Base64.Encoder b64Encoder = Base64.getUrlEncoder();
            String base64Script = b64Encoder.encodeToString(script.getBytes(StandardCharsets.UTF_8));

            String query = "script=" + URLEncoder.encode(base64Script, StandardCharsets.UTF_8);

            try (Writer output = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8))) {
                output.write(query);
                output.flush();
            }

            try (InputStream input = conn.getInputStream()) {
                input.close();
            }

            JOptionPane.showMessageDialog(frame, Lang.getMsg(Utils.class, "luaUploadInfos"),
                                          Lang.getMsg(Utils.class, "luaUploadInfosTitle"),
                                          JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            LOGGER.error("Connection to host failed", ex);
        }
    }

    /**
     * The private implementation of the of the save easyScript function. This
     * writes a text to a file using the correct encoding.
     *
     * @param editor the editor supplying the script
     * @param targetFile the file that is the target of this writing operation
     */
    private static void saveEasyNPCImpl(@NotNull Editor editor, @NotNull Path targetFile) {
        try {
            @Nullable Path backupFile = Files.createTempFile(targetFile.getParent(), "npc_", ".bak");
            if (Files.isReadable(targetFile)) {
                Files.copy(targetFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.deleteIfExists(backupFile);
                backupFile = null;
            }

            try (Writer bufferedWriter = Files.newBufferedWriter(targetFile, EasyNpcScript.DEFAULT_CHARSET)) {
                String scriptText = editor.getScriptText();
                EasyNpcScript.COPYRIGHT_HEADER.writeTo(bufferedWriter);
                bufferedWriter.write(scriptText);
                bufferedWriter.flush();
            } catch (Exception e) {
                if (backupFile != null) {
                    Files.copy(backupFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            if (backupFile != null) {
                Files.deleteIfExists(backupFile);
            }
        } catch (Exception e) {
            LOGGER.error("Writing the easyNPC Script failed.", e); //$NON-NLS-1$
        }
    }
}
