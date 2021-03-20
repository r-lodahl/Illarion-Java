package org.illarion.engine.backend.gdx.ui.login;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import illarion.common.config.ConfigReader;
import org.apache.commons.lang3.ArrayUtils;
import org.illarion.engine.Option;
import org.illarion.engine.graphic.ResolutionManager;
import org.illarion.engine.ui.NullSecureResourceBundle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OptionsView extends Table {
    private ResolutionManager resolutionManager;
    private ConfigReader config;

    //region + UI Definition +
    private final Table generalTab, graphicsTab, soundTab, serverTab;

    private final TextButton btSaveSettings, btCancelSettings, btGraphicsTab, btGeneralTab, btSoundTab, btServerTab;

    private final CheckBox cbWasdWalking, cbDisableChatAfterSending, cbShowQuestsOnMap, cbShowQuestsOnMiniMap,
            cbShowPing, cbUseFullscreen, cbShowFps, cbSoundEffectsActive, cbMusicActive, cbAccountLogin,
            cbSendErrorReports;

    private final SelectBox<String> sbTranslationProvider, sbTranslationDirection;

    private final SelectBox<Integer> sbRefreshRate, sbBitDepth;

    private final SelectBox<ResolutionManager.Device> sbDevice;

    private final SelectBox<ResolutionManager.WindowSize> sbResolution;

    private final Slider slSoundEffectsVolume, slMusicVolume;

    private final TextField tbUserDefinedServerAddress, tbUserDefinedServerPort, tbUserDefinedServerVersion;

    private final Container<Table> contentRoot;
    //endregion

    public Map<String, String> getCurrentSettings() {
        var settings = new HashMap<String, String>();

        var device = sbDevice.getSelected();
        var isFullscreen = cbUseFullscreen.isChecked();

        settings.put(Option.wasdWalking, Boolean.toString(cbWasdWalking.isChecked()));
        settings.put(Option.disableChatAfterSending, Boolean.toString(cbDisableChatAfterSending.isChecked()));
        settings.put(Option.showQuestsOnGameMap, Boolean.toString(cbShowQuestsOnMap.isChecked()));
        settings.put(Option.showQuestsOnMiniMap, Boolean.toString(cbShowQuestsOnMiniMap.isChecked()));
        settings.put(Option.showPing, Boolean.toString(cbShowPing.isChecked()));
        settings.put(Option.fullscreen, Boolean.toString(isFullscreen));
        settings.put(Option.deviceVirtualX, Integer.toString(device.virtualX));
        settings.put(Option.deviceVirtualY, Integer.toString(device.virtualY));
        settings.put(Option.deviceName, device.name);
        settings.put(Option.showFps, Boolean.toString(cbShowFps.isChecked()));
        settings.put(Option.customServerAccountSystem, Boolean.toString(cbAccountLogin.isChecked()));
        settings.put(Option.customServerDomain, tbUserDefinedServerAddress.getText());
        settings.put(Option.customServerClientVersion, tbUserDefinedServerVersion.getText());
        settings.put(Option.customServerPort, tbUserDefinedServerPort.getText());
        settings.put(Option.errorReport, Boolean.toString(cbSendErrorReports.isChecked()));
        settings.put(Option.translatorProvider, sbTranslationProvider.getSelected());
        settings.put(Option.translatorDirection, sbTranslationDirection.getSelected());
        settings.put(Option.soundVolume, Float.toString(slSoundEffectsVolume.getValue()));
        settings.put(Option.musicVolume, Float.toString(slMusicVolume.getValue()));
        settings.put(Option.soundOn, Boolean.toString(cbSoundEffectsActive.isChecked()));
        settings.put(Option.musicOn, Boolean.toString(cbMusicActive.isChecked()));
        settings.put(Option.fullscreenWidth, isFullscreen
                ? Integer.toString(sbResolution.getSelected().width)
                : config.getString(Option.fullscreenWidth));
        settings.put(Option.fullscreenHeight, isFullscreen
                ? Integer.toString(sbResolution.getSelected().height)
                : config.getString(Option.fullscreenHeight));
        settings.put(Option.fullscreenRefreshRate, isFullscreen
                ? Integer.toString(sbRefreshRate.getSelected())
                : config.getString(Option.fullscreenRefreshRate));
        settings.put(Option.fullscreenBitsPerPoint, isFullscreen
                ? Integer.toString(sbBitDepth.getSelected())
                : config.getString(Option.fullscreenBitsPerPoint));
        settings.put(Option.windowWidth, isFullscreen
                ? config.getString(Option.windowWidth)
                : Integer.toString(sbResolution.getSelected().width));
        settings.put(Option.windowHeight, isFullscreen
                ? config.getString(Option.fullscreenHeight)
                : Integer.toString(sbResolution.getSelected().height));

        return settings;
    }

    public void setOnSaveCallback(ClickListener listener) {
        btSaveSettings.addListener(listener);
    }

    public void setOnCancelCallback(ClickListener listener) {
        btCancelSettings.addListener(listener);
    }

    public void setOptions(ConfigReader config, ResolutionManager resolutionManager) {
        this.resolutionManager = resolutionManager;
        this.config = config;

        setupTabSwitchingListeners();

        setupResolutionSelection(config, resolutionManager);

        setupConfigDependentGui(config);
    }

    private void setupTabSwitchingListeners() {
        var tabs = new Table[] {generalTab, graphicsTab, soundTab, serverTab};

        setupTabSwitchingListener(btGeneralTab, generalTab, tabs);
        setupTabSwitchingListener(btGraphicsTab, graphicsTab, tabs);
        setupTabSwitchingListener(btSoundTab, soundTab, tabs);
        setupTabSwitchingListener(btServerTab, serverTab, tabs);
    }

    private void setupTabSwitchingListener(TextButton tabButton, Table tab, Table[] tabs) {
        tabButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switchTab(tabs, tab, contentRoot);
            }
        });
    }

    private void switchTab(Table[] tabs, Table newTab, Container<Table> container) {
        for (var tab : tabs) {
            if (tab != newTab) {
                container.removeActor(tab, true);
            }
        }
        container.setActor(newTab);
    }

    private void setupResolutionSelection(ConfigReader config, ResolutionManager resolutionManager) {
        int deviceVirtualX = config.getInteger(Option.deviceVirtualX);
        int deviceVirtualY = config.getInteger(Option.deviceVirtualY);
        String deviceName = config.getString(Option.deviceName);

        var devices = resolutionManager.getDevices();
        var currentDevice = Arrays.stream(devices)
                .filter(x -> x.virtualY == deviceVirtualY && x.virtualX == deviceVirtualX && x.name.equals(deviceName))
                .findFirst();

        var usedDevice = currentDevice.orElse(devices[0]);

        sbDevice.setItems(devices);
        sbDevice.setSelectedIndex(ArrayUtils.indexOf(devices, usedDevice));

        updateResolutions(usedDevice);
        updateCurrentlySelectedResolution();
        updateFullscreenOptions(sbDevice.getSelected(), sbResolution.getSelected());

        sbDevice.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateResolutions(sbDevice.getSelected());
                updateCurrentlySelectedResolution();
                updateFullscreenOptions(sbDevice.getSelected(), sbResolution.getSelected());
            }
        });

        sbResolution.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateFullscreenOptions(sbDevice.getSelected(), sbResolution.getSelected());
            }
        });

        cbUseFullscreen.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setFullscreenOptionsActive(cbUseFullscreen.isChecked());
                updateCurrentlySelectedResolution();
                updateFullscreenOptions(sbDevice.getSelected(), sbResolution.getSelected());
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

        sbResolution.setItems(resolutions);
    }

    private void updateCurrentlySelectedResolution() {
        boolean isFullscreen = config.getBoolean(Option.fullscreen);
        int fullscreenWidth = config.getInteger(Option.fullscreenWidth);
        int fullscreenHeight = config.getInteger(Option.fullscreenHeight);
        int windowWidth = config.getInteger(Option.windowWidth);
        int windowHeight = config.getInteger(Option.windowHeight);

        var resolutions = sbResolution.getItems().toArray(ResolutionManager.WindowSize.class);

        var currentResolutionIndex= Arrays.stream(resolutions)
                .filter(x -> isFullscreen && x.width == fullscreenWidth && x.height == fullscreenHeight ||
                        !isFullscreen && x.width == windowWidth && x.height == windowHeight)
                .findFirst()
                .map(x -> ArrayUtils.indexOf(resolutions, x))
                .orElse(0);

        sbResolution.setSelectedIndex(currentResolutionIndex);
    }

    private void updateFullscreenOptions(ResolutionManager.Device device, ResolutionManager.WindowSize resolution) {
        var fullscreenResolutionOptions = resolutionManager.getFullscreenOptions(device, resolution);
        int fullscreenRefreshRate = config.getInteger(Option.fullscreenRefreshRate);
        int fullscreenBitsPerPoints = config.getInteger(Option.fullscreenBitsPerPoint);

        var bitsPerPoints = fullscreenResolutionOptions
                .map(x -> x.bitsPerPoints.toArray(Integer[]::new))
                .orElse(new Integer[0]);
        var refreshRates = fullscreenResolutionOptions
                .map(x -> x.refreshRates.toArray(Integer[]::new))
                .orElse(new Integer[0]);

        sbRefreshRate.setItems(refreshRates);
        sbRefreshRate.setSelectedIndex(
                Arrays.stream(refreshRates)
                        .filter(x -> x == fullscreenRefreshRate)
                        .findFirst()
                        .map(x -> ArrayUtils.indexOf(refreshRates, x))
                        .orElse(0));

        sbBitDepth.setItems(bitsPerPoints);
        sbBitDepth.setSelectedIndex(
                Arrays.stream(bitsPerPoints)
                        .filter(x -> x == fullscreenBitsPerPoints)
                        .findFirst()
                        .map(x -> ArrayUtils.indexOf(bitsPerPoints, x))
                        .orElse(0));
    }

    private void setFullscreenOptionsActive(boolean isActive) {
        sbRefreshRate.setDisabled(!isActive);
        sbBitDepth.setDisabled(!isActive);
    }

    //region + Only UI Definition below +
    public OptionsView(Skin skin, NullSecureResourceBundle resourceBundle) {
        cbWasdWalking = new CheckBox("", skin);
        cbDisableChatAfterSending = new CheckBox("", skin);
        cbShowQuestsOnMap = new CheckBox("", skin);
        cbShowQuestsOnMiniMap = new CheckBox("", skin);
        cbShowPing = new CheckBox("", skin);
        cbUseFullscreen = new CheckBox("", skin);
        cbShowFps = new CheckBox("", skin);
        cbSoundEffectsActive = new CheckBox("", skin);
        cbMusicActive = new CheckBox("", skin);
        cbAccountLogin = new CheckBox("", skin);
        cbSendErrorReports = new CheckBox("", skin);

        sbTranslationProvider = new SelectBox<>(skin);
        sbTranslationProvider.setItems(
                resourceBundle.getLocalizedString("translation.provider.none"),
                resourceBundle.getLocalizedString("translation.provider.mymemory"));

        sbTranslationDirection = new SelectBox<>(skin);
        sbTranslationDirection.setItems(
                resourceBundle.getLocalizedString("translation.direction.enToDe"),
                resourceBundle.getLocalizedString("translation.direction.deToEn"));

        sbResolution = new SelectBox<>(skin);
        sbRefreshRate = new SelectBox<>(skin);
        sbBitDepth = new SelectBox<>(skin);
        sbDevice = new SelectBox<>(skin);

        slSoundEffectsVolume = new Slider(0.0f, 100.0f, 1.0f, false, skin);
        slMusicVolume = new Slider(0.0f, 100.0f, 1.0f, false, skin);

        tbUserDefinedServerAddress = new TextField("", skin);
        tbUserDefinedServerPort = new TextField("", skin);
        tbUserDefinedServerVersion = new TextField("", skin);

        TextButton resetServerSettingsButton = new TextButton(resourceBundle.getLocalizedString("serverReset"), skin);
        btSaveSettings = new TextButton(resourceBundle.getLocalizedString("save"), skin);
        btCancelSettings = new TextButton(resourceBundle.getLocalizedString("cancel"), skin);
        btGeneralTab = new TextButton(resourceBundle.getLocalizedString("category.general"), skin);
        btGraphicsTab = new TextButton(resourceBundle.getLocalizedString("category.graphics"), skin);
        btSoundTab = new TextButton(resourceBundle.getLocalizedString("category.sound"), skin);
        btServerTab = new TextButton(resourceBundle.getLocalizedString("category.server"), skin);

        VerticalGroup tabButtonRow = new VerticalGroup();
        tabButtonRow.left().top().fill();
        btGraphicsTab.padLeft(5.0f);
        btGraphicsTab.padRight(5.0f);
        tabButtonRow.addActor(btGeneralTab);
        tabButtonRow.addActor(btGraphicsTab);
        tabButtonRow.addActor(btSoundTab);
        tabButtonRow.addActor(btServerTab);

        generalTab = new Table();
        generalTab.columnDefaults(0).align(Align.left).width(200.0f);
        generalTab.columnDefaults(1).align(Align.right);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("wasdWalk"), skin));
        generalTab.add(cbWasdWalking);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("disableChatAfterSending"), skin));
        generalTab.add(cbDisableChatAfterSending);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("showQuestsOnGameMap"), skin));
        generalTab.add(cbShowQuestsOnMap);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("showQuestsOnGameMap"), skin));
        generalTab.add(cbShowQuestsOnMiniMap);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("report"), skin));
        generalTab.add(cbSendErrorReports).width(200.0f);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("translation.provider"), skin));
        generalTab.add(sbTranslationProvider).width(200.0f);
        generalTab.row();
        generalTab.add(new Label(resourceBundle.getLocalizedString("translation.direction"), skin));
        generalTab.add(sbTranslationDirection).width(200.0f);

        graphicsTab = new Table();
        graphicsTab.columnDefaults(0).align(Align.left).width(200.0f);
        graphicsTab.columnDefaults(1).align(Align.right);
        graphicsTab.row();
        graphicsTab.add(new Label(resourceBundle.getLocalizedString("fullscreen"), skin));
        graphicsTab.add(cbUseFullscreen);
        graphicsTab.row();
        graphicsTab.add(new Label(resourceBundle.getLocalizedString("device"), skin));
        graphicsTab.add(sbDevice).width(200.0f);
        graphicsTab.row();
        graphicsTab.add(new Label(resourceBundle.getLocalizedString("resolution"), skin));
        graphicsTab.add(sbResolution).width(200.0f);
        graphicsTab.row();
        graphicsTab.add(new Label(resourceBundle.getLocalizedString("refreshRate"), skin));
        graphicsTab.add(sbRefreshRate).width(200.0f);
        graphicsTab.row();
        graphicsTab.add(new Label(resourceBundle.getLocalizedString("bitsPerPoint"), skin));
        graphicsTab.add(sbBitDepth).width(200.0f);
        graphicsTab.row();
        graphicsTab.add(new Label(resourceBundle.getLocalizedString("showFps"), skin));
        graphicsTab.add(cbShowFps);

        soundTab = new Table();
        soundTab.columnDefaults(0).align(Align.left).width(200.0f);
        soundTab.columnDefaults(1).align(Align.right);
        soundTab.row();
        soundTab.add(new Label(resourceBundle.getLocalizedString("soundOn"), skin));
        soundTab.add(cbSoundEffectsActive);
        soundTab.row();
        soundTab.add(new Label(resourceBundle.getLocalizedString("soundVolume"), skin));
        soundTab.add(slSoundEffectsVolume).width(200.0f);
        soundTab.row();
        soundTab.add(new Label(resourceBundle.getLocalizedString("musicOn"), skin));
        soundTab.add(cbMusicActive);
        soundTab.row();
        soundTab.add(new Label(resourceBundle.getLocalizedString("musicVolume"), skin));
        soundTab.add(slMusicVolume).width(200.0f);

        serverTab = new Table();
        serverTab.columnDefaults(0).align(Align.left).width(200.0f);
        serverTab.columnDefaults(1).align(Align.right);
        serverTab.row();
        serverTab.add(new Label(resourceBundle.getLocalizedString("showPing"), skin));
        serverTab.add(cbShowPing);
        serverTab.row();
        Label serverWarnText = new Label(resourceBundle.getLocalizedString("serverWarning"), skin);
        serverWarnText.setWrap(true);
        serverTab.add(serverWarnText).colspan(2).width(400.0f);
        serverTab.row();
        serverTab.add(new Label(resourceBundle.getLocalizedString("serverAddress"), skin));
        serverTab.add(tbUserDefinedServerAddress).width(200.0f);
        serverTab.row();
        serverTab.add(new Label(resourceBundle.getLocalizedString("serverPort"), skin));
        serverTab.add(tbUserDefinedServerPort).width(200.0f);
        serverTab.row();
        serverTab.add(new Label(resourceBundle.getLocalizedString("clientVersion"), skin));
        serverTab.add(tbUserDefinedServerVersion).width(200.0f);
        serverTab.row();
        serverTab.add(new Label(resourceBundle.getLocalizedString("serverAccountLogin"), skin));
        serverTab.add(cbAccountLogin);
        serverTab.row();
        serverTab.add(resetServerSettingsButton).colspan(2).align(Align.right).width(200.0f);

        contentRoot = new Container<>();
        contentRoot.top().left();

        HorizontalGroup settingsButtonRow = new HorizontalGroup();
        settingsButtonRow.addActor(btSaveSettings);
        settingsButtonRow.addActor(btCancelSettings);

        add(tabButtonRow).width(150.0f).height(180.0f);
        add(contentRoot).width(400.0f).height(180.0f);
        row();
        add(settingsButtonRow).colspan(2).align(Align.right).padTop(20.0f);

        setFillParent(true);
        contentRoot.setActor(generalTab);
    }

    private void setupConfigDependentGui(ConfigReader config) {
        cbWasdWalking.setChecked(config.getBoolean(Option.wasdWalking));
        cbDisableChatAfterSending.setChecked(config.getBoolean(Option.disableChatAfterSending));
        cbShowQuestsOnMap.setChecked(config.getBoolean(Option.showQuestsOnGameMap));
        cbShowQuestsOnMiniMap.setChecked(config.getBoolean(Option.showQuestsOnMiniMap));
        cbShowPing.setChecked(config.getBoolean(Option.showPing));
        cbUseFullscreen.setChecked(config.getBoolean(Option.fullscreen));
        cbShowFps.setChecked(config.getBoolean(Option.showFps));
        cbSoundEffectsActive.setChecked(config.getBoolean(Option.soundOn));
        cbMusicActive.setChecked(config.getBoolean(Option.musicOn));
        cbAccountLogin.setChecked(config.getBoolean(Option.customServerAccountSystem));
        cbSendErrorReports.setChecked(config.getBoolean(Option.errorReport));

        sbTranslationProvider.setSelectedIndex(config.getInteger(Option.translatorProvider));
        sbTranslationDirection.setSelectedIndex(config.getInteger(Option.translatorDirection));

        slSoundEffectsVolume.setValue(config.getFloat(Option.soundVolume));
        slMusicVolume.setValue(config.getFloat(Option.musicVolume));

        tbUserDefinedServerAddress.setText(config.getString(Option.customServerDomain));
        tbUserDefinedServerPort.setText(config.getString(Option.customServerClientVersion));
        tbUserDefinedServerVersion.setText(config.getString(Option.customServerPort));
    }
    //endregion
}
