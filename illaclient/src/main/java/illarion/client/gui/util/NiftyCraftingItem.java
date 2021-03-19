/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.gui.util;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.render.NiftyImage;
import illarion.client.gui.TextureRenderImage;
import illarion.client.resources.ItemFactory;
import illarion.client.world.items.CraftingItem;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import org.illarion.nifty.controls.CraftingItemEntry;
import org.jetbrains.annotations.NotNull;


/**
 * Created with IntelliJ IDEA.
 * User: Martin Karing
 * Date: 06.10.12
 * Time: 13:35
 * To change this template use File | Settings | File Templates.
 */
public class NiftyCraftingItem extends CraftingItem implements CraftingItemEntry {
    @NotNull
    private final NiftyImage craftImage;
    @NotNull
    private final NiftyImage[] ingredientImages;

    public NiftyCraftingItem(@NotNull Nifty nifty, @NotNull CraftingItem org) {
        super(org);

        craftImage = new NiftyImage(nifty.getRenderEngine(), new TextureRenderImage(
                ItemFactory.getInstance().getTemplate(getItemId().getValue())));

        ingredientImages = new NiftyImage[getIngredientCount()];
        for (int i = 0; i < ingredientImages.length; i++) {
            ingredientImages[i] = new NiftyImage(nifty.getRenderEngine(), new TextureRenderImage(
                    ItemFactory.getInstance().getTemplate(getIngredientItemId(i).getValue())));
        }
    }

    /**
     * Get the crafting time in seconds.
     *
     * @return the crafting time in seconds
     */
    @Override
    public double getCraftTime() {
        return (float) getBuildTime() / 10.f;
    }

    @NotNull
    @Override
    public NiftyImage getImage() {
        return craftImage;
    }

    @NotNull
    @Override
    public ItemCount getIngredientAmount(int index) {
        return getIngredient(index).getCount();
    }

    @NotNull
    @Override
    public NiftyImage getIngredientImage(int index) {
        return ingredientImages[index];
    }

    @NotNull
    @Override
    public ItemId getIngredientItemId(int index) {
        return getIngredient(index).getItemId();
    }

    /**
     * Get the text displayed in the tree.
     *
     * @return the text to display in the tree
     */
    @NotNull
    @Override
    public String getTreeLabel() {
        return getName();
    }
}
