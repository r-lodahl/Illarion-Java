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

import de.lessvoid.nifty.spi.sound.SoundHandle;
import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sounds;
import org.jetbrains.annotations.NotNull;


/**
 * This is the sound handle used to reference to the background music track.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class MusicSoundHandle implements SoundHandle {
    /**
     * The music track that is wrapped by this sound handle.
     */
    @NotNull
    private final Music music;

    /**
     * The sound engine used to play the music.
     */
    @NotNull
    private final Sounds soundsEngine;

    /**
     * Create a new sound handle for playing a background music track.
     *
     * @param soundsEngine the sound engine used to play the music
     * @param music the music track that is played
     */
    MusicSoundHandle(@NotNull Sounds soundsEngine, @NotNull Music music) {
        this.music = music;
        this.soundsEngine = soundsEngine;
    }

    @Override
    public void play() {
        soundsEngine.playMusic(music, 100, 100);
    }

    @Override
    public void stop() {
        soundsEngine.stopMusic(100);
    }

    @Override
    public void setVolume(float volume) {
        soundsEngine.setMusicVolume(volume);
    }

    @Override
    public float getVolume() {
        return soundsEngine.getMusicVolume();
    }

    @Override
    public boolean isPlaying() {
        return soundsEngine.isMusicPlaying(music);
    }

    @Override
    public void dispose() {
        music.dispose();
    }
}
