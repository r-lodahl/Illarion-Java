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
package illarion.client;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.google.common.eventbus.Subscribe;
import illarion.client.crash.DefaultCrashHandler;
import illarion.client.net.client.LogoutCmd;
import illarion.client.resources.SongFactory;
import illarion.client.resources.SoundFactory;
import illarion.client.resources.loaders.SongLoader;
import illarion.client.resources.loaders.SoundLoader;
import illarion.client.state.StateManager;
import illarion.client.util.GlobalExecutorService;
import illarion.client.util.Lang;
import illarion.client.world.World;
import illarion.client.world.events.ConnectionLostEvent;
import illarion.common.bug.CrashReporter;
import illarion.common.bug.ReportDialogFactorySwing;
import illarion.common.config.Config;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.config.ConfigSystem;
import illarion.common.util.AppIdent;
import illarion.common.util.Crypto;
import illarion.common.util.DirectoryManager;
import illarion.common.util.DirectoryManager.Directory;
import illarion.common.util.TableLoader;
import org.illarion.engine.EventBus;
import org.illarion.engine.GameContainer;
import org.illarion.engine.backend.gdx.ApplicationGameContainer;
import org.illarion.engine.event.GameExitEnforcedEvent;
import org.illarion.engine.event.WindowResizedEvent;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*; //TODO: Remove swing and awt
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Locale.Category;
import java.util.Timer;

/**
 * Main Class of the Illarion Client, this loads up the whole game and runs the main loop of the Illarion Client.
 */
public final class IllaClient {
    /**
     * The identification of this application.
     */
    @Nonnull
    public static final AppIdent APPLICATION = new AppIdent("Illarion"); //$NON-NLS-1$
    @Nonnull
    public static final String CFG_FULLSCREEN = "fullscreen";
    @Nonnull
    public static final String CFG_WIDTH = "windowWidth";
    @Nonnull
    public static final String CFG_HEIGHT = "windowHeight";
    /**
     * The default server the client connects too. The client will always connect to this server.
     */
    @Nonnull
    public static final Servers DEFAULT_SERVER;

    /**
     * The error and debug logger of the client.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(IllaClient.class);

    static {
        String server = System.getProperty("illarion.server", "realserver");
        switch ((server == null) ? "" : server) {
            case "testserver" -> DEFAULT_SERVER = Servers.Testserver;
            case "devserver" -> DEFAULT_SERVER = Servers.Devserver;
            default -> DEFAULT_SERVER = Servers.Illarionserver;
        }
    }

    /**
     * The configuration of the client settings.
     */
    private ConfigSystem config;
    /**
     * The container that is used to display the game.
     */
    private GameContainer gameContainer;
    /**
     * Stores the server the client shall connect to.
     */
    @Nonnull
    private Servers usedServer = DEFAULT_SERVER;
    /**
     * The singleton instance of this class.
     */
    @Nonnull
    private static final IllaClient INSTANCE = new IllaClient();
    /**
     * The default empty constructor used to create the singleton instance of this class.
     */
    private IllaClient() {
    }

    /**
     * Show an error message and leave the client.
     *
     * @param message the error message that shall be displayed.
     */
    public static void exitWithError(@Nonnull String message) {
        World.cleanEnvironment();

        LOGGER.info("Client terminated on user request.");

        LOGGER.error(message);
        LOGGER.error("Terminating client!");

        INSTANCE.config.save();

        new Thread(() -> {
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            startFinalKiller();
        }).start();
    }

    /**
     * Save the current configuration and shutdown the client
     */
    public static void exit() {
        EventBus.INSTANCE.post(new GameExitEnforcedEvent());

        LOGGER.info("Client shutdown initiated.");

        getInstance().quitGame();
        World.cleanEnvironment();
        getConfig().save();
        GlobalExecutorService.shutdown();

        LOGGER.info("Cleanup done.");
        startFinalKiller();
    }

    /**
     * This method is the final one to be called before the client is killed for sure. It gives the rest of the client
     * 10 seconds before it forcefully shuts down everything. This is used to ensure that the client quits when it has
     * to, but in case it does so faster, it won't be killed like that.
     */
    private static void startFinalKiller() {
        Timer finalKiller = new Timer("Final Death", true);
        finalKiller.schedule(new TimerTask() {
            @Override
            public void run() {
                CrashReporter.getInstance().waitForReport();
                System.err.println("Killed by final death");
                System.exit(-1);
            }
        }, 10000);
    }

