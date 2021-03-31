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
package illarion.client.state;

import illarion.client.IllaClient;
import illarion.client.graphics.FontLoader;
import illarion.client.input.InputReceiver;
import illarion.client.net.NetworkEventConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.BackendBinding;
import org.illarion.engine.GameStateManager;
import org.illarion.engine.State;
import org.illarion.engine.assets.TextureManager;
import org.illarion.engine.sound.Sounds;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the game Illarion. This class takes care for actually building up Illarion. It will maintain the different
 * states of the game and allow switching them.
 */
public final class StateManager implements GameStateManager {
    private final Map<State, GameState> gameStates;
    private BackendBinding binding;
    private NetworkEventConsumer networkEventConsumer;

    private State currentState = State.NONE;
    private State nextState = State.NONE;

    /**
     * Create the game with the fitting title, showing the name of the application and its version.
     */
    public StateManager() {
        gameStates = new HashMap<>(7);
    }

    /**
     * Sets the next game state to the given state, if between -1 and 5
     * Game will advance to the given state upon the next call of update()
     * @param state the state to enter.
     */
    @Override
    public void enterState(State state) {
        nextState = state;
    }

    @NotNull
    private GameState getCurrentState() {
        return gameStates.get(currentState);
    }

    private static final Logger log = LogManager.getLogger();

    /**
     * Initializes fields and prepares the Game for launch
     * Enters the login state when finished
     */
    @Override
    public void create(BackendBinding binding) {
        this.binding = binding;

        TextureManager texManager = binding.getAssets().getTextureManager();
        texManager.addTextureDirectory("gui");
        texManager.addTextureDirectory("chars");
        texManager.addTextureDirectory("items");
        texManager.addTextureDirectory("tiles");
        texManager.addTextureDirectory("effects");

        try {
            FontLoader.getInstance().prepareAllFonts(binding.getAssets());
        } catch (IOException e) {
            log.error("Error while loading fonts!", e);
        }

        InputReceiver inputReceiver = new InputReceiver(binding.getInput());
        // Prepare the game's Nifty and its properties
        /*nifty = new Nifty(new IgeRenderDevice(container, "gui/"), new IgeSoundDevice(container.getEngine()),
                          new IgeInputSystem(container.getEngine().getInput(), inputReceiver),
                          new AccurateTimeProvider());

        Properties niftyProperties = nifty.getGlobalProperties();
        if (niftyProperties == null) {
            niftyProperties = new Properties();
            nifty.setGlobalProperties(niftyProperties);
        }
        niftyProperties.setProperty("MULTI_CLICK_TIME",
                                    Integer.toString(IllaClient.getCfg().getInteger("doubleClickInterval")));
        nifty.setLocale(Lang.getInstance().getLocale());*/
        /*container.getEngine().getInput().addForwardingListener(new ForwardingListener() {
            @Override
            public void forwardingEnabledFor(@NotNull ForwardingTarget target) {
                // nothing
            }

            @Override
            public void forwardingDisabledFor(@NotNull ForwardingTarget target) {
                //if ((target == ForwardingTarget.Mouse) || (target == ForwardingTarget.All)) {
                //    nifty.resetMouseInputEvents();
                //}
            }
        });*/

        gameStates.put(State.NONE, new NoneState());
        gameStates.put(State.LOGIN, new LoginState());
        gameStates.put(State.LOADING, new LoadingState());
        gameStates.put(State.PLAYING, new PlayingState(inputReceiver));
        gameStates.put(State.ENDING, new EndState());
        gameStates.put(State.LOGOUT, new LogoutState());
        gameStates.put(State.DISCONNECT, new DisconnectedState());

        // Prepare the sounds and music for use, set volume based on the current configuration settings
        Sounds sounds = binding.getSounds();
        if (IllaClient.getConfig().getBoolean("musicOn")) {
            sounds.setMusicVolume(IllaClient.getConfig().getFloat("musicVolume") / 100.f);
        } else {
            sounds.setMusicVolume(0.f);
        }
        if (IllaClient.getConfig().getBoolean("soundOn")) {
            sounds.setSoundVolume(IllaClient.getConfig().getFloat("soundVolume") / 100.f);
        } else {
            sounds.setSoundVolume(0.f);
        }

        /* Loading general style and control files. */
        //nifty.loadStyleFile("nifty-illarion-style.xml");
        //nifty.loadControlFile("nifty-default-controls.xml");
        //nifty.loadControlFile("illarion-gamecontrols.xml");

        for (GameState state : gameStates.values()) {
            state.create(binding);
        }

        this.networkEventConsumer = new NetworkEventConsumer(binding);
    }

    /**
     * Returns the state associated with the given index
     * @param state the enum value of the state to use
     * @return  the state associated with the given index
     */
    @NotNull
    public GameState getState(State state) {
        return gameStates.get(state);
    }

    /**
     * Disposes each GameState
     */
    @Override
    public void dispose() {
        for (@NotNull GameState state : gameStates.values()) {
            state.dispose();
        }
    }

    /**
     * During the call of this function the application is supposed to perform the update of the game logic.
     *
     * If the Game is not in the state given by the last call of enterState(), enters that state
     * Updates the Nifty gui for the (now current) state
     *
     * @param delta the time since the last update call
     */
    @Override
    public void update(int delta) {
        if (nextState != currentState) {
            GameState activeState = getCurrentState();
            activeState.leaveState();

            currentState = nextState;
            getCurrentState().enterState();
        }

        getCurrentState().update(delta);
    }

    /**
     * Perform all rendering operations
     * If more diagnostic data should be shown, add to this method
     */
    @Override
    public void render() {
        getCurrentState().render();
    }

    /**
     * This function is called in case the game receives a request to be closed.
     *
     * @return {@code true} in case the game is supposed to shutdown, else the closing request is rejected
     */
    @Override
    public boolean isClosingGame() {
        return getCurrentState().isClosingGame();
    }
}
