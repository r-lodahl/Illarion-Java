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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.illarion.engine.backend.shared.AbstractSoundsManager;
import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * This is the sounds manager implementation for the libGDX backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxSoundsManager extends AbstractSoundsManager {
    /**
     * The file system handler that is used to load the sound data.
     */
    @NotNull
    private final Files files;

    /**
     * The sound interface of libGDX that is used to prepare the sound for playback.
     */
    @Nullable
    private final Audio audio;

    /**
     * Create a new instance of the sound manager.
     *
     * @param files the file system handler used to load the data
     * @param audio the audio interface of libGDX that is supposed to be used
     */
    GdxSoundsManager(@NotNull Files files, @Nullable Audio audio) {
        this.files = files;
        this.audio = audio;
    }

    @Nullable
    @Override
    protected Sound loadSound(@NotNull String ref) {
        if (audio == null) {
            return null;
        }
        try {
            return new GdxSound(ref, audio.newSound(files.internal(ref)));
        } catch (@NotNull GdxRuntimeException e) {
            return null;
        }
    }

    @Nullable
    @Override
    protected Music loadMusic(@NotNull String ref) {
        if (audio == null) {
            return null;
        }
        try {
            return new GdxMusic(ref, new SaveGdxOpenALMusic(audio, files.internal(ref)));
        } catch (@NotNull GdxRuntimeException e) {
            return null;
        }
    }
}
