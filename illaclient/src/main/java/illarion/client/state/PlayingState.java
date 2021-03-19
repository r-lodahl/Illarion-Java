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

import com.google.common.eventbus.Subscribe;
import illarion.client.IllaClient;
import illarion.client.input.InputReceiver;
import illarion.client.world.MapDimensions;
import illarion.client.world.World;
import illarion.common.data.SkillLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.BackendBinding;
import org.illarion.engine.EngineException;
import org.illarion.engine.EventBus;
import org.illarion.engine.event.WindowResizedEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This state is active while the player is playing the game.
 */
public class PlayingState implements GameState {
    /**
     * The logger that is used for the logging output of this class.
     */
    @NotNull
    private static final Logger log = LogManager.getLogger();

    private final InputReceiver inputReceiver;

    private BackendBinding binding;

    public PlayingState(@NotNull InputReceiver inputReceiver) {
        this.inputReceiver = inputReceiver;
    }

    @Override
    public void create(BackendBinding binding) {
        log.trace("Creating playing state.");
        this.binding = binding;

        World.initGui(binding.getInput());
        EventBus.INSTANCE.register(this);
        //nifty.registerScreenController(World.getGameGui().getScreenController());
        //Util.loadXML(nifty, "illarion/client/gui/xml/gamescreen.xml");
    }

    @Override
    public void dispose() { }

    @Override
    public void update(int delta) {
        if (World.getGameGui().isReady()) {
            World.getUpdateTaskManager().onUpdateGame(delta);
        }
        World.getGameGui().onUpdateGame(delta);
        World.getWeather().update(delta);
        World.getMapDisplay().update(binding, delta);
        World.getAnimationManager().animate(delta);
        World.getMusicBox().update();
    }

    @Override
    public void render() {
        World.getMap().getMiniMap().render();
        World.getMapDisplay().render(binding.getGraphics());
    }

    @Override
    public boolean isClosingGame() {
        World.getGameGui().getCloseGameGui().showClosingDialog();
        return false;
    }

    @Override
    public void enterState() {
        SkillLoader.load();
        try {
            World.initWorldComponents(binding);
        } catch (EngineException e) {
            log.error("Initialization failed.");
            IllaClient.exitWithError("World init failed!");
        }

        //nifty.gotoScreen("gamescreen");
        inputReceiver.setEnabled(true);

        /*if (Login.INSTANCE.login()) {
            MapDimensions.getInstance().reportScreenSize(binding.getWindow().getWidth(), binding.getWindow().getHeight(), true);
        } else {
            EventBus.INSTANCE.post(new ServerNotFoundEvent());
        }*/
    }

    @Override
    public void leaveState() {
        inputReceiver.setEnabled(false);
    }

    @Subscribe
    public void OnWindowSizeChanged(WindowResizedEvent event) {
        if (!World.isInitDone()) {
            return;
        }
        MapDimensions.getInstance().reportScreenSize(event.width, event.height, false);
    }
}
