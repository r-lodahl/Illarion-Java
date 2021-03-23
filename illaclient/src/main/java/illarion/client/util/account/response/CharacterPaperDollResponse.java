package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CharacterPaperDollResponse {
    @SerializedName("hairId")
    private int hairId;

    @SerializedName("beardId")
    private int beardId;

    @SerializedName("skinColour")
    private ColorResponse skinColour;

    @SerializedName("hairColour")
    private ColorResponse hairColour;

    public int getHairId() {
        return hairId;
    }

    public int getBeardId() {
        return beardId;
    }

    public ColorResponse getSkinColour() {
        return skinColour;
    }

    public ColorResponse getHairColour() {
        return hairColour;
    }
}
