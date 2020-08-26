package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge;

import net.runelite.api.TileObject;

import java.util.Set;

public class SkillingObstacle {

    private Set<TileObject> tileObjects;

    private SkillChallenge skillChallenge;


    void addTile(TileObject tileObject) {
        tileObjects.add(tileObject);
    }

    

}
