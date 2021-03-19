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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Pools;
import org.illarion.engine.graphic.Font;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * The font implementation of the libGDX backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxFont implements Font {
    /**
     * The internal bitmap font that is used.
     */
    @NotNull
    private final BitmapFont bitmapFont;

    @Nullable
    private final GdxFont outlineFont;

    /**
     * Create a new instance of the libGDX font implementation.
     *
     * @param bitmapFont the bitmap font that is wrapped by this instance
     * @param outlineFont the outline font in case there is any
     */
    GdxFont(@NotNull BitmapFont bitmapFont, @Nullable GdxFont outlineFont) {
        this.bitmapFont = bitmapFont;
        this.outlineFont = outlineFont;
    }

    @Override
    public void dispose() {
        bitmapFont.dispose();
    }

    @Override
    public int getLineHeight() {
        return Math.round(bitmapFont.getLineHeight());
    }

    @Override
    public int getWidth(@NotNull CharSequence text) {
        if (text.length() == 0) { return 0; }
        GlyphLayout layout = Pools.obtain(GlyphLayout.class);
        layout.setText(bitmapFont, text);
        float width = layout.width;
        BitmapFont outlineBitmapFont = getOutlineBitmapFont();
        if (outlineBitmapFont != null) {
            layout.setText(outlineBitmapFont, text);
            width = Math.max(width, layout.width);
        }
        Pools.free(layout);
        return (int) width;
    }

    @Override
    public int getAdvance(char current, char next) {
        @Nullable Glyph currentGlyph = bitmapFont.getData().getGlyph(current);
        if (currentGlyph == null) {
            return 0;
        }
        return currentGlyph.getKerning(next) + currentGlyph.xadvance;
    }

    @Nullable
    BitmapFont getOutlineBitmapFont() {
        if (outlineFont != null) {
            return outlineFont.getBitmapFont();
        }
        return null;
    }

    /**
     * Get the internal bitmap font.
     *
     * @return the internal font
     */
    @NotNull
    BitmapFont getBitmapFont() {
        return bitmapFont;
    }
}
