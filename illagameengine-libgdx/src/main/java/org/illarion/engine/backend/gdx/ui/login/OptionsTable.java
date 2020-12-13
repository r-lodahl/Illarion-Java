package org.illarion.engine.backend.gdx.ui.login;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class OptionsTable extends Table {
    private final TextButton saveSettingsButton, cancelSettingsButton;

    public OptionsTable(Skin skin) {
        /* Components */
        CheckBox wasdWalkingCheckbox = new CheckBox("", skin);
        CheckBox disableChatAfterSendingCheckbox = new CheckBox("", skin);
        CheckBox showQuestsOnMapCheckbox = new CheckBox("", skin);
        CheckBox showQuestsOnMiniMapCheckbox = new CheckBox("", skin);
        CheckBox showServerResponseTimeCheckbox = new CheckBox("", skin);
        CheckBox fullScreenCheckbox = new CheckBox("", skin);
        CheckBox showFpsCheckbox = new CheckBox("", skin);
        CheckBox soundEffectsActiveCheckbox = new CheckBox("", skin);
        CheckBox musicActiveCheckbox = new CheckBox("", skin);
        CheckBox accountLoginCheckbox = new CheckBox("", skin);

        SelectBox<String> sendErrorReportsSelection = new SelectBox<>(skin);
        sendErrorReportsSelection.setItems("Ask", "Always", "Never");
        SelectBox<String> translationProviderSelection = new SelectBox<>(skin);
        translationProviderSelection.setItems("None", "MyMemory");
        SelectBox<String> translationDirectionSelection = new SelectBox<>(skin);
        translationDirectionSelection.setItems("German to English", "English to German");
        SelectBox<String> resolutionSelection = new SelectBox<>(skin);
        resolutionSelection.setItems("680 x 480 x 32", "1280 x 1024 x 32", "1920 x 1080 x 32", "3440 x 1440 x 32");

        Slider soundEffectVolume = new Slider(0f, 1f, 0.1f, false, skin);
        Slider musicVolume = new Slider(0f, 1f, 0.1f, false, skin);

        TextField userDefinedServerAddress = new TextField("", skin);
        TextField userDefinedServerPort = new TextField("", skin);
        TextField clientVersion = new TextField("20", skin);

        TextButton resetServerSettingsButton = new TextButton("Reset Server Settings", skin);
        saveSettingsButton = new TextButton("Save", skin);
        cancelSettingsButton = new TextButton("Cancel", skin);
        TextButton generalTabButton = new TextButton("General", skin);
        TextButton graphicsTabButton = new TextButton("Graphics", skin);
        TextButton soundTabButton = new TextButton("Sound", skin);
        TextButton serverTabButton = new TextButton("Server", skin);

        /* Layout Definition */
        VerticalGroup tabButtonRow = new VerticalGroup();
        tabButtonRow.left().top().fill();
        graphicsTabButton.padLeft(5f);
        graphicsTabButton.padRight(5f);
        tabButtonRow.addActor(generalTabButton);
        tabButtonRow.addActor(graphicsTabButton);
        tabButtonRow.addActor(soundTabButton);
        tabButtonRow.addActor(serverTabButton);

        Table generalTab = new Table();
        generalTab.columnDefaults(0).align(Align.left).width(200f);
        generalTab.columnDefaults(1).align(Align.right);
        generalTab.row();
        generalTab.add(new Label("Use WASD for walking", skin));
        generalTab.add(wasdWalkingCheckbox);
        generalTab.row();
        generalTab.add(new Label("Disable chat after sending", skin));
        generalTab.add(disableChatAfterSendingCheckbox);
        generalTab.row();
        generalTab.add(new Label("Show quests on the game map", skin));
        generalTab.add(showQuestsOnMapCheckbox);
        generalTab.row();
        generalTab.add(new Label("Show quests on the mini map", skin));
        generalTab.add(showQuestsOnMiniMapCheckbox);
        generalTab.row();
        generalTab.add(new Label("Send error reports", skin));
        generalTab.add(sendErrorReportsSelection).width(200f);
        generalTab.row();
        generalTab.add(new Label("Show server response time", skin));
        generalTab.add(showServerResponseTimeCheckbox);
        generalTab.row();
        generalTab.add(new Label("Translation provider", skin));
        generalTab.add(translationProviderSelection).width(200f);
        generalTab.row();
        generalTab.add(new Label("Translation direction", skin));
        generalTab.add(translationDirectionSelection).width(200f);

        Table graphicsTab = new Table();
        graphicsTab.columnDefaults(0).align(Align.left).width(200f);
        graphicsTab.columnDefaults(1).align(Align.right);
        graphicsTab.row();
        graphicsTab.add(new Label("Resolution", skin));
        graphicsTab.add(resolutionSelection).width(200f);
        graphicsTab.row();
        graphicsTab.add(new Label("Full-screen mode", skin));
        graphicsTab.add(fullScreenCheckbox);
        graphicsTab.row();
        graphicsTab.add(new Label("Show FPS", skin));
        graphicsTab.add(showFpsCheckbox);

        Table soundTab = new Table();
        soundTab.columnDefaults(0).align(Align.left).width(200f);
        soundTab.columnDefaults(1).align(Align.right);
        soundTab.row();
        soundTab.add(new Label("Effects on", skin));
        soundTab.add(soundEffectsActiveCheckbox);
        soundTab.row();
        soundTab.add(new Label("Effects volume", skin));
        soundTab.add(soundEffectVolume).width(200f);
        soundTab.row();
        soundTab.add(new Label("Music on", skin));
        soundTab.add(musicActiveCheckbox);
        soundTab.row();
        soundTab.add(new Label("Music volume", skin));
        soundTab.add(musicVolume).width(200f);

        Table serverTab = new Table();
        serverTab.columnDefaults(0).align(Align.left).width(200f);
        serverTab.columnDefaults(1).align(Align.right);
        serverTab.row();
        Label serverWarnText = new Label("These settings control the way the client is connecting to the server. Do not" +
                "alter here unless you know what you are doing.", skin);
        serverWarnText.setWrap(true);
        serverTab.add(serverWarnText).colspan(2).width(400f);
        serverTab.row();
        serverTab.add(new Label("Server address", skin));
        serverTab.add(userDefinedServerAddress).width(200f);
        serverTab.row();
        serverTab.add(new Label("Server port", skin));
        serverTab.add(userDefinedServerPort).width(200f);
        serverTab.row();
        serverTab.add(new Label("Client version", skin));
        serverTab.add(clientVersion).width(200f);
        serverTab.row();
        serverTab.add(new Label("Account login", skin));
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

    public void setOnSaveCallback(ClickListener onClick) {
        saveSettingsButton.addListener(onClick);
    }

    public void setOnCancelCallback(ClickListener onClick) {
        cancelSettingsButton.addListener(onClick);
    }
}
