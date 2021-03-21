/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.illarion.engine.MouseCursor;
import org.illarion.engine.backend.shared.AbstractCursorManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * This is the implementation of the cursor manager for libGDX.
 * <p/>
 * As libGDX does not directly support changing the cursors this is done by directly using LWJGL. This will not work
 * in case any other libGDX backend then LWJGL is used.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxCursorManager extends AbstractCursorManager {
    /**
     * The graphics handler of libGDX.
     */
    @NotNull
    private final Graphics graphics;

    /**
     * The file system handler of libGDX that is supposed to be used by this manager.
     */
    @NotNull
    private final Files files;

    /**
     * Create a new instance of this manager.
     *
     * @param graphics the libGDX graphics implementation.
     * @param files the files implementation that is supposed to be used to load the data
     */
    GdxCursorManager(@NotNull Graphics graphics, @NotNull Files files) {
        this.graphics = graphics;
        this.files = files;
    }

    @Nullable
    @Override
    protected MouseCursor loadCursor(@NotNull String ref, int hotSpotX, int hotSpotY) {
        try {
            Pixmap cursorPixels = new Pixmap(files.internal(ref));

            int cursorHeight = MathUtils.nextPowerOfTwo(cursorPixels.getHeight());
            int cursorWidth = MathUtils.nextPowerOfTwo(cursorPixels.getWidth());

            if ((cursorHeight != cursorPixels.getHeight()) || (cursorWidth != cursorPixels.getWidth()) ||
                    (cursorPixels.getFormat() != Format.RGBA8888)) {
                Pixmap tempPixMap = new Pixmap(cursorWidth, cursorHeight, Format.RGBA8888);
                tempPixMap.setColor(Color.CLEAR);
                tempPixMap.fill();
                tempPixMap.drawPixmap(cursorPixels, 0, 0);
                cursorPixels.dispose();
                cursorPixels = tempPixMap;
            }

            Cursor cursor = graphics.newCursor(cursorPixels, hotSpotX, hotSpotY);
            if (cursor != null) {
                return new GdxCursor(cursor);
            }
            return null;
        } catch (GdxRuntimeException e) {
            return null;
        }
    }
}