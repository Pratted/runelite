package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge;

import net.runelite.api.TileObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static net.runelite.api.NullObjectID.*;

public class SkillChallengeManager {

    private static Map<Integer, SkillChallenge> lookup = new HashMap<>();

    private Map<Integer, SkillChallenge> activeChallenges = new HashMap<>();

    public SkillChallengeManager() {

        lookup.put(NULL_39524, SkillChallenge.GRAPPLE);
        lookup.put(NULL_39525, SkillChallenge.PRAYER);
        // lookup.put(NULL_39526, SkillChallenge.PRAYER); // not used
        lookup.put(NULL_39527, SkillChallenge.CONSTRUCTION);
        // lookup.put(NULL_39528, SkillChallenge.CONSTRUCTION); // not used
        lookup.put(NULL_39533, SkillChallenge.MAGIC);
    }

    public Collection<SkillChallenge> getSkillChallenges() {
        return activeChallenges.values();
    }

    public void addSkillChallenge(TileObject tileObject) {
        getSkillChallengeForTile(tileObject);
    }

    public SkillChallenge getSkillChallengeForTile(TileObject tileObject) {
        final SkillChallenge skillChallenge = getOrMakeSkillChallenge(tileObject.getId());
        skillChallenge.addTileObject(tileObject);

        return skillChallenge;
    }

    private SkillChallenge getOrMakeSkillChallenge(Integer tileId) {
        // Normalize the id so we don't accidentally create a second skill challenge.
        int id = normalizeId(tileId);

        return activeChallenges.getOrDefault(id, lookup.get(id));
    }

    // There are 6 different skill challenge tiles, but only 4 types of
    // challenges. As a result, some challenges consist of tiles with
    // different ids.
    // We want to avoid creating two skill challenges for one skill
    // challenge that consists of tiles with different ids (e.g a prayer challenge).
    // We solve this by only using 1 id per challenge.
    private static int normalizeId(int id) {
        switch (id) {
            case 1:
                return 2;
            case NULL_39526: // PRAYER
                return NULL_39525;
            case NULL_39528: // CON
                return NULL_39527;
            default:
                return id;
        }
    }
}
