package org.illarion.engine.ui.login;

import illarion.common.types.CharacterId;
import org.illarion.engine.ui.DynamicUiContent;

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
