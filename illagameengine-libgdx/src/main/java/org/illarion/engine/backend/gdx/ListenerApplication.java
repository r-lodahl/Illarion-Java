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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.common.eventbus.Subscribe;
import org.illarion.engine.Diagnostics;
import org.illarion.engine.EventBus;
import org.illarion.engine.GameStateManager;
import org.illarion.engine.event.GameExitEnforcedEvent;
import org.illarion.engine.event.GameExitRequestedEvent;
import org.illarion.engine.event.WindowResizedEvent;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

/**
 * This is the listener application that forwards the application events of libGDX to the {@link GameStateManager} that
 * is defined by this game engine.
 */
class ListenerApplication extends ApplicationAdapter {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(ListenerApplication.class);

    /**
     * This is the game listener of the engine that has to receive the information regarding the game.
     */
    @Nonnull
    private final GameStateManager stateManager;
    private final Diagnostics diagnostics;

    private GdxGraphics graphics;
    private GdxAssets assets;
    private GdxSounds sounds;
    private GdxInput input;

    /**
     * Create a new listener application that forwards the events of libGDX to the engine listener.
     *
     * @param stateManager the listener of the game engine
     */
    ListenerApplication(@Nonnull GameStateManager stateManager, Diagnostics diagnostics) {
        this.stateManager = stateManager;
        this.diagnostics = diagnostics;
    }

    @Override
    public void create() {
        graphics = new GdxGraphics(Gdx.graphics);
        assets = new GdxAssets(Gdx.graphics, Gdx.files, Gdx.audio);
        input = new GdxInput(Gdx.input);
        sounds = new GdxSounds();

        stateManager.create(new GdxBinding(graphics, sounds, input, assets, new GdxWindow()));

        EventBus.INSTANCE.register(this);
    }

    @Override
    public void resize(int width, int height) {
        EventBus.INSTANCE.post(new WindowResizedEvent(width, height));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int updateDelta = Math.round(Gdx.graphics.getDeltaTime() * 1000.f);

        stateManager.update(updateDelta);

        graphics.beginFrame();
        assets.getTextureManager().update();
        stateManager.render();

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
