package org.illarion.engine.backend.gdx.ui.login;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.illarion.engine.ui.login.CharacterSelectionData;
import org.illarion.engine.ui.NullSecureResourceBundle;

import java.util.Optional;

public final class CharacterSelectView extends Table {
    private final TextButton btPlay, btLogout, btCreate;
    private final List<CharacterSelectionData> lvCharacters;

    CharacterSelectView(Skin skin, NullSecureResourceBundle resourceBundle) {
        //Texture titleImageTexture = new Texture("skin/illarion_title.png");
        //Image titleImage = new Image(titleImageTexture);

        lvCharacters = new List<>(skin);

        //Texture characterImageTexture = new Texture("skin/placeholder.png");
        //Image characterImage = new Image(characterImageTexture);

        btPlay = new TextButton(resourceBundle.getLocalizedString("play"), skin);
        btLogout = new TextButton(resourceBundle.getLocalizedString("logout"), skin);
        btCreate = new TextButton(resourceBundle.getLocalizedString("create"), skin);

        /* Layout */
        //row();
        //add(titleImage);

        row();
        Table characterSelectGroup = new Table();
        characterSelectGroup.add(lvCharacters).expandX().fill();
        //characterSelectGroup.add(characterImage);
        add(characterSelectGroup).fillX();

        row();
        HorizontalGroup buttonRow = new HorizontalGroup();
        buttonRow.addActor(btPlay);
        buttonRow.addActor(btCreate);
        buttonRow.addActor(btLogout);
        add(buttonRow);
    }

    public Optional<CharacterSelectionData> getSelectedCharacter() {
        return Optional.ofNullable(lvCharacters.getSelected());
    }

    public void setCharacters(CharacterSelectionData[] characters) {
        lvCharacters.setItems(characters);
    }

    public void setOnPlayCallback(ClickListener onClick) {
        btPlay.addListener(onClick);
    }
    public void setOnCreateCharacterCallback(ClickListener onClick) {
        btCreate.addListener(onClick);
    }
    public void setOnLogoutCallback(ClickListener onClick) {
        btLogout.addListener(onClick);
    }
}
