package org.illarion.engine.ui;

import org.illarion.engine.graphic.Color;
import org.illarion.engine.ui.login.CharacterCreationOptions;

import java.util.Map;

public record CharacterCreation(String name,
                                CharacterCreationOptions.LocalizedId race,
                                CharacterCreationOptions.LocalizedId sex,
                                CharacterCreationOptions.LocalizedId hair,
                                CharacterCreationOptions.LocalizedId beard,
                                Color hairColor,
                                Color skinColor,
                                Map<String, Integer> attributes,
                                CharacterCreationOptions.LocalizedId startPack,
                                int dateOfBirthDay,
                                int dateOfBirthMonth,
                                int age) {
}
