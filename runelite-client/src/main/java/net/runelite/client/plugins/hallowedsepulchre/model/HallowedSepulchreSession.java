package net.runelite.client.plugins.hallowedsepulchre.model;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;

/**
 * A {@link HallowedSepulchreSession} starts when a player enters the Hallowed
 * Sepulchre (the actual not instance, not the lobby) and ends when the player
 * leaves the instance and returns to the lobby.
 *
 * @see net.runelite.client.plugins.hallowedsepulchre.SessionManager
 */
@Slf4j
public class HallowedSepulchreSession {

    private Optional<HallowedSepulchreFloor> currentFloor = Optional.empty();

    // Index 0 = Floor 1, Index 1 = Floor 2...
    private List<Duration> completedFloorDurations = new ArrayList<>();

    // The overall duration recorded by the server.
    // This shouldn't be set until after the server posts the completion time
    // for floor 5.
    private Optional<Duration> overallDuration = Optional.empty();

    /**
     * Sets the floor the player is currently on.
     * @param currentFloor The floor the player is currently on,
     *                     or null if they are not on a floor.
     */
    public void setCurrentFloor(@Nullable final HallowedSepulchreFloor currentFloor) {
        this.currentFloor = Optional.ofNullable(currentFloor);
    }

    public void setOverallDuration(final Duration overallDuration) {
        this.overallDuration = Optional.of(overallDuration);
    }

    public void addCompletedFloorDuration(final Duration floorDuration) {
        completedFloorDurations.add(floorDuration);
    }

    /**
     * @return The overall time recorded by the server.
     */
    public Duration getOverallDuration() {
        // Prioritize the server time, otherwise use our own calculated version.
        return overallDuration.orElse(getCumulativeDuration());
    }

    /**
     * @return The total amount of the time the player has spent on all floors
     * including the current floor.
     */
    private Duration getCumulativeDuration() {
        return completedFloorDurations.stream()
                .reduce(Duration.ZERO, Duration::plus)
                .plus(getCurrentFloorDuration());
    }

    /**
     * @return The amount of the time the player has spent on the current floor.
     */
    private Duration getCurrentFloorDuration() {
        return currentFloor
                .map(HallowedSepulchreFloor::getDuration)
                .orElse(Duration.ZERO);
    }

    /**
     * @return The durations for each of the completed floors in order starting
     * with floor 1.
     */
    public List<Duration> getCompletedFloorDurations() {
        return completedFloorDurations;
    }

    /**
     * @return The floor the player is currently on.
     */
    public Optional<HallowedSepulchreFloor> getCurrentFloor() {
        return currentFloor;
    }
}
