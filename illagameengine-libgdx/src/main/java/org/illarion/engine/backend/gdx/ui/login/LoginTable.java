package org.illarion.engine.backend.gdx.ui.login;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import org.illarion.engine.ui.LoginData;

import java.util.Arrays;

public class LoginTable extends Table {
    private final TextButton optionsButton, creditsButton, exitButton, loginButton;
    private final SelectBox<String> serverSelection;
    private final TextField accountNameField, passwordField;
    private final CheckBox savePasswordCheckbox;

    private LoginData[] loginData;
    private int currentServerIndex;

    public LoginTable(Skin skin) {
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

        loginButton = new TextButton("Login", skin);
        loginButton.pad(20f, 70f, 20f, 70f);

        optionsButton = new TextButton("Options", skin);
        creditsButton = new TextButton("Credits", skin);
        exitButton = new TextButton("Exit", skin);

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
        loginEditFieldsTable.add(new Label("Server:", skin));
        loginEditFieldsTable.add(serverSelection);

        loginEditFieldsTable.row();
        loginEditFieldsTable.add(new Label("Accout name:", skin));
        loginEditFieldsTable.add(accountNameField);

        loginEditFieldsTable.row();
        loginEditFieldsTable.add(new Label("Password:", skin));
        loginEditFieldsTable.add(passwordField);

        loginEditFieldsTable.row();
        loginEditFieldsTable.add(new Label("Save password", skin));
        loginEditFieldsTable.add(savePasswordCheckbox).width(20f).padLeft(-4f);
        add(loginEditFieldsTable);

        row();
        add(loginButton);

        row();
        Table buttonRowTable = new Table();
        buttonRowTable.row().width(100f);
        buttonRowTable.add(optionsButton);
        buttonRowTable.add(creditsButton).spaceLeft(2f).spaceRight(2f);
        buttonRowTable.add(exitButton);
        add(buttonRowTable);

        layout();
        pack();
    }

    public void setLoginData(LoginData[] data, int initialServer) {
        loginData = data;
        serverSelection.setItems(Arrays.stream(data).map(x -> x.server).toArray(String[]::new));

        changeCurrentServer(initialServer);
    }

    private void changeCurrentServer(int index) {
        LoginData data = loginData[index];
        accountNameField.setText(data.username);
        passwordField.setText(data.password);
        savePasswordCheckbox.setChecked(data.savePassword);
    }

    public void setOnOptionsCallback(ClickListener onClick) {
        optionsButton.addListener(onClick);
    }

    public void setOnExitCallback(ClickListener onClick) {
        exitButton.addListener(onClick);
    }

    public void setOnCreditsCallback(ClickListener onClick) {
        creditsButton.addListener(onClick);
    }

    public void setOnLoginCallback(ClickListener onClick) {
        loginButton.addListener(onClick);
    }
}
