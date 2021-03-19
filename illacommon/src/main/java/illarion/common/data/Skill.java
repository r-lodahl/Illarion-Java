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

import org.jetbrains.annotations.NotNull;

/**
 * This class defines a single skill.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Skill {
    /**
     * The name of the skill.
     */
    @NotNull
    private final String name;

    /**
     * The name of the skill in english.
     */
    @NotNull
    private final String nameEnglish;

    /**
     * The name of the skill in german.
     */
    @NotNull
    private final String nameGerman;

    /**
     * The ID of the skill.
     */
    private final int id;

    /**
     * The group this skill belongs to.
     */
    @NotNull
    private final SkillGroup group;

    /**
     * Create a new instance of the skill class.
     *
     * @param skillId the ID of the skill
     * @param skillName the name of the skill
     * @param german the german name of the skill
     * @param english the english name of the skill
     * @param skillGroup the group this skill belong to
     */
    Skill(
            int skillId,
            @NotNull String skillName,
            @NotNull String german,
            @NotNull String english,
            @NotNull SkillGroup skillGroup) {
        id = skillId;
        name = skillName;
        nameEnglish = english;
        nameGerman = german;
        group = skillGroup;
        group.addSkill(this);
    }

    /**
     * Get the ID of the skill.
     *
     * @return the ID of the skill
     */
    public int getId() {
        return id;
    }

    /**
     * Get the group this skill belongs to.
     *
     * @return the group this skill belong to
     */
    @NotNull
    public SkillGroup getGroup() {
        return group;
    }

    /**
     * Get the name of the skill. This name is mainly required for the scripts.
     *
     * @return the name of the skills
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Get the german name of the skill.
     *
     * @return the german name of the skill
     */
    @NotNull
    public String getNameGerman() {
        return nameGerman;
    }

    /**
     * Get the english name of the skill.
     *
     * @return the english name of the skill
     */
    @NotNull
    public String getNameEnglish() {
        return nameEnglish;
    }

    @NotNull
    @Override
    public String toString() {
        return "Skill " + id + ": " + nameEnglish;
    }
}
