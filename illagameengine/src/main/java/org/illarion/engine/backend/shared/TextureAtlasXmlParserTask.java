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
package org.illarion.engine.backend.shared;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class TextureAtlasXmlParserTask<T> implements Supplier<List<TextureAtlasXmlParserTask<T>.AtlasData>> {
    final class AtlasData {
        final T textureData;
        final List<SpriteData> spriteList;
        final String atlasName;

        private AtlasData(String atlasName, T textureData, List<SpriteData> spriteList) {
            this.atlasName = atlasName;
            this.textureData = textureData;
            this.spriteList = spriteList;
        }
    }

    /**
     * The logger that provides the logging output of this class.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The factory required to create the XML parser. Setting up this parser should be done before its assigned to
     * this task.
     */
    @NotNull
    private final XmlPullParserFactory parserFactory;

    /**
     * The name of the atlas. Its required to fetch the correct XML file.
     */
    @NotNull
    private final String atlasName;

    /**
     * The parent texture manager. This one is needed to fetch the actual texture files later during the loading
     * progress.
     */
    @NotNull
    private final AbstractTextureManager<T> textureManager;

    /**
     * Create a new loading task. This task is meant to be executed concurrently. It will request the required
     * textures from the parent texture manager once the loading is progressed to this point.
     *
     * @param parserFactory the parser factory
     * @param atlasName the name of the atlas files
     * @param textureManager the parent texture manager
     */
    TextureAtlasXmlParserTask(
            @NotNull XmlPullParserFactory parserFactory,
            @NotNull String atlasName,
            @NotNull AbstractTextureManager<T> textureManager) {
        this.parserFactory = parserFactory;
        this.atlasName = atlasName;
        this.textureManager = textureManager;
    }

    @Override
    public List<TextureAtlasXmlParserTask<T>.AtlasData> get() {
        InputStream xmlStream = null;
        var atlasData = new ArrayList<AtlasData>();

        try {
            XmlPullParser parser = parserFactory.newPullParser();

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            xmlStream = classLoader.getResourceAsStream(atlasName + "-atlas.xml");
            parser.setInput(xmlStream, "UTF-8");

            int currentEvent = parser.nextTag();
            var spriteList = new ArrayList<SpriteData>(300);
            T textureData = null;

            while (currentEvent != XmlPullParser.END_DOCUMENT) {
                if (currentEvent == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();
                    switch (tagName) {
                        case "atlas" -> {
                            @Nullable String currentAtlasName = getAtlasTextureName(parser);
                            if (currentAtlasName != null) {
                                spriteList.clear();
                                textureData = textureManager.loadTextureData(currentAtlasName + ".png");
                            }
                        }
                        case "sprite" -> transferSpriteData(parser, spriteList);
                    }
                } else if (currentEvent == XmlPullParser.END_TAG) {
                    String tagName = parser.getName();
                    if ("atlas".equals(tagName)) {
                        if (textureData != null) {
                            atlasData.add(new AtlasData(atlasName, textureData, new ArrayList<>(spriteList)));
                        }
                    } else if ("atlasList".equals(tagName)) {
                        break;
                    }
                }
                currentEvent = parser.nextTag();
            }
        } catch (XmlPullParserException e) {
            LOGGER.error("Failed to load requested texture atlas: {}", atlasName, e);
        } catch (IOException e) {
            LOGGER.error("Reading error while loading texture atlas: {}", atlasName, e);
        } finally {
            if (xmlStream != null) {
                try {
                    xmlStream.close();
                } catch (IOException ignored) { }
            }
        }

        return atlasData;
    }

    @Nullable
    private static String getAtlasTextureName(@NotNull XmlPullParser parser) {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            if ("file".equals(parser.getAttributeName(i))) {
                String atlasName = parser.getAttributeValue(i);
                if (atlasName.endsWith(".png")) {
                    return atlasName.substring(0, atlasName.length() - 4);
                }
                return atlasName;
            }
        }
        return null;
    }

    private static void transferSpriteData(@NotNull XmlPullParser parser, @NotNull Collection<SpriteData> spriteList) {
        @Nullable String name = null;
        int posX = -1;
        int posY = -1;
        int width = -1;
        int height = -1;

        int attributeCount = parser.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            @NotNull String attributeName = parser.getAttributeName(i);
            @NotNull String attributeValue = parser.getAttributeValue(i);
            try {
                switch (attributeName) {
                    case "x" -> posX = Integer.parseInt(attributeValue);
                    case "y" -> posY = Integer.parseInt(attributeValue);
                    case "height" -> height = Integer.parseInt(attributeValue);
                    case "width" -> width = Integer.parseInt(attributeValue);
                    case "name" -> name = attributeValue;
                }
            } catch (NumberFormatException e) {
                LOGGER.error("Error while parsing texture atlas sprite: {}=\"{}" + '"', attributeName, attributeValue);
            }
        }

        if ((name != null) && (posX > -1) && (posY > -1) && (width > -1) && (height > -1)) {
            SpriteData data = new SpriteData(name, posX, posY, width, height);
            spriteList.add(data);
        } else {
            LOGGER.error("Unable to receive all required values for sprite definition!");
        }
    }
}
