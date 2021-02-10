package org.illarion.engine.backend.gdx.ui.login;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import illarion.common.config.ConfigReader;
import org.apache.commons.lang3.ArrayUtils;
import org.illarion.engine.graphic.ResolutionManager;
import org.illarion.engine.ui.NullSecureResourceBundle;

import java.util.Arrays;

public class OptionsTable extends Table {
    private ResolutionManager resolutionManager;
    private ConfigReader config;

    /* UI-Elements */

    private final TextButton saveSettingsButton, cancelSettingsButton;

    private final CheckBox wasdWalkingCheckbox, disableChatAfterSendingCheckbox, showQuestsOnMapCheckbox,
            showQuestsOnMiniMapCheckbox, showServerResponseTimeCheckbox, fullScreenCheckbox, showFpsCheckbox,
            soundEffectsActiveCheckbox, musicActiveCheckbox, accountLoginCheckbox;

    private final SelectBox<String> sendErrorReportsSelection, translationProviderSelection, translationDirectionSelection;

    private final SelectBox<Integer> refreshRateSelection, bitsPerPointSelection;

    private final SelectBox<ResolutionManager.Device> deviceSelection;

    private final SelectBox<ResolutionManager.WindowSize> resolutionSelection;

    private final Slider soundEffectVolume, musicVolume;

    private final TextField userDefinedServerAddress, userDefinedServerPort, userDefinedClientVersion;

