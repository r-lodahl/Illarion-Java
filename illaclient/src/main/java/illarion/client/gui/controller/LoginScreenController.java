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
package illarion.client.gui.controller;

import com.google.common.util.concurrent.FutureCallback;
import illarion.client.IllaClient;
import illarion.client.LoginService;
import illarion.client.resources.SongFactory;
import illarion.client.util.AudioPlayer;
import illarion.client.util.Lang;
import illarion.client.util.account.AccountSystem;
import illarion.common.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.BackendBinding;
import org.illarion.engine.EventBus;
import org.illarion.engine.Option;
import org.illarion.engine.Window;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.backend.gdx.events.ResolutionChangedEvent;
import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sounds;
import org.illarion.engine.ui.*;
import org.illarion.engine.ui.stage.LoginStage;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is the screen controller that takes care of displaying the login screen.
 */
public final class LoginScreenController implements ScreenController {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final List<String> RESOLUTION_OPTIONS = Arrays.asList(Option.fullscreen, Option.fullscreenHeight,
            Option.fullscreenWidth, Option.fullscreenWidth, Option.fullscreenBitsPerPoint, Option.fullscreenRefreshRate,
            Option.deviceName, Option.windowHeight, Option.windowWidth);

    private final UserInterface gui;
    private final Sounds sounds;
    private final Assets assets;
    private final Window window;
    private final AccountSystem accountSystem;

    private LoginStage stage;

    public LoginScreenController(BackendBinding binding, AccountSystem accountSystem) {
        this.gui = binding.getGui();
        this.sounds = binding.getSounds();
        this.assets = binding.getAssets();
        this.window = binding.getWindow();
        this.accountSystem = accountSystem;
    }

    @Override
    public void onStartScreen() {
        AudioPlayer audioPlayer = AudioPlayer.INSTANCE;
        audioPlayer.initAudioPlayer(sounds);
        Music illarionTheme = SongFactory.getInstance().getSong(2, assets.getSoundsManager());
        audioPlayer.setLastMusic(illarionTheme);
        if (IllaClient.getConfig().getBoolean(Option.musicOn)
                && illarionTheme != null
                && !audioPlayer.isCurrentMusic(illarionTheme)) { // may be null in case OpenAL is not working
            audioPlayer.playMusic(illarionTheme);
        }

        int serverKey = IllaClient.DEFAULT_SERVER.getServerKey();

        LoginService loginService = LoginService.INSTANCE;
        loginService.setCurrentServerByKey(serverKey);
        LoginData[] loginData = loginService.restoreLoginData();

        stage = gui.activateLoginStage(Lang.INSTANCE.getLoginResourceBundle());

        stage.setLoginData(loginData, serverKey);
        stage.setExitListener(IllaClient::exit);
        stage.setLoginListener(this::onLoginIssued);
        stage.setOptionsData(IllaClient.getConfig(), window.getResolutionManager());
        stage.setOptionsSaveListener(this::saveOptions);
        stage.setCharacterSelectionListener(this::onCharacterSelection);
        stage.setCharacterCreationListener(this::onCharacterCreationIssued);
    }

    @Override
    public void onEndScreen() {}

    private void onLoginIssued(LoginData loginData) {
        var loginService = new illarion.client.util.account.services.LoginService(accountSystem);
        loginService.issueLogin(loginData, new FutureCallback<>() {
            @Override
            public void onSuccess(@NotNull CharacterSelectionData[] result) {
                stage.loginSuccessful(result);
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                LOGGER.warn(t.getMessage());
                stage.loginFailed();
            }
        });
    }

    private void onCharacterSelection(CharacterSelectionData character) {
        // call injected LeaveState
    }

    private void onCharacterCreationIssued(CharacterCreationData characterData) {
        //if (!accountSystem.isConnectionDataSet()) {
        //    throw new RuntimeException("Premature Call to Character Creation: Login first");
        //}

        //accountSystem.getPossibleCharacterCreationSpecifications();
    }

    private void saveOptions(Map<String, String> options) {
        var config = IllaClient.getConfig();

        var changedOptions = options.entrySet().stream()
                .filter(option -> isOptionChanged(option.getKey(), option.getValue(), config))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        changedOptions.forEach(config::set);
        config.save();

        // Special handling for options (applied as one event not as several)
        if (changedOptions.keySet().stream().anyMatch(RESOLUTION_OPTIONS::contains)) {
            EventBus.INSTANCE.post(new ResolutionChangedEvent(config));
        }
    }

    private boolean isOptionChanged(String option, String optionValue, ConfigReader config) {
        var configOptionValue = config.getString(option);
        return !optionValue.equals(configOptionValue);
    }
}
