package org.illarion.engine.ui.login;

import org.illarion.engine.graphic.Color;

import java.util.List;
import java.util.Map;

public class CharacterCreationOptions {
    public record LocalizedId(int id, String localizationKey) {
        @Override
        public String toString() {
            return localizationKey;
        }
    }

    public record ValueRange(int min, int max) {}

    public record Sex (
            LocalizedId name,
            LocalizedId[] hairs,
            LocalizedId[] beards,
            Color[] hairColors,
            Color[] skinColors
    ) {
        @Override
        public String toString() {
            return name.localizationKey + name.id();
        }
    }

    public record Race (
            LocalizedId name,
            int totalAttributePoints,
            Map<String, ValueRange> attributeRanges,
            Sex[] sexes
    ) {
        @Override
        public String toString() {
            return name.localizationKey;
        }
    }

    public record StartPack(
        LocalizedId name,
        List<LocalizedId> skills,
        List<LocalizedId> items
    ) {
        @Override
        public String toString() {
            return name.localizationKey;
        }
    }

    public final Race[] races;
    public final StartPack[] startPacks;
    public CharacterCreationOptions(Race[] races, StartPack[] startPacks) {
        this.races = races;
        this.startPacks = startPacks;
    }
}



