package illarion.client.gui;

import illarion.client.graphics.AvatarEntity;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.ui.DynamicUiContent;

public class EntityRenderImage implements DynamicUiContent {
    private final AvatarEntity entity;

    public EntityRenderImage(AvatarEntity entity) {
        this.entity = entity;
    }

    @Override
    public void render(Graphics g) {
        entity.render(g);
    }
}
