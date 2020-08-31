package net.runelite.client.plugins.hallowedsepulchre;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.plugins.hallowedsepulchre.model.HallowedSepulchreFloor;
import net.runelite.client.plugins.hallowedsepulchre.model.HallowedSepulchreSession;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

/**
 * The {@link SessionManager} manages the player's current
 * {@link HallowedSepulchreSession}.
 * <p/>
 * Throughout the course of the Hallowed Sepulchre, different events create,
 * modify or destroy the state of a session. These events are not always
 * synchronous, so managing the state of the session is not a simple task. The
 * {@link SessionManager} attempts to normalize those actions by managing
 * the creation and destruction of a session as well as providing access to it.
 */
@Slf4j
public class SessionManager {

    // When the user leaves the HS and is teleported back to the lobby, they
    // may want to see information (e.g. lap times) about their most recent
    // session. The current session is destroyed when the user leaves the HS,
    // so normally that information would be lost, however, this variable keeps
    // a copy of their most recent session so it is still accessible.
    private List<HallowedSepulchreSession> previousSessions = new ArrayList<>();

    private Optional<HallowedSepulchreSession> currentSession = Optional.empty();

    /**
     * Gets the current floor if it is present, otherwise it creates the
     * upcoming floor.
     *
     * @return The current or upcoming floor.
     * @throws IllegalStateException When the session is not present.
     */
    public HallowedSepulchreFloor getCurrentOrUpcomingFloor(Client client) {
        checkState(currentSession.isPresent(), "The current session is not present.");

        HallowedSepulchreSession session = currentSession.get();

        if (!session.getCurrentFloor().isPresent()) {
            log.debug("Creating floor.");

            int floorNumber = getCurrentFloorNumber(client);
            HallowedSepulchreFloor floor = new HallowedSepulchreFloor(floorNumber);

            session.setCurrentFloor(floor);
        }

        return session.getCurrentFloor().get();
    }

    private static int getCurrentFloorNumber(Client client) {
        try {
            // The floor number should be derived from the player's location.
            // Don't assume player starts on floor 1 because the plugin
            // can be started in the middle of the floor. For example, if the
            // plugin is started in the middle of Floor 4, we don't want to
            // display it as Floor 1.
            return HallowedSepulchreUtil.getCurrentFloorNumber(client)
                    .orElseThrow(() -> new IllegalStateException("Unable to determine floor number from location."));
        } catch (IllegalStateException e) {
            log.error("Unable to determine floor number from location.", e);
            return -1;
        }
    }

    /**
     * Prepares the current session and initializes one if it doesn't exist.
     */
    public void prepareSession() {
        if (!currentSession.isPresent()) {
            log.debug("Creating new session.");
            currentSession = Optional.of(new HallowedSepulchreSession());
        }
    }

    /**
     * Forcefully requests the current {@link HallowedSepulchreSession} even
     * when it might not be present.
     *
     * @return The current session.
     * @throws IllegalStateException When the session is not present.
     */
    public HallowedSepulchreSession demandCurrentSession() {
        checkState(currentSession.isPresent(), "The current session is not present.");

        return currentSession.get();
    }

    /**
     * @return An {@link Optional} containing the current session, or
     * {@link Optional#empty()} if it does not exist.
     */
    public Optional<HallowedSepulchreSession> getCurrentSession() {
        return currentSession;
    }

    /**
     * Forcefully requests the current {@link HallowedSepulchreFloor} even
     * when it might not be present.
     *
     * @return The current floor.
     * @throws IllegalStateException When the session or floor is not present.
     */
    public HallowedSepulchreFloor demandCurrentFloor() {
        checkState(currentSession.isPresent(), "The current session is not present.");
        checkState(currentSession.get().getCurrentFloor().isPresent(), "The current floor is not present.");

        return currentSession.get().getCurrentFloor().get();
    }

    /**
     * @return An {@link Optional} containing the current floor, or
     * {@link Optional#empty()} if it does not exist.
     */
    public Optional<HallowedSepulchreFloor> getCurrentFloor() {
        return currentSession.flatMap(HallowedSepulchreSession::getCurrentFloor);
    }

    /**
     * Ends the current session.
     * @throws IllegalStateException When the session is not present.
     */
    public void endSession() {
        checkState(currentSession.isPresent(), "Unable to end session, no current session is present.");

        log.debug("Ending session");
        // The current floor shouldn't be present, but this will take a
        // precautionary measure and destroy it.
        getCurrentFloor().ifPresent(HallowedSepulchreFloor::clearAllObstacles);
        currentSession.get().setCurrentFloor(null);

        previousSessions.add(currentSession.get());
        currentSession = Optional.empty();
    }

    /**
     * Gets the most recent session if a session has been started.
     *
     * @return An {@link Optional} containing the most recent session, or
     * {@link Optional#empty()} if no session was ever created.
     */
    public Optional<HallowedSepulchreSession> getMostRecentSession() {
        final Optional<HallowedSepulchreSession> previousSession = !previousSessions.isEmpty() ?
                Optional.of(previousSessions.get(previousSessions.size() - 1)) :
                Optional.empty();

        return currentSession.isPresent() ?
                currentSession :
                previousSession;
    }
}