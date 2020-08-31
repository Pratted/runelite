package net.runelite.client.plugins.hallowedsepulchre.model;

import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.SkillChallenge;
import net.runelite.client.util.RSTimeUnit;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static net.runelite.api.NullObjectID.*;

/**
 * A {@link HallowedSepulchreFloor} holds all of the objects on a floor.
 * These objects may be a variety of things like, skilling obstacles,
 * arrows and swords, statues, etc.
 */
public class HallowedSepulchreFloor {

    // The completed duration as recorded by the server (e.g. Floor 2 time: 0:43)
    private Optional<Duration> completedDuration = Optional.empty();

    // A stopwatch is used instead of the server varbit because once the time
    // remaining hits 0, the server will stop updating the varbit. However, we
    // still want to continue recording the time so a stopwatch is used. The
    // stopwatch lives in this model so the plugin doesn't have to set the
    // duration every tick.
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();

    // The stopwatch starts the instant the floor begins loading so the
    // stopwatch duration is usually 2 or 3 ticks ahead of the server's time.
    // This offset records that tick difference so a more accurate duration
    // can be return by getDuration.
    @Setter
    private Duration stopwatchOffset = Duration.of(1, RSTimeUnit.GAME_TICKS);

    @Getter
    @Setter
    private int floorNumber;

    private boolean hasTimeExpired = false;

    // Collections for the obstacles on this floor.
    private final Map<LocalPoint, CrossbowmanStatue> statues = new HashMap<>();
    private final Map<LocalPoint, StrangeTile> strangeTiles = new HashMap<>();
    private final Set<TileObject> progressionObstacles = new HashSet<>();
    private final Set<TileObject> skillingObstacles = new HashSet<>();
    private final Set<NPC> projectiles = new HashSet<>();

    // The skill challenges for this floor.
    private Map<Integer, SkillChallenge> skillChallenges = new HashMap<>();

    private Set<SkillChallenge> challenges = new HashSet<>();

    private static Map<Integer, SkillChallenge> lookup = new HashMap<>();

    private Map<Integer, SkillChallenge> activeChallenges = new HashMap<>();

    public HallowedSepulchreFloor(final int floorNumber) {
        this.floorNumber = floorNumber;

        skillChallenges.put(NULL_39524, SkillChallenge.GRAPPLE); // done
        skillChallenges.put(NULL_39525, SkillChallenge.PRAYER); // done
        skillChallenges.put(NULL_39526, SkillChallenge.PRAYER); // done
        skillChallenges.put(NULL_39527, SkillChallenge.CONSTRUCTION); // done
        skillChallenges.put(NULL_39528, SkillChallenge.CONSTRUCTION); // done
        skillChallenges.put(NULL_39533, SkillChallenge.MAGIC); // done

        lookup.put(NULL_39524, SkillChallenge.GRAPPLE);
        lookup.put(NULL_39525, SkillChallenge.PRAYER);
        // lookup.put(NULL_39526, SkillChallenge.PRAYER); // not used
        lookup.put(NULL_39527, SkillChallenge.CONSTRUCTION);
        // lookup.put(NULL_39528, SkillChallenge.CONSTRUCTION); // not used
        lookup.put(NULL_39533, SkillChallenge.MAGIC);
    }

    public void startTimer() {
        stopwatch.start();
    }

    public boolean isTimerRunning() {
        return stopwatch.isRunning();
    }

    public void stopTimer() {
        stopwatch.stop();
    }

    public Collection<SkillChallenge> getSkillChallenges() {
        return activeChallenges.values();
    }

    public SkillChallenge getOrMakeSkillChallenge(TileObject tileObject) {
        final SkillChallenge skillChallenge = getOrMakeSkillChallenge(tileObject.getId());
        skillChallenge.addTileObject(tileObject);

        return skillChallenge;
    }

    private SkillChallenge getOrMakeSkillChallenge(Integer tileId) {
        // Normalize the id so we don't accidentally create a second skill challenge.
        int id = normalizeSkillChallengeId(tileId);

        return activeChallenges.getOrDefault(id, lookup.get(id));
    }

    // There are 6 different skill challenge tiles (obstacles), but only 4
    // types of challenges. As a result, some challenges consist of tiles with
    // different ids.
    // We want to avoid creating two skill challenges for one skill
    // challenge that consists of tiles with different ids (e.g a prayer challenge).
    // We solve this by only using 1 id per challenge.
    private static int normalizeSkillChallengeId(int id) {
        switch (id) {
            case NULL_39526: // PRAYER
                return NULL_39525;
            case NULL_39528: // CON
                return NULL_39527;
            default:
                return id;
        }
    }


    public void addStrangeTile(StrangeTile tile) {
        strangeTiles.put(tile.getLocalLocation(), tile);
    }

    public void addStatue(final CrossbowmanStatue statue) {
        statues.put(statue.getLocalPoint(), statue);
    }

    public void addProgressionObstacle(TileObject progressionObstacle) {
        progressionObstacles.add(progressionObstacle);
    }

    public void addSkillingObstacle(TileObject skillingObstacle) {
        activeChallenges.put(skillingObstacle.getId(), getOrMakeSkillChallenge(skillingObstacle));

        skillingObstacles.add(skillingObstacle);
    }

    public void addProjectile(NPC projectile) {
        projectiles.add(projectile);
    }

    public Set<TileObject> getProgressionObstacles() {
        return progressionObstacles;
    }

    public Set<TileObject> getSkillingObstacles() {
        return skillingObstacles;
    }

    public Set<NPC> getProjectiles() {
        return projectiles;
    }

    public void removeProjectile(NPC projectile) {
        projectiles.remove(projectile);
    }

    public void clearAllObstacles() {
        statues.clear();
        strangeTiles.clear();
        progressionObstacles.clear();
        skillingObstacles.clear();
        projectiles.clear();
        challenges.clear();
    }

    public void setHasTimeExpired(final boolean expired) {
        this.hasTimeExpired = expired;
    }

    public boolean hasTimeExpired() {
        return hasTimeExpired;
    }

    /**
     * @return Get all of the active {@link StrangeTile}s on this floor.
     */
    public Set<StrangeTile> getActiveStrangeTiles() {
        return strangeTiles.values().stream()
                .filter(StrangeTile::isActivated)
                .collect(Collectors.toSet());
    }

    public Optional<StrangeTile> getStrangeTile(final LocalPoint localPoint) {
        return Optional.ofNullable(strangeTiles.get(localPoint));
    }

    /**
     * @return The the amount of time the player took to complete this floor.
     */
    public Duration getDuration() {
        // This value may be negative immediately after the timer is started
        // because the offset can be greater than the elapsed time.
        Duration stopwatchDuration = stopwatch.elapsed().plus(stopwatchOffset);

        // Default to zero, since a negative doesn't make sense to the caller.
        if (stopwatchDuration.isNegative()) {
            stopwatchDuration = Duration.ZERO;
        }

        // The completed duration set by the server takes precedence over our
        // stopwatch.
        return completedDuration.orElse(stopwatchDuration);
    }

    /**
     * Set the floor's duration.
     * @param duration The time it took to complete this floor.
     */
    public void setDuration(final Duration duration) {
        completedDuration = Optional.of(duration);
    }

    public Collection<CrossbowmanStatue> getStatues() {
        return statues.values();
    }
}
