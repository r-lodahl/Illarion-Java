package org.illarion.engine.backend.gdx.ui.login;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.ui.CharacterCreation;
import org.illarion.engine.ui.NullSecureResourceBundle;
import org.illarion.engine.ui.login.CharacterCreationOptions;

import java.util.ArrayList;
import java.util.HashMap;

public final class CharacterCreationView extends Table {
    private static final int MAX_STEP = 2;

    private final ArrayList<Table> characterCreationSequence;
    private final TextButton cancelButton, finishButton, nextButton, previousButton;
    private final Container<Table> dynamicContainer;

    private final SelectBox<CharacterCreationOptions.Race> raceSelection;
    private final SelectBox<CharacterCreationOptions.Sex> sexSelection;
    private final SelectBox<CharacterCreationOptions.LocalizedId> hairSelection, beardSelection;
    private final SelectBox<Color> hairColorSelection, skinColorSelection;
    private final SelectBox<CharacterCreationOptions.StartPack> presetSelection;

    private final Slider weightSlider, heightSlider, ageSlider, strengthSlider, agilitySlider, constitutionSlider,
            dexteritySlider, intelligenceSlider, perceptionSlider, willpowerSlider, essenceSlider;

    private final Label unspentAttributePointsLabel;

    private final TextField nameField;

    private int currentStep;

    private final SelectBox<String> dayOfBirthSelection, monthOfBirthSelection;

