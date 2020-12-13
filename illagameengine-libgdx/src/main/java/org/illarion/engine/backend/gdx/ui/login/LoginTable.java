package org.illarion.engine.backend.gdx.ui.login;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class LoginTable extends Table {
    private final TextButton optionsButton, creditsButton, exitButton, loginButton;

    public LoginTable(Skin skin) {
        setFillParent(true);

        /* Actor Setup */
        //Texture titleImageTexture = new Texture("skin/illarion_title.png");
        //Image titleImage = new Image(titleImageTexture);

        SelectBox<String> serverSelection = new SelectBox<>(skin);
        serverSelection.setItems("Game Server", "Dev Server", "Local Server", "User-Defined Server");
        TextField accountNameField = new TextField("", skin);
        TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        CheckBox savePasswordCheckbox = new CheckBox("", skin);

        loginButton = new TextButton("Login", skin);
        loginButton.pad(20f, 70f, 20f, 70f);

        optionsButton = new TextButton("Options", skin);
        creditsButton = new TextButton("Credits", skin);
        exitButton = new TextButton("Exit", skin);

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
