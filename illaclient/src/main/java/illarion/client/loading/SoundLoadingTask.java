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
package illarion.client.loading;

import illarion.client.resources.SongFactory;
import illarion.client.resources.SoundFactory;
import org.illarion.engine.assets.Assets;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * This loading task is used to load all songs and sounds.
 */
class SoundLoadingTask implements Runnable {
    /**
     * The game engine instance used to load the sounds.
     */
    @NotNull
    private final Assets assets;

    /**
     * The list of songs that need to be loaded.
     */
    @NotNull
    private final Queue<String> songsToLoad;

    /**
     * The list of sounds that need to be loaded.
     */
    @NotNull
    private final Queue<String> soundsToLoad;

    SoundLoadingTask(@NotNull Assets assets) {
        this.assets = assets;

        songsToLoad = new LinkedList<>(SongFactory.getInstance().getSongNames());
        soundsToLoad = new LinkedList<>(SoundFactory.getInstance().getSoundNames());
    }

    @Override
    public void run() {
        while (!songsToLoad.isEmpty() || !soundsToLoad.isEmpty()) {
            if (!songsToLoad.isEmpty()) {
                SongFactory.getInstance().loadSong(assets.getSoundsManager(), Objects.requireNonNull(songsToLoad.poll()));
            } else {
                SoundFactory.getInstance().loadSound(assets.getSoundsManager(), Objects.requireNonNull(soundsToLoad.poll()));
            }
        }
    }
}
