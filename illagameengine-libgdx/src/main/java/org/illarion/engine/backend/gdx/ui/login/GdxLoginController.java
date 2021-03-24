package org.illarion.engine.backend.gdx.ui.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import illarion.common.config.ConfigReader;
import org.illarion.engine.backend.gdx.GdxRenderable;
import org.illarion.engine.graphic.ResolutionManager;
import org.illarion.engine.ui.*;

import java.util.Map;
import java.util.function.Consumer;

public class GdxLoginController implements LoginStage, GdxRenderable {
    private final Stage stage;
    private final Container<Table> root;
    private final CreditView credits;
    private final OptionsView options;
    private final LoginView login;
    private final CharacterSelectView characterSelection;
    private final CharacterCreationView characterCreation;

    private Table activeTable;

    public GdxLoginController(Skin skin, NullSecureResourceBundle resourceBundle) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        root = new Container<>();
        root.setFillParent(true);
        //root.setBackground(getBackgroundDrawable());
        root.center();
        root.fill();
        stage.addActor(root);

        login = new LoginView(skin, resourceBundle);
        credits = new CreditView(skin, resourceBundle);
        options = new OptionsView(skin, resourceBundle);
        characterSelection = new CharacterSelectView(skin, resourceBundle);
        characterCreation = new CharacterCreationView(skin, resourceBundle);
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
        options.setOnBackCallback(new ClickListener() {
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

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        root.setSize(width, height);
    }

    public void dispose() {
        stage.dispose();
    }

    @Override
    public void setLoginData(LoginData[] data, int initialServer) {
        login.setLoginData(data, initialServer);
    }

    @Override
    public void loginSuccessful(CharacterSelectionData[] data) {
        for (CharacterSelectionData characterData : data) {
            //characterSelection.addCharacter(characterData);
        }
        login.hideWaitDialog();
        // goto: charselectionscreen
    }

    @Override
    public void loginFailed() {
        login.hideWaitDialog();
        //login.showErrorDialog();
    }

    @Override
    public void setOptionsData(ConfigReader configReader, ResolutionManager resolutionManager) {
        options.setOptions(configReader, resolutionManager);
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
                login.showWaitDialog("login", stage);
                event.accept(login.getSelectedLoginData());
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
    public void setOptionsSaveListener(Consumer<Map<String, String>> event){
        options.setOnSaveCallback(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                event.accept(options.getCurrentSettings());
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
