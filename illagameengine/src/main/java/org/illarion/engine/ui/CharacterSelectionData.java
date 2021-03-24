package org.illarion.engine.ui;

import illarion.common.types.CharacterId;

public class CharacterSelectionData {
    public final CharacterId id;
    public final String name;

    public CharacterSelectionData(CharacterId id, String name) {
        this.id = id;
        this.name = name;
    }
}
