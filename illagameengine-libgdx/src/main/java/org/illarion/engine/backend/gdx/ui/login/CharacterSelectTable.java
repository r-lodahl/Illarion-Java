package org.illarion.engine.backend.gdx.ui.login;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CharacterSelectTable extends Table {
    private final TextButton playButton, logoutButton, newCharacterButton;

    public CharacterSelectTable(Skin skin) {
        //Texture titleImageTexture = new Texture("skin/illarion_title.png");
        //Image titleImage = new Image(titleImageTexture);

        List<String> characterSelectBox = new List<>(skin);
        characterSelectBox.setItems("Franz Tester", "Klaus von Testenstein", "Testosterus");

        //Texture characterImageTexture = new Texture("skin/placeholder.png");
        //Image characterImage = new Image(characterImageTexture);

        playButton = new TextButton("Play", skin);
        logoutButton = new TextButton("Logout", skin);
        newCharacterButton = new TextButton("Create Character", skin);

        /* Layout */
        //row();
        //add(titleImage);

        row();
        Table characterSelectGroup = new Table();
        characterSelectGroup.add(characterSelectBox).expandX().fill();
        //characterSelectGroup.add(characterImage);
        add(characterSelectGroup).fillX();

        row();
        HorizontalGroup buttonRow = new HorizontalGroup();
        buttonRow.addActor(playButton);
        buttonRow.addActor(newCharacterButton);
        buttonRow.addActor(logoutButton);
        add(buttonRow);
    }

    public void setOnPlayCallback(ClickListener onClick) {
        playButton.addListener(onClick);
    }
    public void setOnCreateCharacterCallback(ClickListener onClick) {
        newCharacterButton.addListener(onClick);
    }
    public void setOnLogoutCallback(ClickListener onClick) {
        logoutButton.addListener(onClick);
    }
}
