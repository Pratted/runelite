package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.old;

import net.runelite.api.Client;
import net.runelite.api.TileObject;

public class SkillChallengeFactory {

    private static final int BRIDGE = 1;
    private static final int PORTAL = 2;
    private static final int GRAPPLE = 3;
    private static final int BRAZIER = 4;

    public static SkillChallenge fromTileObject(Client client, TileObject tileObject) {
        int id = tileObject.getId();

        switch (id) {
            case BRIDGE:
                return new ConstructionSkillChallenge(client);
            case PORTAL:
                return new MagicSkillChallenge(client);
            case GRAPPLE:
                return new RangedSkillChallenge(client);
            case BRAZIER:
                return new PrayerSkillChallenge(client);
        }

        throw new IllegalArgumentException(id + " does not have a Skill Challenge associated with it");
    }
}
