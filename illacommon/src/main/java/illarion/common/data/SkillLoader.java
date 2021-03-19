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
package illarion.common.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class takes care for loading the skills from the XML file as required. Calling this function is required
 * before the skills and skill groups are used.
 * <p/>
 * This function blocks the execution of the current thread until loading the Skill is done for sure.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SkillLoader {
    /**
     * The logger instance of this class.
     */
    @NotNull
    private static final Logger log = LogManager.getLogger();

    /**
     * This value is turned {@code true} once the loading is finished.
     */
    private static volatile boolean loadingFinished;

    /**
     * Load the skills from the XML file.
     */
    @SuppressWarnings("OverlyLongMethod")
    public static void load() {
        if (loadingFinished) {
            return;
        }

        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        synchronized (SkillLoader.class) {
            if (loadingFinished) {
                return;
            }

            try (InputStream skillXmlStream = ccl.getResourceAsStream("skills.xml")) {
                if (skillXmlStream == null) {
                    throw new IllegalStateException("Skill XML was not found.");
                }
                XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                parser.setInput(skillXmlStream, "UTF-8");

                parser.nextTag();
                parser.require(XmlPullParser.START_TAG, null, "skills");
                int currentTag = parser.nextTag();
                while (currentTag == XmlPullParser.START_TAG) {
                    parser.require(XmlPullParser.START_TAG, null, "group");
                    SkillGroup currentGroup = readSkillGroup(parser);
                    SkillGroups.getInstance().addSkillGroup(currentGroup);
                    currentTag = parser.nextTag();
                    while (currentTag == XmlPullParser.START_TAG) {
                        parser.require(XmlPullParser.START_TAG, null, "skill");
                        Skill skill = readSkill(parser, currentGroup);
                        Skills.getInstance().addSkill(skill);
                        parser.nextTag();
                        parser.require(XmlPullParser.END_TAG, null, "skill");
                        currentTag = parser.nextTag();
                    }
                    parser.require(XmlPullParser.END_TAG, null, "group");
                    currentTag = parser.nextTag();
                }
                parser.require(XmlPullParser.END_TAG, null, "skills");
            } catch (@NotNull XmlPullParserException e) {
                log.error("Parsing the XML file failed.", e);
            } catch (@NotNull IOException e) {
                log.error("Reading the XML file failed.", e);
            }
            loadingFinished = true;
        }
    }

    @NotNull
    private static SkillGroup readSkillGroup(@NotNull XmlPullParser parser) throws XmlPullParserException {
        int count = parser.getAttributeCount();
        String germanName = null;
        String englishName = null;
        int id = -1;
        for (int i = 0; i < count; i++) {
            switch (parser.getAttributeName(i)) {
                case "german":
                    germanName = parser.getAttributeValue(i);
                    break;
                case "english":
                    englishName = parser.getAttributeValue(i);
                    break;
                case "id":
                    id = Integer.parseInt(parser.getAttributeValue(i));
                    break;
                default:
            }
        }
        if ((germanName != null) && (englishName != null) && (id > -1)) {
            return new SkillGroup(id, germanName, englishName);
        }
        throw new XmlPullParserException("Group tag does not contain the required attributes.", parser, null);
    }

    @NotNull
    private static Skill readSkill(@NotNull XmlPullParser parser, @NotNull SkillGroup group)
            throws XmlPullParserException {
        int count = parser.getAttributeCount();
        String serverName = null;
        String germanName = null;
        String englishName = null;
        int serverId = -1;
        for (int i = 0; i < count; i++) {
            String attributeName = parser.getAttributeName(i);
            switch (attributeName) {
                case "name":
                    serverName = parser.getAttributeValue(i);
                    break;
                case "german":
                    germanName = parser.getAttributeValue(i);
                    break;
                case "english":
                    englishName = parser.getAttributeValue(i);
                    break;
                case "id":
                    try {
                        serverId = Integer.parseInt(parser.getAttributeValue(i));
                    } catch (NumberFormatException e) {
                        throw new XmlPullParserException("Parsing the ID attribute failed.", parser, e);
                    }
                    break;
                default:
                    break;
            }
        }
        if ((germanName != null) && (englishName != null) && (serverName != null) && (serverId >= 0)) {
            return new Skill(serverId, serverName, germanName, englishName, group);
        }
        throw new XmlPullParserException("Skill tag does not contain the required attributes.", parser, null);
    }

    /**
     * Private constructor to block the creation of a instance.
     */
    private SkillLoader() {
        // nothing
    }
}
