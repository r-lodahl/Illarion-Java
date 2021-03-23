package org.illarion.engine.backend.gdx.ui.login;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import org.illarion.engine.ui.LoginData;
import org.illarion.engine.ui.NullSecureResourceBundle;

import java.util.Arrays;

final class LoginView extends Table {
    private final TextButton optionsButton, creditsButton, exitButton, loginButton;
    private final SelectBox<String> serverSelection;
    private final TextField accountNameField, passwordField;
    private final CheckBox savePasswordCheckbox;

    private LoginData[] loginData;
    private int currentServerIndex;

    private final NullSecureResourceBundle resourceBundle;

    LoginView(Skin skin, NullSecureResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        setFillParent(true);

        /* Actor Setup */
        //Texture titleImageTexture = new Texture("skin/illarion_title.png");
        //Image titleImage = new Image(titleImageTexture);

        serverSelection = new SelectBox<>(skin);
        accountNameField = new TextField("", skin);
        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        savePasswordCheckbox = new CheckBox("", skin);

        loginButton = new TextButton(resourceBundle.getLocalizedString("login"), skin);
        loginButton.pad(20.0f, 70.0f, 20.0f, 70.0f);

        optionsButton = new TextButton(resourceBundle.getLocalizedString("options"), skin);
        creditsButton = new TextButton(resourceBundle.getLocalizedString("credits"), skin);
        exitButton = new TextButton(resourceBundle.getLocalizedString("exit"), skin);

        /* Internal Listener Setup */
        serverSelection.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changeCurrentServer(serverSelection.getSelectedIndex());
            }
        });

        /* Layout Setup */
        //row();
        //add(titleImage);

        row();
        Table loginEditFieldsTable = new Table();
        loginEditFieldsTable.defaults().align(Align.left);
        loginEditFieldsTable.columnDefaults(0).width(120);
        loginEditFieldsTable.columnDefaults(1).width(180);

        loginEditFieldsTable.row();
        loginEditFieldsTable.add(new Label(resourceBundle.getLocalizedString("server"), skin));
        loginEditFieldsTable.add(serverSelection);

        loginEditFieldsTable.row();
        loginEditFieldsTable.add(new Label(resourceBundle.getLocalizedString("account"), skin));
        loginEditFieldsTable.add(accountNameField);

        loginEditFieldsTable.row();
        loginEditFieldsTable.add(new Label(resourceBundle.getLocalizedString("password"), skin));
        loginEditFieldsTable.add(passwordField);

        loginEditFieldsTable.row();
        loginEditFieldsTable.add(new Label(resourceBundle.getLocalizedString("savePassword"), skin));
        loginEditFieldsTable.add(savePasswordCheckbox).width(20.0f).padLeft(-4.0f);
        add(loginEditFieldsTable);

        row();
        add(loginButton);

        row();
        Table buttonRowTable = new Table();
        buttonRowTable.row().width(100.0f);
        buttonRowTable.add(optionsButton);
        buttonRowTable.add(creditsButton).spaceLeft(2.0f).spaceRight(2.0f);
        buttonRowTable.add(exitButton);
        add(buttonRowTable);

        layout();
        pack();
    }

    LoginData getSelectedLoginData() {
        var selectedLoginData = loginData[serverSelection.getSelectedIndex()];

        return new LoginData(
                selectedLoginData.serverId,
                selectedLoginData.server,
                accountNameField.getText(),
                passwordField.getText(),
                savePasswordCheckbox.isChecked()
        );
    }

    void setLoginData(LoginData[] data, int initialServer) {
        loginData = data;
        serverSelection.setItems(Arrays.stream(data)
                .map(x -> resourceBundle.getLocalizedString(x.server))
                .toArray(String[]::new));

        changeCurrentServer(initialServer);
    }

    void setOnOptionsCallback(EventListener onClick) {
        optionsButton.addListener(onClick);
    }

    void setOnExitCallback(EventListener onClick) {
        exitButton.addListener(onClick);
    }

    void setOnCreditsCallback(EventListener onClick) {
        creditsButton.addListener(onClick);
    }

    void setOnLoginCallback(EventListener onClick) {
        loginButton.addListener(onClick);
    }

    private void changeCurrentServer(int index) {
        LoginData data = loginData[index];
        accountNameField.setText(data.username);
        passwordField.setText(data.password);
        savePasswordCheckbox.setChecked(data.savePassword);
    }
}
