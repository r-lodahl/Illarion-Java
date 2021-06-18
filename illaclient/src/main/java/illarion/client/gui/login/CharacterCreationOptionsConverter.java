package illarion.client.gui.login;

import illarion.client.util.account.form.CharacterCreateForm;
import illarion.client.util.account.response.CharacterCreateGetResponse;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.ui.CharacterCreation;
import org.illarion.engine.ui.login.CharacterCreationOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public enum CharacterCreationOptionsConverter {
    ;

    public static CharacterCreationOptions convertToCharacterCreationOptions(@Nullable CharacterCreateGetResponse characterCreationData) {
        if (characterCreationData == null || characterCreationData.getError() != null) {
            throw new RuntimeException("Character creation option data was erroneous or null ");
        }

        return new CharacterCreationOptions(
                characterCreationData
                        .getRaces()
                        .stream()
                        .map(raceResponse -> {
                            var attributes = raceResponse.getAttributes();
                            var attributeOptions = new HashMap<String, CharacterCreationOptions.ValueRange>();
                            attributeOptions.put("age", new CharacterCreationOptions.ValueRange(
                                    attributes.getAge().getMin(), attributes.getAge().getMax()
                            ));
                            attributeOptions.put("weight", new CharacterCreationOptions.ValueRange(
                                    attributes.getWeight().getMin(), attributes.getWeight().getMax()
                            ));
                            attributeOptions.put("height", new CharacterCreationOptions.ValueRange(
                                    attributes.getHeight().getMin(), attributes.getHeight().getMax()
                            ));
                            attributeOptions.put("agility", new CharacterCreationOptions.ValueRange(
                                    attributes.getAgility().getMin(), attributes.getAgility().getMax()
                            ));
                            attributeOptions.put("constitution", new CharacterCreationOptions.ValueRange(
                                    attributes.getConstitution().getMin(), attributes.getConstitution().getMax()
                            ));
                            attributeOptions.put("dexterity", new CharacterCreationOptions.ValueRange(
                                    attributes.getDexterity().getMin(), attributes.getDexterity().getMax()
                            ));
                            attributeOptions.put("essence", new CharacterCreationOptions.ValueRange(
                                    attributes.getEssence().getMin(), attributes.getEssence().getMax()
                            ));
                            attributeOptions.put("intelligence", new CharacterCreationOptions.ValueRange(
                                    attributes.getIntelligence().getMin(), attributes.getIntelligence().getMax()
                            ));
                            attributeOptions.put("perception", new CharacterCreationOptions.ValueRange(
                                    attributes.getPerception().getMin(), attributes.getPerception().getMax()
                            ));
                            attributeOptions.put("strength", new CharacterCreationOptions.ValueRange(
                                    attributes.getStrength().getMin(), attributes.getStrength().getMax()
                            ));
                            attributeOptions.put("willpower", new CharacterCreationOptions.ValueRange(
                                    attributes.getWillpower().getMin(), attributes.getWillpower().getMax()
                            ));

                            var sexes = raceResponse.getTypes().stream().map(raceTypeResponse ->
                                    new CharacterCreationOptions.Sex(
                                            new CharacterCreationOptions.LocalizedId(
                                                    raceTypeResponse.getId(), raceResponse.getName()
                                            ),
                                            raceTypeResponse.getHairs().stream().map(
                                                    hair -> new CharacterCreationOptions.LocalizedId(
                                                            hair.getId(), hair.getName()
                                                    )
                                            ).toArray(CharacterCreationOptions.LocalizedId[]::new),
                                            raceTypeResponse.getBeards().stream().map(
                                                    beard -> new CharacterCreationOptions.LocalizedId(
                                                            beard.getId(), beard.getName()
                                                    )
                                            ).toArray(CharacterCreationOptions.LocalizedId[]::new),
                                            raceTypeResponse.getHairColours().stream().map(
                                                    hairColor -> new Color(
                                                            hairColor.getRed(),
                                                            hairColor.getGreen(),
                                                            hairColor.getBlue(),
                                                            hairColor.getAlpha()
                                                    )
                                            ).toArray(Color[]::new),
                                            raceTypeResponse.getSkinColours().stream().map(
                                                    skinColor -> new Color(
                                                            skinColor.getRed(),
                                                            skinColor.getGreen(),
                                                            skinColor.getBlue(),
                                                            skinColor.getAlpha()
                                                    )
                                            ).toArray(Color[]::new))
                            ).toArray(CharacterCreationOptions.Sex[]::new);

                            return new CharacterCreationOptions.Race(
                                    new CharacterCreationOptions.LocalizedId(
                                            raceResponse.getId(), raceResponse.getName()
                                    ),
                                    attributes.getTotalAttributePoints(),
                                    attributeOptions,
                                    sexes);
                        }).toArray(CharacterCreationOptions.Race[]::new),
                characterCreationData
                        .getStartPacks()
                        .stream()
                        .map(startPackResponse ->
                                new CharacterCreationOptions.StartPack(
                                        new CharacterCreationOptions.LocalizedId(
                                                startPackResponse.getId(),
                                                startPackResponse.getName()
                                        ),
                                        startPackResponse
                                                .getItems()
                                                .stream()
                                                .map(
                                                        startPackItemsResponse -> new CharacterCreationOptions.LocalizedId(
                                                                startPackItemsResponse.getItemId(),
                                                                startPackItemsResponse.getName()
                                                        ))
                                                .toList(),
                                        startPackResponse
                                                .getSkills()
                                                .stream()
                                                .map(
                                                        skill -> new CharacterCreationOptions.LocalizedId(
                                                                skill.getId(), skill.getName()))
                                                .toList()))
                        .toArray(CharacterCreationOptions.StartPack[]::new));
    }

    @NotNull
    public static CharacterCreateForm convertToCharacterCreateForm(CharacterCreation characterData) {
        return new CharacterCreateForm(
                characterData.name(),
                characterData.race().id(),
                characterData.sex().id(),
                characterData.attributes().get("agility"),
                characterData.attributes().get("constitution"),
                characterData.attributes().get("dexterity"),
                characterData.attributes().get("essence"),
                characterData.attributes().get("intelligence"),
                characterData.attributes().get("perception"),
                characterData.attributes().get("strength"),
                characterData.attributes().get("willpower"),
                characterData.startPack().id(),
                characterData.hair().id(),
                characterData.beard().id(),
                characterData.skinColor().getRed(),
                characterData.skinColor().getGreen(),
                characterData.skinColor().getBlue(),
                characterData.skinColor().getAlpha(),
                characterData.hairColor().getRed(),
                characterData.hairColor().getGreen(),
                characterData.hairColor().getBlue(),
                characterData.hairColor().getAlpha(),
                characterData.attributes().get("weight"),
                characterData.attributes().get("height"),
                characterData.dateOfBirthMonth(),
                characterData.dateOfBirthDay(),
                characterData.age());
    }
}
