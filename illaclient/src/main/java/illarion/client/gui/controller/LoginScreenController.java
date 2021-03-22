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

import illarion.client.IllaClient;
import illarion.client.Login;
import illarion.client.resources.SongFactory;
import illarion.client.util.AudioPlayer;
import illarion.client.util.Lang;
import illarion.common.config.ConfigReader;
import org.illarion.engine.BackendBinding;
import org.illarion.engine.EventBus;
import org.illarion.engine.Option;
import org.illarion.engine.Window;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.backend.gdx.events.ResolutionChangedEvent;
import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sounds;
import org.illarion.engine.ui.LoginData;
import org.illarion.engine.ui.LoginStage;
import org.illarion.engine.ui.UserInterface;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is the screen controller that takes care of displaying the login screen.
 */
public final class LoginScreenController {
    private static final List<String> RESOLUTION_OPTIONS = Arrays.asList(Option.fullscreen, Option.fullscreenHeight,
            Option.fullscreenWidth, Option.fullscreenWidth, Option.fullscreenBitsPerPoint, Option.fullscreenRefreshRate,
            Option.deviceName, Option.windowHeight, Option.windowWidth);

    private final UserInterface gui;
    private final Sounds sounds;
    private final Assets assets;
    private final Window window;

    private LoginStage stage;

    public LoginScreenController(BackendBinding binding) {
        this.gui = binding.getGui();
        this.sounds = binding.getSounds();
        this.assets = binding.getAssets();
        this.window = binding.getWindow();
    }

    public void onStartStage() {
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

        Login login = Login.INSTANCE;
        login.setCurrentServerByKey(serverKey);
        LoginData[] loginData = login.restoreLoginData();

        stage = gui.activateLoginStage(Lang.INSTANCE.getLoginResourceBundle());

        stage.setLoginData(loginData, serverKey);
        stage.setExitListener(IllaClient::exit);
        stage.setLoginListener(login::login);
        stage.setOptionsData(IllaClient.getConfig(), window.getResolutionManager());
        stage.setOptionsSaveListener(this::saveOptions);
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





    public void onEndStage() {
        gui.removeLoginStage();
    }

    /*private void gotoLoginScreen() {
        Login login = Login.getInstance();
        login.restoreServer();
        restoreLoginData();

        if (IllaClient.DEFAULT_SERVER == Servers.Illarionserver) {
            // Do not show server selection
        } else {
            // Do add to server selection:
            //"${login-bundle.server.develop}"
            //"${login-bundle.server.test}"
            //"${login-bundle.server.game}"
            //"${login-bundle.server.custom}"
        }
    }

    private void gotoCreditScreen() {

    }

    private void gotoOptionsScreen() {

    }

    private void gotoCharacterSelectionScreen() {

    }

    private void gotoCharacterCreationScreen() {

    }



    public void onExitButtonClicked(String topic, ButtonClickedEvent event) {
        //IllaClient.ensureExit();
    }

    public void onLoginButtonClicked(String topic, ButtonClickedEvent event) {
        login();
    }

    private void login() {
        // Show "receiving characters popup"
        Login login = Login.getInstance();
        login.setLoginData(nameTxt.getRealText(), passwordTxt.getRealText());

        if (server != null) {
            login.applyServerByKey(server.getSelectedIndex());
        } else {
            login.setServer(Servers.Illarionserver);
        }

        login.storeData(savePassword.isChecked());

        if (login.isCharacterListRequired()) {
            login.requestCharacterList(errorCode -> {
                lastErrorCode = errorCode;
                receivedLoginResponse = true;

                nifty.closePopup(popupReceiveChars.getId());
            });
        } else {
            engine.getSounds().stopMusic(15);
            stateManager.enterState(StateManager.State.PLAYING);
        }
    }

    @NiftyEventSubscriber(id = "server")
    public void onServerChanged(@NotNull String topic, @NotNull DropDownSelectionChangedEvent<String> data) {
        Login.getInstance().applyServerByKey(server.getSelectedIndex());
        restoreLoginData();
    }

    public static String getErrorText(int error) {
        return Lang.getMsg("login.error." + Integer.toString(error));
    }*/
}