    /**
     * Main function starts the client and sets up all data.
     *
     * @param args the arguments handed over to the client
     */
    public static void main(String... args) {
        // Setup the crash reporter so the client is able to crash properly.
        //CrashReporter.getInstance().setMessageSource(Lang.INSTANCE);
        //TODO: investigate if crash reporter still works (removal of awt/swing in client)

        INSTANCE.init();
    }

    /**
     * Prepares and sets up the entire client
     */
    private void init() {
        prepareConfig();
        assert config != null;
        try {
            initLogfiles();
        } catch (IOException e) {
            System.err.println("Failed to setup logging system!");
            e.printStackTrace(System.err);
        }

        Lang.INSTANCE.setLocale(new Locale(config.getString(Lang.INSTANCE.CONFIG_KEY_LOCALIZATION)));
        CrashReporter.getInstance().setConfig(getConfig());

        // Report errors of the released version only
        if (DEFAULT_SERVER != Servers.Illarionserver) {
            CrashReporter.getInstance().setMode(CrashReporter.MODE_NEVER);
        }
        CrashReporter.getInstance().setDialogFactory(new ReportDialogFactorySwing());

        // Preload sound and music
        try {
            new SongLoader().setTarget(SongFactory.getInstance()).call();
            new SoundLoader().setTarget(SoundFactory.getInstance()).call();
        } catch (Exception e) {
            LOGGER.error("Failed to load sounds and music!");
        }

        boolean isFullscreen = config.getBoolean(CFG_FULLSCREEN);
        int width = config.getInteger(CFG_WIDTH);
        int height = config.getInteger(CFG_HEIGHT);

        gameContainer = new ApplicationGameContainer(width, height, isFullscreen);
        gameContainer.setTitle(APPLICATION.getApplicationIdentifier());
        gameContainer.setIcons("illarion_client16.png",
                "illarion_client32.png",
                "illarion_client64.png",
                "illarion_client256.png");

        EventBus.INSTANCE.register(this);

        try {
            System.out.println("Startup done.");
            //MacOSx notice: in case AWT cannot be completely removed from the game (error messages etc)
            //it is needed to initialize AWT once (by calling getDefaultToolkit) before initGLFW
            gameContainer.startGame(new StateManager(), new Diagnostics(true));
        } catch (@Nonnull Exception e) {
            LOGGER.error("Exception while launching game.", e);
            exit();
        }
    }

    /**
     * Prepare the configuration system and the decryption system.
     */
    private void prepareConfig() {
        config = new ConfigSystem(getFile("Illarion.cfg"),
        getClass().getResourceAsStream("/default-config.properties"));

        //TODO: Set default for RESOLUTION ASAP

        config.setDefault(
                Lang.INSTANCE.CONFIG_KEY_LOCALIZATION,
                Locale.getDefault(Category.DISPLAY).equals(Locale.GERMAN)
                        ? Locale.GERMAN.toString()
                        : Locale.ENGLISH.toString());

        Crypto crypt = new Crypto();
        crypt.loadPublicKey();
        TableLoader.setCrypto(crypt);
    }

    /**
     * Basic initialization of the log files and the debug settings.
     */
    @SuppressWarnings("Duplicates")
    private static void initLogfiles() throws IOException {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        Path userDir = DirectoryManager.getInstance().getDirectory(Directory.User);
        if (!Files.isDirectory(userDir)) {
            if (Files.exists(userDir)) {
                Files.delete(userDir);
            }
            Files.createDirectories(userDir);
        }
        System.setProperty("log_dir", userDir.toAbsolutePath().toString());

        //Reload:
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        ContextInitializer ci = new ContextInitializer(lc);
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL resource = cl.getResource("logback-to-file.xml");
            if (resource != null) {
                ci.configureByResource(resource);
            }
        } catch (JoranException ignored) {
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

        Thread.setDefaultUncaughtExceptionHandler(DefaultCrashHandler.getInstance());

        //noinspection UseOfSystemOutOrSystemErr
        LOGGER.info("{} started.", APPLICATION.getApplicationIdentifier());
        LOGGER.info("VM: {}", System.getProperty("java.version"));
        LOGGER.info("OS: {} {} {}", System.getProperty("os.name"), System.getProperty("os.version"),
                System.getProperty("os.arch"));
    }

