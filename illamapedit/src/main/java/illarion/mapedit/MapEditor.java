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
package illarion.mapedit;

import illarion.common.bug.CrashReporter;
import illarion.common.bug.ReportDialogFactorySwing;
import illarion.common.util.AppIdent;
import illarion.common.util.Crypto;
import illarion.common.util.TableLoader;
import illarion.mapedit.crash.DefaultCrashHandler;
import illarion.mapedit.crash.exceptions.UnhandlableException;
import illarion.mapedit.gui.GuiController;
import illarion.mapedit.gui.MainFrame;
import illarion.mapedit.gui.MapEditorConfig;
import illarion.mapedit.gui.SplashScreen;
import illarion.mapedit.resource.ResourceManager;
import illarion.mapedit.resource.loaders.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;

import javax.swing.*;
import java.io.IOException;

/**
 * Main MapEditor class. This class starts the map editor and handles all
 * configuration files and central settings.
 *
 * @author Martin Karing
 * @since 0.99
 */
public final class MapEditor {
    /**
     * The identifier of the application.
     */
    @NotNull
    public static final AppIdent APPLICATION = new AppIdent("Illarion Mapeditor");

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    @NotNull
    private static final Logger LOGGER = LogManager.getLogger();

    private MapEditor() {
    }

    /**
     * Crash the editor with a message.
     *
     * @param message the message the editor is supposed to crash with
     */
    public static void crashEditor(String message) {
        LOGGER.error(message);
        System.exit(-1);
    }

    /**
     * Stop the map editor correctly.
     */
    public static void exit() {
        MainFrame.getInstance().exit();
        CrashReporter.getInstance().waitForReport();
        MapEditorConfig.getInstance().save();
    }

    /**
     * Main function to call to start the map editor.
     *
     * @param args the argument of the system call
     */
    public static void main(String... args) {
        try {
            initLogging();
        } catch (IOException e) {
            System.err.println("Failed to setup logging system!");
            e.printStackTrace(System.err);
        }

        MapEditorConfig.getInstance().init();
        initExceptionHandler();

        SplashScreen.getInstance().setVisible(true);

        JRibbonFrame.setDefaultLookAndFeelDecorated(MapEditorConfig.getInstance().isUseWindowDecoration());
        JDialog.setDefaultLookAndFeelDecorated(MapEditorConfig.getInstance().isUseWindowDecoration());

        Crypto crypt = new Crypto();
        crypt.loadPublicKey();
        TableLoader.setCrypto(crypt);

        loadResources();
        GuiController controller = new GuiController();
        controller.initialize();

        controller.start();
    }

    private static void loadResources() {
        ResourceManager resourceManager = ResourceManager.getInstance();
        resourceManager
                .addResources(ImageLoader.getInstance(), TextureLoaderAwt.getInstance(), TileLoader.getInstance(),
                              ItemNameLoader.getInstance(), ItemLoader.getInstance(), SongLoader.getInstance(),
                              ItemGroupLoader.getInstance(), OverlayLoader.getInstance());

        while (resourceManager.hasNextToLoad()) {
            try {
                LOGGER.debug("Loading {}", resourceManager.getNextDescription());
                SplashScreen.getInstance().setMessage("Loading " + resourceManager.getNextDescription());
                resourceManager.loadNext();
            } catch (IOException e) {
                LOGGER.warn("{} failed!", resourceManager.getPrevDescription());
                //                Crash the editor
                throw new UnhandlableException("Can't load " + resourceManager.getPrevDescription(), e);
            }
        }
    }

    private static void initExceptionHandler() {
        CrashReporter.getInstance().setConfig(MapEditorConfig.getInstance().getInternalCfg());
        CrashReporter.getInstance().setMessageSource(Lang.getInstance());
        CrashReporter.getInstance().setDialogFactory(new ReportDialogFactorySwing());
        Thread.setDefaultUncaughtExceptionHandler(DefaultCrashHandler.getInstance());
        Thread.currentThread().setUncaughtExceptionHandler(DefaultCrashHandler.getInstance());
        SwingUtilities.invokeLater(() -> Thread.currentThread().setUncaughtExceptionHandler(DefaultCrashHandler.getInstance()));
    }

    /**
     * Prepare the proper output of the log files.
     */
    private static void initLogging() throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(DefaultCrashHandler.getInstance());

        LOGGER.info("{} started.", APPLICATION.getApplicationIdentifier());
        LOGGER.info("VM: {}", System.getProperty("java.version"));
        LOGGER.info("OS: {} {} {}", System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"));
    }
}
