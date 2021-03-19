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
package org.illarion.engine.nifty;

import de.lessvoid.nifty.sound.SoundSystem;
import de.lessvoid.nifty.spi.sound.SoundDevice;
import de.lessvoid.nifty.spi.sound.SoundHandle;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;
import org.illarion.engine.Engine;
import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * This is the sound device for the Nifty-GUI that uses the Illarion Game Engine to play background music and sound
 * effects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class IgeSoundDevice implements SoundDevice {
    /**
     * The sounds playback engine.
     */
    @NotNull
    private final Engine engine;

    /**
     * Create a new sound device.
     *
     * @param engine the engine that is used by this device
     */
    public IgeSoundDevice(@NotNull Engine engine) {
        this.engine = engine;
    }

    @Override
    public void setResourceLoader(@NotNull NiftyResourceLoader niftyResourceLoader) {
        // nothing
    }

    @Nullable
    @Override
    public SoundHandle loadSound(@NotNull SoundSystem soundSystem, @NotNull String filename) {
        Sound sound = engine.getAssets().getSoundsManager().getSound(filename);
        if (sound == null) {
            return null;
        }
        return new SoundSoundHandle(engine.getSounds(), sound);
    }

    @Nullable
    @Override
    public SoundHandle loadMusic(@NotNull SoundSystem soundSystem, @NotNull String filename) {
        Music music = engine.getAssets().getSoundsManager().getMusic(filename);
        if (music == null) {
            return null;
        }
        return new MusicSoundHandle(engine.getSounds(), music);
    }

    @Override
    public void update(int delta) {
        // nothing to do
    }
}