    /**
     * Get the configuration handler of the basic client settings.
     *
     * @return the configuration handler
     */
    @Nonnull
    @Contract(pure = true)
    public static Config getConfig() {
        return Objects.requireNonNull(INSTANCE.config, "Config is not ready yet");
    }

    /**
     * Get the full path to a file. This includes the default path that was set up and the name of the file this
     * function gets.
     *
     * @param name the name of the file that shall be append to the folder
     * @return the full path to a file
     */
    @Nonnull
    public static Path getFile(@Nonnull String name) {
        Path userDir = DirectoryManager.getInstance().getDirectory(Directory.User);
        return userDir.resolve(name);
    }

    /**
     * End the game by user request and send the logout command to the server.
     */
    public void quitGame() {
        try {
            World.getNet().sendCommand(new LogoutCmd());
        } catch (@Nonnull IllegalStateException ex) {
            // the NET was not launched up yet. This does not really matter.
        }
    }

    /**
     * Get the singleton instance of this client main object.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static IllaClient getInstance() {
        return INSTANCE;
    }

    // TODO: Move to stateManager
    public static void performLogout() {
        LOGGER.info("Logout requested.");
        getInstance().quitGame();
        //INSTANCE.stateManager.enterState(StateManager.STATE_LOGOUT);
    }

    // TODO: Move to stateManager
    public static void returnToLogin(@Nullable String message) {
        if (message == null) {
            LOGGER.info("Returning to login initiated.");
        } else {
            LOGGER.info("Returning to login initiated. Reason: {}", message);
        }

        //INSTANCE.stateManager.enterState(StateManager.STATE_LOGIN);
    }

    /**
     * Publishing a ConnectionLostEvent.
     *
     * @param message the message that shall be displayed
     */
    public static void sendDisconnectEvent(@Nonnull String message, boolean tryToReconnect) {
        LOGGER.warn("Disconnect received: {}", message);
        EventBus.INSTANCE.post(new ConnectionLostEvent(message, tryToReconnect));
    }

    /**
     * Get the container that is used to display the game inside.
     *
     * @return the are used to display the game inside
     */
    public GameContainer getContainer() {
        return gameContainer;
    }

    /**
     * Get the server that was selected as connection target.
     *
     * @return the selected server
     */
    @Nonnull
    @Contract(pure = true)
    public Servers getUsedServer() {
        return usedServer;
    }

    /**
     * Set the server that shall be used to login at.
     *
     * @param server the server that is used to connect with
     */
    public void setUsedServer(@Nonnull Servers server) {
        usedServer = server;
    }

    @Subscribe
    public void OnWindowSizeChanged(WindowResizedEvent event) {
        config.set(CFG_HEIGHT, event.height);
        config.set(CFG_WIDTH, event.width);
    }

    /**
     * If the config is changed AND the changed config is either the resolution
     * or the fullscreen boolean, updates the relevant config
     *
     * Otherwise, does nothing.
     *
     * Handling changes in other settings (like volume) should be done here
     *
     * @param topic indicates what in the config to change
     * @param data  the event being handled
     */
    public void onEvent(String topic, ConfigChangedEvent data) {
        //TODO: Move to game itself, reaction to new UI cfg/resolution change
        //TODO: Must make Config better accessible for Game
        /*if (CFG_WIDTH.equals(topic) || CFG_HEIGHT.equals(topic) || CFG_FULLSCREEN.equals(topic)) {
            String resolutionString = cfg.getString(CFG_RESOLUTION);
            if (resolutionString == null) {
                LOGGER.error("Failed reading new resolution.");
                return;
            }
            try {
                GraphicResolution res = new GraphicResolution(resolutionString);
                boolean fullScreen = cfg.getBoolean(CFG_FULLSCREEN);
                if (fullScreen) {
                    gameContainer.setFullScreenResolution(res);
                    gameContainer.setFullScreen(true);
                } else {
                    gameContainer.setWindowSize(res.getWidth(), res.getHeight());
                    gameContainer.setFullScreen(false);
                }
                if (!fullScreen) {
                    cfg.set("windowHeight", res.getHeight());
                    cfg.set("windowWidth", res.getWidth());
                }
            } catch (@Nonnull EngineException | IllegalArgumentException e) {
                LOGGER.error("Failed to apply graphic mode: {}", resolutionString);
            }
        }*/
    }
}