    public OptionsTable(Skin skin, NullSecureResourceBundle resourceBundle) {
        /* Components */
        wasdWalkingCheckbox = new CheckBox("", skin);
        disableChatAfterSendingCheckbox = new CheckBox("", skin);
        showQuestsOnMapCheckbox = new CheckBox("", skin);
        showQuestsOnMiniMapCheckbox = new CheckBox("", skin);
        showServerResponseTimeCheckbox = new CheckBox("", skin);
        fullScreenCheckbox = new CheckBox("", skin);
        showFpsCheckbox = new CheckBox("", skin);
        soundEffectsActiveCheckbox = new CheckBox("", skin);
        musicActiveCheckbox = new CheckBox("", skin);
        accountLoginCheckbox = new CheckBox("", skin);

        sendErrorReportsSelection = new SelectBox<>(skin);
        sendErrorReportsSelection.setItems(
                resourceBundle.getLocalizedString("report.ask"),
                resourceBundle.getLocalizedString("report.always"),
                resourceBundle.getLocalizedString("report.never"));

        translationProviderSelection = new SelectBox<>(skin);
        translationProviderSelection.setItems(
                resourceBundle.getLocalizedString("translation.provider.none"),
                resourceBundle.getLocalizedString("translation.provider.mymemory"));

        translationDirectionSelection = new SelectBox<>(skin);
        translationDirectionSelection.setItems(
                resourceBundle.getLocalizedString("translation.direction.enToDe"),
                resourceBundle.getLocalizedString("translation.direction.deToEn"));

        resolutionSelection = new SelectBox<>(skin);
        refreshRateSelection = new SelectBox<>(skin);
        bitsPerPointSelection = new SelectBox<>(skin);
        deviceSelection = new SelectBox<>(skin);

        soundEffectVolume = new Slider(0f, 100f, 1f, false, skin);
        musicVolume = new Slider(0f, 100f, 1f, false, skin);

        userDefinedServerAddress = new TextField("", skin);
        userDefinedServerPort = new TextField("", skin);
        userDefinedClientVersion = new TextField("", skin);

        TextButton resetServerSettingsButton = new TextButton(resourceBundle.getLocalizedString("serverReset"), skin);
        saveSettingsButton = new TextButton(resourceBundle.getLocalizedString("save"), skin);
        cancelSettingsButton = new TextButton(resourceBundle.getLocalizedString("cancel"), skin);
        TextButton generalTabButton = new TextButton(resourceBundle.getLocalizedString("category.general"), skin);
        TextButton graphicsTabButton = new TextButton(resourceBundle.getLocalizedString("category.graphics"), skin);
        TextButton soundTabButton = new TextButton(resourceBundle.getLocalizedString("category.sound"), skin);
        TextButton serverTabButton = new TextButton(resourceBundle.getLocalizedString("category.server"), skin);

        /* Layout Definition */
        VerticalGroup tabButtonRow = new VerticalGroup();
        tabButtonRow.left().top().fill();
        graphicsTabButton.padLeft(5.0f);
        graphicsTabButton.padRight(5f);
        tabButtonRow.addActor(generalTabButton);
        tabButtonRow.addActor(graphicsTabButton);
        tabButtonRow.addActor(soundTabButton);
        tabButtonRow.addActor(serverTabButton);

        Table generalTab = new Table();
        generalTab.columnDefaults(0).align(Align.left).width(200f);
        generalTab.columnDefaults(1).align(Align.right);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("wasdWalk"), skin));
        generalTab.add(wasdWalkingCheckbox);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("disableChatAfterSending"), skin));
        generalTab.add(disableChatAfterSendingCheckbox);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("showQuestsOnGameMap"), skin));
        generalTab.add(showQuestsOnMapCheckbox);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("showQuestsOnGameMap"), skin));
        generalTab.add(showQuestsOnMiniMapCheckbox);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("report"), skin));
        generalTab.add(sendErrorReportsSelection).width(200f);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("showPing"), skin));
        generalTab.add(showServerResponseTimeCheckbox);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("translation.provider"), skin));
        generalTab.add(translationProviderSelection).width(200f);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("translation.direction"), skin));
        generalTab.add(translationDirectionSelection).width(200f);

        Table graphicsTab = new Table();
        graphicsTab.columnDefaults(0).align(Align.left).width(200f);
        graphicsTab.columnDefaults(1).align(Align.right);
        graphicsTab.row();
        graphicsTab.add(new Label(resourceBundle.getLocalizedString("fullscreen"), skin));
        graphicsTab.add(fullScreenCheckbox);
        graphicsTab.row();
        graphicsTab.add(new Label(resourceBundle.getLocalizedString("device"), skin));
        graphicsTab.add(deviceSelection).width(200f);
        graphicsTab.row();
        graphicsTab.add(new Label(resourceBundle.getLocalizedString("resolution"), skin));
        graphicsTab.add(resolutionSelection).width(200f);
        graphicsTab.row();
        graphicsTab.add(new Label(resourceBundle.getLocalizedString("refreshRate"), skin));
        graphicsTab.add(refreshRateSelection).width(200f);
        graphicsTab.row();
        graphicsTab.add(new Label(resourceBundle.getLocalizedString("bitsPerPoint"), skin));
        graphicsTab.add(bitsPerPointSelection).width(200f);
        graphicsTab.row();
        graphicsTab.add(new Label(resourceBundle.getLocalizedString("showFps"), skin));
        graphicsTab.add(showFpsCheckbox);

        Table soundTab = new Table();
        soundTab.columnDefaults(0).align(Align.left).width(200f);
        soundTab.columnDefaults(1).align(Align.right);
        soundTab.row();
        soundTab.add(new Label(resourceBundle.getLocalizedString("soundOn"), skin));
        soundTab.add(soundEffectsActiveCheckbox);
        soundTab.row();
        soundTab.add(new Label(resourceBundle.getLocalizedString("soundVolume"), skin));
        soundTab.add(soundEffectVolume).width(200f);
        soundTab.row();
        soundTab.add(new Label(resourceBundle.getLocalizedString("musicOn"), skin));
        soundTab.add(musicActiveCheckbox);
        soundTab.row();
        soundTab.add(new Label(resourceBundle.getLocalizedString("musicVolume"), skin));
        soundTab.add(musicVolume).width(200f);

        Table serverTab = new Table();
        serverTab.columnDefaults(0).align(Align.left).width(200f);
        serverTab.columnDefaults(1).align(Align.right);
        serverTab.row();
        Label serverWarnText = new Label(resourceBundle.getLocalizedString("serverWarning"), skin);
        serverWarnText.setWrap(true);
        serverTab.add(serverWarnText).colspan(2).width(400f);
        serverTab.row();
        serverTab.add(new Label(resourceBundle.getLocalizedString("serverAddress"), skin));
        serverTab.add(userDefinedServerAddress).width(200f);
        serverTab.row();
        serverTab.add(new Label(resourceBundle.getLocalizedString("serverPort"), skin));
        serverTab.add(userDefinedServerPort).width(200f);
        serverTab.row();
        serverTab.add(new Label(resourceBundle.getLocalizedString("clientVersion"), skin));
        serverTab.add(userDefinedClientVersion).width(200f);
        serverTab.row();
        serverTab.add(new Label(resourceBundle.getLocalizedString("serverAccountLogin"), skin));
        serverTab.add(accountLoginCheckbox);
        serverTab.row();
        serverTab.add(resetServerSettingsButton).colspan(2).align(Align.right).width(200f);

        Container<Table> dynamicContainer = new Container<>();
        dynamicContainer.top().left();

        HorizontalGroup settingsButtonRow = new HorizontalGroup();
        settingsButtonRow.addActor(saveSettingsButton);
        settingsButtonRow.addActor(cancelSettingsButton);

        add(tabButtonRow).width(150f).height(180f);
        add(dynamicContainer).width(400f).height(180f);
        row();
        add(settingsButtonRow).colspan(2).align(Align.right).padTop(20f);

        /* Listener Definition */
        generalTabButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dynamicContainer.removeActor(graphicsTab, true);
                dynamicContainer.removeActor(soundTab, true);
                dynamicContainer.removeActor(serverTab, true);
                dynamicContainer.setActor(generalTab);
            }
        });

        graphicsTabButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dynamicContainer.removeActor(generalTab, true);
                dynamicContainer.removeActor(soundTab, true);
                dynamicContainer.removeActor(serverTab, true);
                dynamicContainer.setActor(graphicsTab);
            }
        });

        soundTabButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dynamicContainer.removeActor(generalTab, true);
                dynamicContainer.removeActor(graphicsTab, true);
                dynamicContainer.removeActor(serverTab, true);
                dynamicContainer.setActor(soundTab);
            }
        });

        serverTabButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dynamicContainer.removeActor(generalTab, true);
                dynamicContainer.removeActor(soundTab, true);
                dynamicContainer.removeActor(graphicsTab, true);
                dynamicContainer.setActor(serverTab);
            }
        });

        /* Setup */
        setFillParent(true);
        dynamicContainer.setActor(generalTab);
    }

    public void setOptions(ConfigReader config, ResolutionManager resolutionManager) {
        this.resolutionManager = resolutionManager;
        this.config = config;

        int deviceVirtualX = config.getInteger("deviceVirtualX");
        int deviceVirtualY = config.getInteger("deviceVirtualY");
        String deviceName = config.getString("deviceName");

        var devices = resolutionManager.getDevices();
        var currentDevice = Arrays.stream(devices)
                .filter(x -> x.virtualY == deviceVirtualY && x.virtualX == deviceVirtualX && x.name.equals(deviceName))
                .findFirst();

        var usedDevice = currentDevice.orElse(devices[0]);

        deviceSelection.setItems(devices);
        deviceSelection.setSelectedIndex(ArrayUtils.indexOf(devices, usedDevice));

        updateResolutions(usedDevice);
        updateCurrentlySelectedResolution();
        updateFullscreenOptions(deviceSelection.getSelected(), resolutionSelection.getSelected());

        wasdWalkingCheckbox.setChecked(config.getBoolean("wasdWalking"));
        disableChatAfterSendingCheckbox.setChecked(config.getBoolean("disableChatAfterSending"));
        showQuestsOnMapCheckbox.setChecked(config.getBoolean("showQuestsOnGameMap"));
        showQuestsOnMiniMapCheckbox.setChecked(config.getBoolean("showQuestsOnMiniMap"));
        showServerResponseTimeCheckbox.setChecked(config.getBoolean("showPing"));
        fullScreenCheckbox.setChecked(config.getBoolean("fullscreen"));
        showFpsCheckbox.setChecked(config.getBoolean("showFps"));
        soundEffectsActiveCheckbox.setChecked(config.getBoolean("soundOn"));
        musicActiveCheckbox.setChecked(config.getBoolean("musicOn"));
        accountLoginCheckbox.setChecked(config.getBoolean("customServer.accountSystem"));

        sendErrorReportsSelection.setSelectedIndex(config.getInteger("errorReport"));
        translationProviderSelection.setSelectedIndex(config.getInteger("translator_provider"));
        translationDirectionSelection.setSelectedIndex(config.getInteger("translator_direction"));

        soundEffectVolume.setValue(config.getFloat("soundVolume"));
        soundEffectVolume.setValue(config.getFloat("musicVolume"));

        userDefinedServerAddress.setText(config.getString("customServer.domain"));
        userDefinedServerPort.setText(config.getString("customServer.clientVersion"));
        userDefinedClientVersion.setText(config.getString("customServer.port"));

        // configure listener
        deviceSelection.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateResolutions(deviceSelection.getSelected());
                updateCurrentlySelectedResolution();
                updateFullscreenOptions(deviceSelection.getSelected(), resolutionSelection.getSelected());
            }
        });

        resolutionSelection.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateFullscreenOptions(deviceSelection.getSelected(), resolutionSelection.getSelected());
            }
        });

        fullScreenCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setFullscreenOptionsActive(fullScreenCheckbox.isChecked());
                updateCurrentlySelectedResolution();
                updateFullscreenOptions(deviceSelection.getSelected(), resolutionSelection.getSelected());
            }
        });

    }

    private void updateResolutions(ResolutionManager.Device device) {
        var resolutions = resolutionManager.getResolutions(device);

        Arrays.sort(resolutions, (x, y) -> {
            int comparison = Integer.compare(x.width, y.width);

            if (comparison == 0) {
                return Integer.compare(x.height, y.height);
            }

            return comparison;
        });

        resolutionSelection.setItems(resolutions);
    }

    private void updateCurrentlySelectedResolution() {
        boolean isFullscreen = config.getBoolean("fullscreen");
        int fullscreenWidth = config.getInteger("fullscreenWidth");
        int fullscreenHeight = config.getInteger("fullscreenHeight");
        int windowWidth = config.getInteger("windowWidth");
        int windowHeight = config.getInteger("windowHeight");

        var resolutions = resolutionSelection.getItems().toArray(ResolutionManager.WindowSize.class);

        var currentResolutionIndex= Arrays.stream(resolutions)
                .filter(x -> isFullscreen && x.width == fullscreenWidth && x.height == fullscreenHeight ||
                        !isFullscreen && x.width == windowWidth && x.height == windowHeight)
                .findFirst()
                .map(x -> ArrayUtils.indexOf(resolutions, x))
                .orElse(0);

        resolutionSelection.setSelectedIndex(currentResolutionIndex);
    }

    private void updateFullscreenOptions(ResolutionManager.Device device, ResolutionManager.WindowSize resolution) {
        var fullscreenResolutionOptions = resolutionManager.getFullscreenOptions(device, resolution);
        int fullscreenRefreshRate = config.getInteger("fullscreenRefreshRate");
        int fullscreenBitsPerPoints = config.getInteger("fullscreenBitsPerPoint");

        var bitsPerPoints = fullscreenResolutionOptions
                .map(x -> x.bitsPerPoints.toArray(Integer[]::new))
                .orElse(new Integer[0]);
        var refreshRates = fullscreenResolutionOptions
                .map(x -> x.refreshRates.toArray(Integer[]::new))
                .orElse(new Integer[0]);

        refreshRateSelection.setItems(refreshRates);
        refreshRateSelection.setSelectedIndex(
                Arrays.stream(refreshRates)
                        .filter(x -> x == fullscreenRefreshRate)
                        .findFirst()
                        .map(x -> ArrayUtils.indexOf(refreshRates, x))
                        .orElse(0));

        bitsPerPointSelection.setItems(bitsPerPoints);
        bitsPerPointSelection.setSelectedIndex(
                Arrays.stream(bitsPerPoints)
                        .filter(x -> x == fullscreenBitsPerPoints)
                        .findFirst()
                        .map(x -> ArrayUtils.indexOf(bitsPerPoints, x))
                        .orElse(0));
    }

    private void setFullscreenOptionsActive(boolean isActive) {
        refreshRateSelection.setDisabled(!isActive);
        bitsPerPointSelection.setDisabled(!isActive);
    }

    public void setOnSaveCallback(ClickListener onClick) {
        saveSettingsButton.addListener(onClick);
    }

    public void setOnCancelCallback(ClickListener onClick) {
        cancelSettingsButton.addListener(onClick);
    }
}
