package org.illarion.engine.backend.gdx.ui.login;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

public class CharacterCreationTable extends Table {
    private final ArrayList<Table> characterCreationSequence;
    private int currentStep;
    private static final int MAX_STEP = 2;

    private final TextButton cancelButton, finishButton, nextButton, previousButton;
    private final Container<Table> dynamicContainer;

    public CharacterCreationTable(Skin skin) {
        characterCreationSequence = new ArrayList<>(3);
        characterCreationSequence.add(createStepOne(skin));
        characterCreationSequence.add(createStepTwo(skin));
        characterCreationSequence.add(createStepThree(skin));

        previousButton = new TextButton("Previous Step", skin);
        nextButton = new TextButton("Next Step", skin);
        finishButton = new TextButton("Finish", skin);
        cancelButton = new TextButton("Cancel", skin);

        HorizontalGroup buttonRow = new HorizontalGroup();
        buttonRow.addActor(cancelButton);
        buttonRow.addActor(previousButton);
        buttonRow.addActor(nextButton);
        buttonRow.addActor(finishButton);

        setFillParent(true);
        currentStep = 0;

        dynamicContainer = new Container<>();
        dynamicContainer.setActor(characterCreationSequence.get(currentStep));

        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                nextStep();
            }
        });
        previousButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                previousStep();
            }
        });

        previousButton.setDisabled(true);
        finishButton.setDisabled(true);

        /* Layout */
        row();
        add(dynamicContainer);
        row();
        add(buttonRow);
    }

    public void setOnCancelCallback(ClickListener onClick) {
        cancelButton.addListener(onClick);
    }

    public void setOnFinishCallback(ClickListener onClick) {
        finishButton.addListener(onClick);
    }

    private void nextStep() {
        if (nextButton.isDisabled()) {
            return;
        }

        characterCreationSequence.get(currentStep).remove();
        currentStep++;

        if (currentStep == MAX_STEP) {
            nextButton.setDisabled(true);
            finishButton.setDisabled(false);
        }

        if (currentStep == 1) {
            previousButton.setDisabled(false);
        }

        dynamicContainer.setActor(characterCreationSequence.get(currentStep));
    }


    private void previousStep() {
        if (previousButton.isDisabled()) {
            return;
        }

        characterCreationSequence.get(currentStep).remove();
        currentStep--;

        if (currentStep == 0) {
            previousButton.setDisabled(true);
        } else if (currentStep == MAX_STEP - 1) {
            finishButton.setDisabled(true);
            nextButton.setDisabled(false);
        }

        dynamicContainer.setActor(characterCreationSequence.get(currentStep));
    }

    private Table createStepOne(Skin skin) {
        TextField nameField = new TextField("Name", skin);

        SelectBox<String> raceSelection = new SelectBox<>(skin);
        raceSelection.setItems("Human", "Dwarf", "Halfling", "Elf", "Orc", "Lizardmen");

        SelectBox<String> sexSelection = new SelectBox<>(skin);
        sexSelection.setItems("male", "female");

        SelectBox<String> serverSelection = new SelectBox<>(skin);
        serverSelection.setItems("Gameserver", "Devserver");

        Table table = new Table();
        table.row();
        table.add(new Label("Character Name:", skin));
        table.add(nameField);
        table.row();
        table.add(new Label("Race:", skin));
        table.add(nameField);
        table.row();
        table.add(new Label("Sex:", skin));
        table.add(sexSelection);
        table.row();
        table.add(new Label("Server:", skin));
        table.add(serverSelection);

        return table;
    }

    private Table createStepTwo(Skin skin) {
        Slider weightSlider = new Slider(40f, 280f, 1f, false, skin);
        Slider heightSlider = new Slider(60f, 230f, 1f, false, skin);
        Slider ageSlider = new Slider(18f, 4000f, 1f, false, skin);

        SelectBox<String> dayOfBirthSelection = new SelectBox<>(skin);
        dayOfBirthSelection.setItems("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20", "21", "22", "23", "24");
        SelectBox<String> monthOfBirthSelection = new SelectBox<>(skin);
        monthOfBirthSelection.setItems("Elos", "Tanos", "Zhas", "Ushos", "Siros", "Ronas", "Bras", "Eldas", "Irmas",
                "Malas", "Findos", "Olos", "Adras", "Naras", "Chos", "Mas");
        SelectBox<String> hairSelection = new SelectBox<>(skin);
        hairSelection.setItems("No Hair", "Short Hair", "Medium Hair", "Long Hair");
        SelectBox<String> beardSelection = new SelectBox<>(skin);
        beardSelection.setItems("No Beard", "Short Beard", "Goatee Beard", "Square Beard", "Thin Beard", "Long Beard",
                "Mutton Chops");

        //Skin Color, Hair Color

        Table table = new Table();
        table.row();
        table.add(new Label("Weight:", skin));
        table.add(weightSlider);
        table.row();
        table.add(new Label("Height:", skin));
        table.add(heightSlider);
        table.row();
        table.add(new Label("Age:", skin));
        table.add(ageSlider);
        table.row();
        table.add(new Label("Birth Date:", skin));
        HorizontalGroup birthDateGroup = new HorizontalGroup();
        birthDateGroup.addActor(dayOfBirthSelection);
        birthDateGroup.addActor(monthOfBirthSelection);
        table.add(birthDateGroup);
        table.row();
        table.add(new Label("Hair Style:", skin));
        table.add(hairSelection);
        table.row();
        table.add(new Label("Beard Style:", skin));
        table.add(beardSelection);

        return table;
    }

    private Table createStepThree(Skin skin) {
        SelectBox<String> presetSelection = new SelectBox<>(skin);
        presetSelection.setItems("Knight", "Barbarian", "Rogue", "Hunter", "Craftsman", "Worker",
                "Druid", "Ranger", "Mage", "Battle Mage", "Priest", "Paladin");
        SelectBox<String> backgroundSelection = new SelectBox<>(skin);
        backgroundSelection.setItems("Native", "Tribesmen", "Guy from Gynk");

        Slider strengthSlider = new Slider(3, 18, 1, false, skin);
        Slider agilitySlider = new Slider(3, 18, 1, false, skin);
        Slider constitutionSlider = new Slider(3, 18, 1, false, skin);
        Slider dexteritySlider = new Slider(3, 18, 1, false, skin);
        Slider intelligenceSlider = new Slider(3, 18, 1, false, skin);
        Slider perceptionSlider = new Slider(3, 18, 1, false, skin);
        Slider willpowerSlider = new Slider(3, 18, 1, false, skin);
        Slider essenceSlider = new Slider(3, 18, 1, false, skin);

        Table table = new Table();
        table.row();
        table.add(new Label("Choose Preset:", skin));
        table.add(presetSelection);
        table.row();
        table.add(new Label("Background:", skin));
        table.add(backgroundSelection);
        table.row();
        table.add(new Label("Strength:", skin));
        table.add(strengthSlider);
        table.row();
        table.add(new Label("Agility:", skin));
        table.add(agilitySlider);
        table.row();
        table.add(new Label("Constitution:", skin));
        table.add(constitutionSlider);
        table.row();
        table.add(new Label("Dexterity:", skin));
        table.add(dexteritySlider);
        table.row();
        table.add(new Label("Intelligence:", skin));
        table.add(intelligenceSlider);
        table.row();
        table.add(new Label("Perception:", skin));
        table.add(perceptionSlider);
        table.row();
        table.add(new Label("Willpower:", skin));
        table.add(willpowerSlider);
        table.row();
        table.add(new Label("Essence:", skin));
        table.add(essenceSlider);

        return table;
    }

}