    CharacterCreationView(Skin skin, NullSecureResourceBundle resourceBundle) {
        nameField = new TextField("Name", skin);

        raceSelection = new SelectBox<>(skin);
        sexSelection = new SelectBox<>(skin);
        hairSelection = new SelectBox<>(skin);
        beardSelection = new SelectBox<>(skin);
        hairColorSelection = new SelectBox<>(skin);
        skinColorSelection = new SelectBox<>(skin);
        presetSelection = new SelectBox<>(skin);

        dayOfBirthSelection = new SelectBox<>(skin);
        monthOfBirthSelection = new SelectBox<>(skin);

        strengthSlider = new Slider(0, 1, 1, false, skin);
        agilitySlider = new Slider(0, 1, 1, false, skin);
        constitutionSlider = new Slider(0, 1, 1, false, skin);
        dexteritySlider = new Slider(0, 1, 1, false, skin);
        intelligenceSlider = new Slider(0, 1, 1, false, skin);
        perceptionSlider = new Slider(0, 1, 1, false, skin);
        willpowerSlider = new Slider(0, 1, 1, false, skin);
        essenceSlider = new Slider(0, 1, 1, false, skin);

        unspentAttributePointsLabel = new Label("0", skin);

        var attributeChangeEvent = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applyAttributePointsChange();
            }
        };

        strengthSlider.addListener(attributeChangeEvent);
        agilitySlider.addListener(attributeChangeEvent);
        constitutionSlider.addListener(attributeChangeEvent);
        dexteritySlider.addListener(attributeChangeEvent);
        intelligenceSlider.addListener(attributeChangeEvent);
        perceptionSlider.addListener(attributeChangeEvent);
        willpowerSlider.addListener(attributeChangeEvent);
        essenceSlider.addListener(attributeChangeEvent);

        weightSlider = new Slider(0, 1, 1, false, skin);
        heightSlider = new Slider(0, 1, 1, false, skin);
        ageSlider = new Slider(0, 1, 1, false, skin);

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

    public void setOptions(CharacterCreationOptions characterCreationOptions) {

        raceSelection.setItems(characterCreationOptions.races);
        raceSelection.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applyRaceChange();
            }
        });

        sexSelection.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applySexChange();
            }
        });

        presetSelection.setItems(characterCreationOptions.startPacks);

        applyRaceChange();
        applySexChange();
        applyAttributePointsChange();
    }

    private void applySexChange() {
        var sex = sexSelection.getSelected();
        beardSelection.setItems(sex.beards());
        hairSelection.setItems(sex.hairs());
        hairColorSelection.setItems(sex.hairColors());
        skinColorSelection.setItems(sex.skinColors());
    }

    private void applyRaceChange() {
        var race = raceSelection.getSelected();

        sexSelection.setItems(race.sexes());

        var age = race.attributeRanges().get("age");
        ageSlider.setRange(age.min(), age.max());
        ageSlider.setValue(age.min());
        var height = race.attributeRanges().get("height");
        heightSlider.setRange(height.min(), height.max());
        heightSlider.setValue(height.min());
        var weight = race.attributeRanges().get("weight");
        weightSlider.setRange(weight.min(), weight.max());
        weightSlider.setValue(weight.min());

        var strength = race.attributeRanges().get("strength");
        strengthSlider.setRange(strength.min(), strength.max());
        strengthSlider.setValue(strength.min());
        var agility = race.attributeRanges().get("agility");
        agilitySlider.setRange(agility.min(), agility.max());
        agilitySlider.setValue(agility.min());
        var constitution = race.attributeRanges().get("constitution");
        constitutionSlider.setRange(constitution.min(), constitution.max());
        constitutionSlider.setValue(constitution.min());
        var dexterity = race.attributeRanges().get("dexterity");
        dexteritySlider.setRange(dexterity.min(), dexterity.max());
        dexteritySlider.setValue(dexterity.min());
        var intelligence = race.attributeRanges().get("intelligence");
        intelligenceSlider.setRange(intelligence.min(), intelligence.max());
        intelligenceSlider.setValue(intelligence.min());
        var perception = race.attributeRanges().get("perception");
        perceptionSlider.setRange(perception.min(), perception.max());
        perceptionSlider.setValue(perception.min());
        var willpower = race.attributeRanges().get("willpower");
        willpowerSlider.setRange(willpower.min(), willpower.max());
        willpowerSlider.setValue(willpower.min());
        var essence = race.attributeRanges().get("essence");
        essenceSlider.setRange(essence.min(), essence.max());
        essenceSlider.setValue(essence.min());
    }

    private void applyAttributePointsChange() {
        var race = raceSelection.getSelected();

        if (race == null) {
            return;
        }

        var spentAttributePoints = strengthSlider.getValue() + agilitySlider.getValue()
                + constitutionSlider.getValue() + dexteritySlider.getValue() + intelligenceSlider.getValue()
                + willpowerSlider.getValue() + essenceSlider.getValue() + perceptionSlider.getValue();

        unspentAttributePointsLabel.setText(Float.toString(race.totalAttributePoints() - spentAttributePoints));
    }

    void setOnCancelCallback(EventListener onClick) {
        cancelButton.addListener(onClick);
    }

    void setOnFinishCallback(EventListener onClick) {
        finishButton.addListener(onClick);
    }

    public CharacterCreation getCharacter() {
        var attributes = new HashMap<String, Integer>();
        attributes.put("strength", (int) strengthSlider.getValue());
        attributes.put("agility", (int) agilitySlider.getValue());
        attributes.put("constitution", (int) constitutionSlider.getValue());
        attributes.put("dexterity", (int) dexteritySlider.getValue());
        attributes.put("intelligence", (int) intelligenceSlider.getValue());
        attributes.put("perception", (int) perceptionSlider.getValue());
        attributes.put("willpower", (int) willpowerSlider.getValue());
        attributes.put("essence", (int) essenceSlider.getValue());
        attributes.put("age", (int) ageSlider.getValue());
        attributes.put("weight", (int) weightSlider.getValue());
        attributes.put("height", (int) heightSlider.getValue());

        return new CharacterCreation(
                nameField.getText(),
                raceSelection.getSelected().name(),
                sexSelection.getSelected().name(),
                hairSelection.getSelected(),
                beardSelection.getSelected(),
                hairColorSelection.getSelected(),
                skinColorSelection.getSelected(),
                attributes,
                presetSelection.getSelected().name(),
                dayOfBirthSelection.getSelectedIndex(),
                monthOfBirthSelection.getSelectedIndex(),
                (int) ageSlider.getValue());
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
        Table table = new Table();
        table.row();
        table.add(new Label("Character Name:", skin));
        table.add(nameField);
        table.row();
        table.add(new Label("Race:", skin));
        table.add(raceSelection);
        table.row();
        table.add(new Label("Sex:", skin));
        table.add(sexSelection);

        return table;
    }

    private Table createStepTwo(Skin skin) {
        dayOfBirthSelection.setItems("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20", "21", "22", "23", "24");

        monthOfBirthSelection.setItems("Elos", "Tanos", "Zhas", "Ushos", "Siros", "Ronas", "Bras", "Eldas", "Irmas",
                "Malas", "Findos", "Olos", "Adras", "Naras", "Chos", "Mas");

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
        table.row();
        table.add(new Label("Hair Color:", skin));
        table.add(hairColorSelection);
        table.row();
        table.add(new Label("Skin Color:", skin));
        table.add(skinColorSelection);

        return table;
    }

    private Table createStepThree(Skin skin) {
        Table table = new Table();
        table.row();
        table.add(new Label("Unspent attribute points:", skin));
        table.add(unspentAttributePointsLabel);
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
        table.row();
        table.add(new Label("Choose Starter Pack:", skin));
        table.add(presetSelection);

        return table;
    }
}
