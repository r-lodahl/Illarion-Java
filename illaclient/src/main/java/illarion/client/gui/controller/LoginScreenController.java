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
import org.illarion.engine.assets.Assets;
import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sounds;
import org.illarion.engine.ui.LoginData;
import org.illarion.engine.ui.LoginStage;
import org.illarion.engine.ui.UserInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the screen controller that takes care of displaying the login screen.
 */
public final class LoginScreenController {
    /**
     * This is the logging instance for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginScreenController.class);

    private final UserInterface gui;
    private final Sounds sounds;
    private final Assets assets;

    private LoginStage stage;

    public LoginScreenController(Sounds sounds, Assets assets, UserInterface gui) {
        this.gui = gui;
        this.sounds = sounds;
        this.assets = assets;
    }

    public void onStartStage() {
        AudioPlayer audioPlayer = AudioPlayer.getInstance();
        audioPlayer.initAudioPlayer(sounds);
        Music illarionTheme = SongFactory.getInstance().getSong(2, assets.getSoundsManager());
        audioPlayer.setLastMusic(illarionTheme);
        if (IllaClient.getConfig().getBoolean("musicOn")) {
            if (illarionTheme != null) {
                if (!audioPlayer.isCurrentMusic(illarionTheme)) {
                    // may be null in case OpenAL is not working
                    audioPlayer.playMusic(illarionTheme);
                }
            }
        }

        Login login = Login.INSTANCE;
        LoginData[] loginData = login.restoreLoginData();

        stage = gui.activateLoginStage();

        stage.setLoginData(loginData);
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
    public void onServerChanged(@Nonnull String topic, @Nonnull DropDownSelectionChangedEvent<String> data) {
        Login.getInstance().applyServerByKey(server.getSelectedIndex());
        restoreLoginData();
    }

    public static String getErrorText(int error) {
        return Lang.getMsg("login.error." + Integer.toString(error));
    }*/
}
