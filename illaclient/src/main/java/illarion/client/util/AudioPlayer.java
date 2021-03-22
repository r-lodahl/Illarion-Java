/*
 * This file is part of the Illarion project.
 *
 * Copyright 2015 - Illarion e.V.
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
package illarion.client.util;

import illarion.client.IllaClient;
import illarion.client.world.Player;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.config.ConfigReader;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicPatternSubscriber;
import org.illarion.engine.Option;
import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sound;
import org.illarion.engine.sound.Sounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


/**
 * This class is the base for interacting with GdxSounds.
 * Only one AudioPlayer may exist at any time
 *
 * Allows the setting of specific tracks and sounds
 * Tracks the volume:
 *      can be manually set for temporary effect or
 *      permanently changed through config changes
 *
 * @author Mike Kay
 */
public enum AudioPlayer {

    INSTANCE;

    @NotNull
    private Sounds sounds;

    @Nullable
    private Music lastMusic;

    /**
     * This variable is set {@code true} once the music player is initialized.
     */
    private boolean init;

    /**
     * Initiates the AudioPlayer and subscribes to the proper config changes
     * Needs to be called before the AudioPlayer can be used
     * If the INSTANCE has already been initialized, does nothing
     * @param sounds    The engine's sound system
     */
    public void initAudioPlayer(@NotNull Sounds sounds) {
        if (init){
            return;
        }

        INSTANCE.sounds = sounds;
        AnnotationProcessor.process(this);
        updateSettings(null, IllaClient.getConfig());

        init = true;
    }

    /**
     * Checks if the given track is being played
     *
     * @param music the track to be compared against
     * @return  true if music is the track  being played
     */
    public boolean isCurrentMusic(Music music){
        return sounds.isMusicPlaying(music);
    }

    public boolean isCurrentSound(Sound sound, int handle){
        return sounds.isSoundPlaying(sound, handle);
    }

    /**
     * Plays the given track
     * Sets the last track to the given track
     * @param music the track to be played
     */
    public void playMusic(Music music){
        lastMusic = music;
        sounds.playMusic(music, 0, 0);
    }

    /**
     * Plays the given sound
     * @param sound     the sound to be played
     * @param volume    the volume to the play sound at
     */
    public void playSound(Sound sound, float volume){
        sounds.playSound(sound, volume);
    }

    /**
     * Sets the volume of the music played
     * Does NOT save the change to the config file
     * @param volume the new value of the volume
     */
    public void setMusicVolume(float volume){
        sounds.setMusicVolume(volume / Player.MAX_CLIENT_VOL);
    }

    /**
     * Gets the current volume used by the music
     */
    public float getMusicVolume(){
        return sounds.getMusicVolume();
    }

    /**
     * Sets the volume of the sound effects played
     * Does not save the change to the config file
     * @param volume the new value of the volume
     */
    public void setSoundVolume(float volume){
        sounds.setSoundVolume(volume / Player.MAX_CLIENT_VOL);
    }
    /**
     * Gets the current volume used by the sound effects
     */
    public float getSoundVolume(){
        return sounds.getSoundVolume();
    }


    /**
     * Plays the last Music given
     * Does not resume from last point
     * lastMusic is set through playMusic or setLastMusic
     */
    public void playLastMusic() {
        Optional.ofNullable(lastMusic)
                .ifPresent(music -> sounds.playMusic(music, 0, 0));
    }

    /**
     * Sets the value of the last music played without actually playing it
     * Used in conjunction with playLastMusic()
     * @param music the Music value to be played
     */
    public void setLastMusic(Music music){
        lastMusic = music;
    }

    public Optional<Music> getLastMusic(){
        return Optional.ofNullable(lastMusic);
    }
    public void stopMusic(){
        sounds.stopMusic(0);
    }

    public void stopSound(Sound sound){
        sounds.stopSound(sound);
    }

    public static AudioPlayer getInstance(){
        return INSTANCE;
    }

    @EventTopicPatternSubscriber(topicPattern = "((music)|(sound))((On)|(Volume))")
    public void onEvent(@NotNull String topic, @NotNull ConfigChangedEvent data) {
        updateSettings(topic, data.getConfig());
    }

    private void updateSettings(@Nullable String setting, @NotNull ConfigReader config) {
        if ((setting == null) || Option.musicOn.equals(setting)) {
            boolean musicEnabled = config.getBoolean(Option.musicOn);
            if (musicEnabled) {
                float musicVolume = config.getFloat(Option.musicVolume) / Player.MAX_CLIENT_VOL;
                sounds.setMusicVolume(musicVolume);
                playLastMusic();
            } else {
                sounds.setMusicVolume(0.0f);
            }
        } else if (Option.musicVolume.equals(setting)) {
            float musicVolume = config.getFloat(Option.musicVolume) / Player.MAX_CLIENT_VOL;
            if (config.getBoolean(Option.musicOn)) {
                float oldMusicVolume = sounds.getMusicVolume();

                sounds.setMusicVolume(musicVolume);

                // Sounds stops playing if the current music if volume drops to 0
                if (oldMusicVolume == 0.0f) {
                    playLastMusic();
                }
            }
        }

        if ((setting == null) || Option.soundOn.equals(setting)) {
            boolean soundEnabled = config.getBoolean(Option.soundOn);
            if (soundEnabled) {
                float soundVolume = config.getFloat(Option.soundVolume) / Player.MAX_CLIENT_VOL;
                sounds.setSoundVolume(soundVolume);
            } else {
                sounds.setSoundVolume(0.0f);
            }
        } else if (Option.soundVolume.equals(setting)) {
            float soundVolume = config.getFloat(Option.soundVolume) / Player.MAX_CLIENT_VOL;
            if (config.getBoolean(Option.soundOn)) {
                sounds.setSoundVolume(soundVolume);
            }
        }
    }
}
