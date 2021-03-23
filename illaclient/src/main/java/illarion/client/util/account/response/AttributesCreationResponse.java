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
package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;


/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AttributesCreationResponse {
    @SerializedName("age")
    private MinMaxResponse age;

    @SerializedName("weight")
    private MinMaxResponse weight;

    @SerializedName("height")
    private MinMaxResponse height;

    @SerializedName("agility")
    private MinMaxResponse agility;

    @SerializedName("constitution")
    private MinMaxResponse constitution;

    @SerializedName("dexterity")
    private MinMaxResponse dexterity;

    @SerializedName("essence")
    private MinMaxResponse essence;

    @SerializedName("intelligence")
    private MinMaxResponse intelligence;

    @SerializedName("perception")
    private MinMaxResponse perception;

    @SerializedName("strength")
    private MinMaxResponse strength;

    @SerializedName("willpower")
    private MinMaxResponse willpower;

    @SerializedName("totalAttributePoints")
    private int totalAttributePoints;

    @NotNull
    public MinMaxResponse getAge() {
        assert age != null;
        return age;
    }

    @NotNull
    public MinMaxResponse getWeight() {
        assert weight != null;
        return weight;
    }

    @NotNull
    public MinMaxResponse getHeight() {
        assert height != null;
        return height;
    }

    @NotNull
    public MinMaxResponse getAgility() {
        assert agility != null;
        return agility;
    }

    @NotNull
    public MinMaxResponse getConstitution() {
        assert constitution != null;
        return constitution;
    }

    @NotNull
    public MinMaxResponse getDexterity() {
        assert dexterity != null;
        return dexterity;
    }

    @NotNull
    public MinMaxResponse getEssence() {
        assert essence != null;
        return essence;
    }

    @NotNull
    public MinMaxResponse getIntelligence() {
        assert intelligence != null;
        return intelligence;
    }

    @NotNull
    public MinMaxResponse getPerception() {
        assert perception != null;
        return perception;
    }

    @NotNull
    public MinMaxResponse getStrength() {
        assert strength != null;
        return strength;
    }

    @NotNull
    public MinMaxResponse getWillpower() {
        assert willpower != null;
        return willpower;
    }

    public int getTotalAttributePoints() {
        return totalAttributePoints;
    }
}
