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
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import illarion.client.IllaClient;
import illarion.client.LoginService;
import illarion.client.Servers;
import illarion.client.graphics.AvatarClothManager;
import illarion.client.graphics.AvatarEntity;
import illarion.client.gui.EntityRenderImage;
import illarion.client.resources.CharacterFactory;
import illarion.client.resources.SongFactory;
import illarion.client.util.AudioPlayer;
import illarion.client.util.Lang;
import illarion.client.util.account.AccountSystem;
import illarion.client.util.account.response.AccountGetResponse;
import illarion.client.util.account.response.CharacterGetResponse;
import illarion.client.util.account.response.CharacterItemResponse;
import illarion.common.config.ConfigReader;
import illarion.common.graphics.CharAnimations;
import illarion.common.types.AvatarId;
import illarion.common.types.CharacterId;
import illarion.common.types.Direction;
import illarion.common.types.DisplayCoordinate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.illarion.engine.BackendBinding;
import org.illarion.engine.EventBus;
import org.illarion.engine.Option;
import org.illarion.engine.Window;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.backend.gdx.GdxRenderable;
import org.illarion.engine.backend.gdx.events.ResolutionChangedEvent;
import org.illarion.engine.sound.Music;
import org.illarion.engine.sound.Sounds;
import org.illarion.engine.ui.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * This is the screen controller that takes care of displaying the login screen.
 */
public final class LoginScreenController {
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

    private final ExecutorService requestThreadPool = Executors.newCachedThreadPool();

    public LoginScreenController(BackendBinding binding, AccountSystem accountSystem) {
        this.gui = binding.getGui();
        this.sounds = binding.getSounds();
        this.assets = binding.getAssets();
        this.window = binding.getWindow();
        this.accountSystem = accountSystem;
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

        LoginService loginService = LoginService.INSTANCE;
        loginService.setCurrentServerByKey(serverKey);
        LoginData[] loginData = loginService.restoreLoginData();

        stage = gui.activateLoginStage(Lang.INSTANCE.getLoginResourceBundle());

        stage.setLoginData(loginData, serverKey);
        stage.setExitListener(IllaClient::exit);
        stage.setLoginListener(this::OnLoginIssued);
        stage.setOptionsData(IllaClient.getConfig(), window.getResolutionManager());
        stage.setOptionsSaveListener(this::saveOptions);
    }

    private void OnLoginIssued(LoginData loginData) {
        var usedServer = Arrays.stream(Servers.values())
                .filter(server -> server.getServerName().equals(loginData.server))
                .findFirst()
                .orElse(Servers.Illarionserver);

        // TODO: Testserver is used as a placeholder for LocalServer until merged
        if (usedServer == Servers.Testserver ||
                (usedServer == Servers.Customserver
                && !IllaClient.getConfig().getBoolean(Option.customServerAccountSystem))) {
            LOGGER.debug("AccountSystem not active, executing a direct login");
            return;
        }

        var endpoint = usedServer != Servers.Customserver
                ? AccountSystem.OFFICIAL_ENDPOINT
                : "https://" + usedServer.getServerHost() + "/app.php";

        accountSystem.setAuthentication(loginData);
        accountSystem.setEndpoint(endpoint);

        var accountInformation = accountSystem.getAccountInformation();

        var characterInformation = Futures.transformAsync(accountInformation, (result) -> {
            if (result == null) {
                throw new RuntimeException("Request returned <null> value");
            }

            var serverCharacterList = result.getChars();

            var charInformationRequests = serverCharacterList
                    .stream()
                    .filter(server -> server.getName().equals(usedServer.getServerName()))
                    .findFirst()
                    .map(server -> server.getList()
                            .stream()
                            .map(character ->
                                    accountSystem.getCharacterInformation(server.getId(), character.getCharId()))
                            .collect(Collectors.toList()))
                    .orElse(List.of(Futures.immediateFailedFuture(new RuntimeException("No chars found"))));

            return Futures.successfulAsList(charInformationRequests);
        }, requestThreadPool);

        var charactersLoaded = Futures.transformAsync(characterInformation, (result) -> {
            if (result == null) {
                throw new RuntimeException("Request returned <null> value for character information");
            }

            return Futures.immediateFuture(result.stream()
                    .filter(Objects::nonNull)
                    .map(character -> new CharacterSelectionData(
                            character.getId(),
                            character.getName(),
                            buildCharacterRenderable(character)))
                    .toArray(CharacterSelectionData[]::new));
        }, requestThreadPool);

        Futures.addCallback(charactersLoaded, new FutureCallback<>() {
            @Override
            public void onSuccess(@NotNull CharacterSelectionData[] result) {
                stage.loginSuccessful(result);
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                LOGGER.warn(t.getMessage());
                stage.loginFailed();
            }
        }, requestThreadPool);
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


    public DynamicUiContent buildCharacterRenderable(CharacterGetResponse character) {
        var id = new AvatarId(character.getRace(), character.getRaceType(), Direction.West, CharAnimations.STAND);
        var template = CharacterFactory.getInstance().getTemplate(id.getAvatarId());
        var avatarEntity = new AvatarEntity(template, true);

        var paperDoll = character.getPaperDoll();
        avatarEntity.setClothItem(AvatarClothManager.AvatarClothGroup.Hair, paperDoll.getHairId());
        avatarEntity.setClothItem(AvatarClothManager.AvatarClothGroup.Beard, paperDoll.getBeardId());
        avatarEntity.changeClothColor(AvatarClothManager.AvatarClothGroup.Hair, paperDoll.getHairColour().getColour());
        avatarEntity.changeClothColor(AvatarClothManager.AvatarClothGroup.Beard, paperDoll.getHairColour().getColour());
        avatarEntity.changeBaseColor(paperDoll.getSkinColour().getColour());

        for (var item : character.getItems()) {
            var group = AvatarClothManager.AvatarClothGroup.getFromPositionNumber(item.getPosition());

            if (group == null) {
                continue;
            }

            if (item.getId() == 0) {
                avatarEntity.removeClothItem(group);
                continue;
            }

            avatarEntity.setClothItem(group, item.getId());
        }

        return new EntityRenderImage(avatarEntity);
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
