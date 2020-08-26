package net.runelite.client.plugins.hallowedsepulchre;

import net.runelite.api.NullNpcID;
import net.runelite.api.ObjectID;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static net.runelite.api.NullObjectID.*;
import static net.runelite.api.ObjectID.*;

public enum Obstacle {
    // Projectile Obstacles are Arrows and Swords
    PROJECTILE(
            NullNpcID.NULL_9672, NullNpcID.NULL_9673, NullNpcID.NULL_9674,  // arrows
            NullNpcID.NULL_9669, NullNpcID.NULL_9670, NullNpcID.NULL_9671 // swords
    ),
    // Grapple, Prayer, Magic, Construction challenges.
    SKILLING(
            NULL_39524, NULL_39525, NULL_39526, NULL_39527, NULL_39528, NULL_39533
    ),
    // These are 'Progression' obstacles because they block the players progression.
    // For example, the player must proceed down the stairs to progress on the floor.
    PROGRESSION(
            GATE_38460, PLATFORM_38455, PLATFORM_38456, PLATFORM_38457, PLATFORM_38458, PLATFORM_38459,
            PLATFORM_38470, PLATFORM_38477, STAIRS_38462, STAIRS_38463, STAIRS_38464, STAIRS_38465,
            STAIRS_38466, STAIRS_38467, STAIRS_38468, STAIRS_38469, STAIRS_38471, STAIRS_38472,
            STAIRS_38473, STAIRS_38474, STAIRS_38475, STAIRS_38476
    ),
    STATUE(
            CROSSBOWMAN_STATUE, CROSSBOWMAN_STATUE_38445, CROSSBOWMAN_STATUE_38446
    ),
    STRANGE_TILE(
            ObjectID.STRANGE_TILE, STRANGE_TILE_38448
    );

    final Set<Integer> ids;

    Obstacle(int ...ids) {
        this.ids = Arrays.stream(ids)
                .boxed()
                .collect(Collectors.toSet());
    }

    public Set<Integer> getIds() {
        return ids;
    }

    public static boolean isObstacle(int id) {
        return getObstacleType(id).isPresent();
    }

    public static Optional<Obstacle> getObstacleType(int id) {
        if (PROJECTILE.getIds().contains(id)) {
            return Optional.of(PROJECTILE);
        } else if (SKILLING.getIds().contains(id)) {
            return Optional.of(SKILLING);
        } else if (PROGRESSION.getIds().contains(id)) {
            return Optional.of(PROGRESSION);
        } else if(STATUE.getIds().contains(id)) {
            return Optional.of(STATUE);
        } else if (STRANGE_TILE.getIds().contains(id)) {
            return Optional.of(STRANGE_TILE);
        } else {
            return Optional.empty();
        }
    }
}