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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.eventbus.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.*;
import org.illarion.engine.backend.gdx.ui.GdxUserInterface;
import org.illarion.engine.event.GameExitEnforcedEvent;
import org.illarion.engine.event.GameExitRequestedEvent;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Font;
import org.jetbrains.annotations.NotNull;

/**
 * This is the listener application that forwards the application events of libGDX to the {@link GameStateManager} that
 * is defined by this game engine.
 */
class ListenerApplication extends ApplicationAdapter {
    @NotNull
    private static final Logger log = LogManager.getLogger();

    /**
     * This is the game listener of the engine that has to receive the information regarding the game.
     */
    @NotNull
    private final GameStateManager stateManager;
    private final Diagnostics diagnostics;

    private GdxGraphics graphics;
    private GdxAssets assets;
    private GdxSounds sounds;
    private GdxInput input;
    private GdxUserInterface gui;

    /**
     * Create a new listener application that forwards the events of libGDX to the engine listener.
     *
     * @param stateManager the listener of the game engine
     */
    ListenerApplication(@NotNull GameStateManager stateManager, Diagnostics diagnostics) {
        this.stateManager = stateManager;
        this.diagnostics = diagnostics;
    }

    @Override
    public void create() {
        Window window = new GdxWindow();

        assets = new GdxAssets(Gdx.graphics, Gdx.files, Gdx.audio, window);
        graphics = new GdxGraphics(assets, Gdx.graphics);
        input = new GdxInput(Gdx.input);
        sounds = new GdxSounds();

        Skin skin = new Skin(Gdx.files.internal("skin/skin.json"));
        gui = new GdxUserInterface(skin);

        stateManager.create(new GdxBinding(graphics, sounds, input, assets, window, gui));

        EventBus.INSTANCE.register(this);

        stateManager.enterState(State.LOADING);
    }

    @Override
    public void resize(int width, int height) {
        gui.resize(width, height);
    }

    @Override
    public void render() {
        int updateDelta = Math.round(Gdx.graphics.getDeltaTime() * 1000.f);

        stateManager.update(updateDelta);
        assets.getTextureManager().update();

        graphics.beginFrame();

        stateManager.render();
        gui.render();

        if (!diagnostics.isEnabled()) {
            graphics.endFrame();
            return;
        }

        SpriteBatch batch = graphics.getSpriteBatch();
        int diagnosticLastFrameRenderedCalls = batch.totalRenderCalls;
        batch.totalRenderCalls = 0;

        Font diagnosticFont = assets.getFontManager().getFont(diagnostics.getDiagnosticFontName());

        if (diagnosticFont == null) {
            log.warn("Font for displaying diagnostics is null.");
            return;
        }

        int lineHeight = diagnosticFont.getLineHeight();
        int renderHeight = 0;

        graphics.drawText(diagnosticFont, "FPS: " + Gdx.graphics.getFramesPerSecond(), Color.WHITE, 10, renderHeight);
        renderHeight += lineHeight;
        graphics.drawText(diagnosticFont, "Render calls: " + diagnosticLastFrameRenderedCalls, Color.WHITE, 10, renderHeight);
        renderHeight += lineHeight;
        graphics.drawText(diagnosticFont,  "Tile count: " + diagnostics.getTileCount(), Color.WHITE, 10, renderHeight);
        renderHeight += lineHeight;
        graphics.drawText(diagnosticFont, "Scene objects: " + diagnostics.getSceneElementCount(), Color.WHITE, 10, renderHeight);
        renderHeight += lineHeight;
        graphics.drawText(diagnosticFont, "Ping: " + diagnostics.getServerPing() + '+' + diagnostics.getNetPing() + " ms", Color.WHITE, 10, renderHeight);
        graphics.endFrame();
    }

    @Override
    public void dispose() {
        stateManager.dispose();
    }

    @Subscribe
    public void OnGameExitRequested(GameExitRequestedEvent event) {
        if (stateManager.isClosingGame()) {
            Gdx.app.exit();
        }
    }

    @Subscribe
    public void OnGameExitEnforced(GameExitEnforcedEvent event) {
        Gdx.app.exit();
    }
}
