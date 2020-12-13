package org.illarion.engine.backend.gdx.ui.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.illarion.engine.backend.gdx.GdxRenderable;
import org.illarion.engine.ui.*;

import java.util.function.Consumer;

public class GdxLoginStage implements LoginStage, GdxRenderable {
    private final Stage stage;
    private final Container<Table> root;
    private final CreditsTable credits;
    private final OptionsTable options;
    private final LoginTable login;
    private final CharacterSelectTable characterSelection;
    private final CharacterCreationTable characterCreation;

    private Table activeTable;

    public GdxLoginStage(Skin skin) {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        root = new Container<>();
        root.setFillParent(true);
        //root.setBackground(getBackgroundDrawable());
        root.center();
        root.fill();
        stage.addActor(root);

        login = new LoginTable(skin);
        credits = new CreditsTable(skin);
        options = new OptionsTable(skin);
        characterSelection = new CharacterSelectTable(skin);
        characterCreation = new CharacterCreationTable(skin);
        login.setOnOptionsCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                activateTable(options);
            }
        });
        login.setOnCreditsCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                activateTable(credits);
            }
        });
        login.setOnExitCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        login.setOnLoginCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                activateTable(characterSelection);
            }
        });

        characterSelection.setOnLogoutCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                activateTable(login);
            }
        });
        characterSelection.setOnCreateCharacterCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                activateTable(characterCreation);
            }
        });

        characterCreation.setOnCancelCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                activateTable(characterSelection);
            }
        });
        characterCreation.setOnFinishCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                activateTable(characterSelection);
            }
        });

        options.setOnCancelCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                activateTable(login);
            }
        });
        options.setOnCancelCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                activateTable(login);
            }
        });

        activateTable(login);
    }

    @Override
    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if (activeTable == credits && credits.cycleCredits()) {
            activateTable(login);
        }
    }

    public void dispose() {
        stage.dispose();
    }

    @Override
    public void setLoginData(LoginData[] data) {
        for (LoginData loginData : data) {
            //login.addLogin(loginData);
        }
    }

    @Override
    public void setCharacterData(CharacterSelectionData[] data) {
        for (CharacterSelectionData characterData : data) {
            //characterSelection.addCharacter(characterData);
        }
    }

    @Override
    public void setOptionsData(OptionsData data) {
        //options.setOptions(OptionsData);
    }

    @Override
    public void setExitListener(Action event) {
        login.setOnExitCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                event.invoke();
            }
        });
    }

    @Override
    public void setLoginListener(Consumer<LoginData> event) {
        login.setOnLoginCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                //event.accept(login.getSelectedLogin());
            }
        });
    }

    @Override
    public void setLogoutListener(Action event) {
        characterSelection.setOnLogoutCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                event.invoke();
            }
        });
    }

    @Override
    public void setOptionsListener(Consumer<OptionsData> event) {
        login.setOnOptionsCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                //event.accept(options.getSelectedOptions());
            }
        });
    }

    @Override
    public void setCharacterSelectionListener(Consumer<CharacterSelectionData> event) {
        characterSelection.setOnPlayCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                //event.accept(characterSelection.getSelectedCharacter());
            }
        });
    }

    @Override
    public void setCharacterCreationListener(Consumer<CharacterCreationData> event) {
        characterCreation.setOnFinishCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                //event.accept(characterCreation.getCharacter());
            }
        });
    }

    @Override
    public void informLoginResult(RequestResult result) {
        // Remove waiting popup

        if (result.isRequestSuccessful) {
            // goto character selection screen
        } else {
            // Show Error Popup
        }
    }

    @Override
    public void informCharacterCreationResult(RequestResult result) {
        // Remove waiting popup

        if (result.isRequestSuccessful) {
            // goto character selection screen
        } else {
            // Show Error Popup
        }
    }

    private void activateTable(Table table) {
        if (activeTable != null) {
            activeTable.remove();
        }

        activeTable = table;
        root.setActor(activeTable);
    }

    /*private Drawable getBackgroundDrawable() {
        Texture t = new Texture("skin/window_background.png");
        return new TiledDrawable(new TextureRegion(t, 0, 0, t.getWidth(), Gdx.graphics.getHeight()));
    }*/
}
