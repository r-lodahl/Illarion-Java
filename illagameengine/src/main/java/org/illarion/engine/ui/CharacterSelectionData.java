package org.illarion.engine.ui;

import illarion.common.types.CharacterId;

public class CharacterSelectionData {
    public final CharacterId id;
    public final String name;
    public final DynamicUiContent preview;

    public CharacterSelectionData(CharacterId id, String name, DynamicUiContent preview) {
        this.id = id;
        this.name = name;
        this.preview = preview;
    }

    @Override
    public String toString() {
        return name;
    }
}
