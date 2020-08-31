package net.runelite.client.plugins.hallowedsepulchre;

import com.google.inject.Provides;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.hallowedsepulchre.model.*;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.SkillChallenge;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.SkillChallengeManager;
import net.runelite.client.plugins.hallowedsepulchre.overlay.DebugOverlay;
import net.runelite.client.plugins.hallowedsepulchre.overlay.LootOverlay;
import net.runelite.client.plugins.hallowedsepulchre.overlay.ObstacleOverlay;
import net.runelite.client.plugins.hallowedsepulchre.overlay.TimerOverlay;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@PluginDescriptor(
        name = "Hallowed Sepulchre",
        description = "Useful timers and overlays for the Hallowed Sepulchre",
        tags = {"overlay", "hallowed", "sepulchre", "agility", "timer"}
)
public class HallowedSepulchrePlugin extends Plugin {

    private int REGION_LOBBY = 9565;

    @Inject
    private Client client;

    @Inject
    private HallowedSepulchreConfig config;

    @Inject
    private ObstacleOverlay overlay;

    @Inject
    private TimerOverlay timerOverlay;

    @Inject
    private LootOverlay lootOverlay;

    @Inject
    private DebugOverlay debugOverlay;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    private EventBus eventBus;

    @Getter
    private SessionManager sessionManager;

    @Getter
    private LootTracker lootTracker;

    @Getter
    private SkillChallengeManager skillChallengeManager;

    @Provides
    HallowedSepulchreConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(HallowedSepulchreConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        log.debug("Starting Hallowed Sepulchre Plugin");

        sessionManager = new SessionManager();
        skillChallengeManager = new SkillChallengeManager();
        lootTracker = new LootTracker(itemManager);

        overlayManager.add(overlay);
        overlayManager.add(timerOverlay);
        overlayManager.add(lootOverlay);
        overlayManager.add(debugOverlay);

        eventBus.register(lootTracker);
    }

    @Override
    protected void shutDown() throws Exception {
        log.debug("Shutting down Hallowed Sepulchre Plugin");

        overlayManager.remove(overlay);
        overlayManager.remove(timerOverlay);
        overlayManager.remove(lootOverlay);
        overlayManager.remove(debugOverlay);

        eventBus.unregister(lootTracker);
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        onTileObjectSpawned(event.getGameObject());
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        onTileObjectSpawned(event.getGroundObject());
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        final NPC maybeProjectile = npcSpawned.getNpc();
        final int id = maybeProjectile.getId();

        Obstacle.getObstacleType(id).ifPresent(obstacleType -> {
            // Since this is a spawn event, it may trigger a new session and/or
            // floor.
            sessionManager.prepareSession();
            final HallowedSepulchreFloor floor = sessionManager.getCurrentOrUpcomingFloor(client);
            floor.addProjectile(maybeProjectile);
        });
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        NPC npc = event.getNpc();

        sessionManager.getCurrentFloor().ifPresent(floor -> floor.removeProjectile(npc));
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        switch (event.getGameState()) {
            case HOPPING:
                sessionManager.endSession();
                break;
            case LOADING:
                sessionManager.getCurrentFloor().ifPresent(HallowedSepulchreFloor::clearAllObstacles);
                break;
        }
    }

    @Subscribe
    public void onVarbitChanged(final VarbitChanged event) {

        sessionManager.getCurrentFloor().ifPresent(floor -> {
            final int ticksElapsed = client.getVarbitValue(10417);
            final int totalTicksElapsed = client.getVarbitValue(10393);
            final int floorTicksRemaining = client.getVarbitValue(10392);
            final boolean isTimerPaused = client.getVarbitValue(10413) == 1;

            final HallowedSepulchreTimetracker timeTracker = sessionManager.demandCurrentSession().getTimeTracker();

            // The timer starts when the server timer begins to tick.
            if (ticksElapsed > 0 && !isTimerPaused) {
                timeTracker.setFloorTicks(ticksElapsed);
                timeTracker.setOverallTicks(totalTicksElapsed);
            }

            // This flag gets set when the time expires so the skilling obstacles
            // can be highlighted a different color.
            if (floorTicksRemaining == 1 && !isTimerPaused) {
                log.debug("Time limited exceeded. Starting backup timer.");

                timeTracker.startBackupTimer();
            }

            // Pauses the floor timer.
            if (isTimerPaused && !timeTracker.isPaused()) {
                log.debug("Pausing timer.");
                timeTracker.pause();
            }
        });
    }

    @Subscribe
    public void onChatMessage(final ChatMessage chatMessage) {

        sessionManager.getCurrentSession().ifPresent(session -> {
            ChatEvent.fromChatMessage(chatMessage).ifPresent(chatEvent -> {
                log.debug("Chat event received: " + chatEvent);
                final String message = chatMessage.getMessage();

                switch (chatEvent) {
                    case FLOOR_COMPLETED:
                        session.putCompletedFloor(makeCompletedFloor(session));
                        session.setCurrentFloor(null);
                        break;
                    case FLOOR_TIME_POSTED:
                        final Optional<Duration> overallDuration = HallowedSepulchreUtil.extractOverallTime(message);

                        // This will overwrite a CompletedFloor from the FLOOR_COMPLETED event.
                        session.putCompletedFloor(makeCompletedFloor(session, message));
                        overallDuration.ifPresent(session::setOverallDuration);
                        break;
                    case OBELISK_EXIT:
                    case FLOOR_EXIT:
                        session.getTimeTracker().reset();
                        session.setCurrentFloor(null);
                        sessionManager.endSession();
                        break;
                }
            });
        });
    }

    @AllArgsConstructor
    private enum ChatEvent {
        FLOOR_STARTED("You venture", ChatMessageType.GAMEMESSAGE),
        FLOOR_COMPLETED("You have completed Floor", ChatMessageType.GAMEMESSAGE),
        FLOOR_TIME_POSTED("Floor", ChatMessageType.GAMEMESSAGE),
        FLOOR_PAUSED("You jump across the platform.", ChatMessageType.SPAM),
        FLOOR_EXIT("You make your way back to the lobby of the Hallowed Sepulchre", ChatMessageType.GAMEMESSAGE),
        OBELISK_EXIT("The obelisk teleports you back to the lobby of the Hallowed Sepulchre", ChatMessageType.GAMEMESSAGE);

        private String prefix;
        private ChatMessageType messageType;

        public static Optional<ChatEvent> fromChatMessage(final ChatMessage chatMessage) {

            for (final ChatEvent event : values()) {
                if (chatMessage.getMessage().startsWith(event.prefix) && chatMessage.getType().equals(event.messageType)) {
                    return Optional.of(event);
                }
            }

            return Optional.empty();
        }
    }

    private void onTileObjectSpawned(final TileObject tileObject) {
        Obstacle.getObstacleType(tileObject.getId()).ifPresent(obstacleType -> {

            // Since this is a spawn event, it may trigger a new session and/or floor.
            sessionManager.prepareSession();
            final HallowedSepulchreFloor floor = sessionManager.getCurrentOrUpcomingFloor(client);

            switch (obstacleType) {
                case STATUE:
                    // log.debug("Statue spawned at {}", gameObject.getLocalLocation());
                    final CrossbowmanStatue statue = new CrossbowmanStatue((GameObject) tileObject);
                    floor.addStatue(statue);
                    break;
                case PROGRESSION:
                    floor.addProgressionObstacle(tileObject);
                    break;
                case SKILLING:
                    final SkillChallenge skillChallenge = skillChallengeManager.getSkillChallengeForTile(tileObject);
                    floor.addSkillingObstacle(tileObject);
                    break;
                case STRANGE_TILE:
                    final StrangeTile strangeTile = new StrangeTile((GroundObject) tileObject);
                    floor.addStrangeTile(strangeTile);
                    break;
            }
        });
    }

    private CompletedFloor makeCompletedFloor(HallowedSepulchreSession session) {
        final HallowedSepulchreFloor floor = sessionManager.demandCurrentFloor();
        final Duration floorDuration = session.getTimeTracker().getCurrentFloorDuration();
        final Duration overallDuration = session.getTimeTracker().getOverallDuration();

        return new CompletedFloor(floor.getFloorNumber(), floorDuration, overallDuration);
    }

    private CompletedFloor makeCompletedFloor(HallowedSepulchreSession session, String timeMessage) {
        final int floorNumber = HallowedSepulchreUtil.extractFloorNumber(timeMessage);
        final Duration recordedFloorDuration = HallowedSepulchreUtil.extractFloorTime(timeMessage);
        final Duration overallDuration = HallowedSepulchreUtil.extractOverallTime(timeMessage)
                .orElse(session.getTimeTracker().getOverallDuration());

        return new CompletedFloor(floorNumber, recordedFloorDuration, overallDuration);
    }

    public Optional<HallowedSepulchreSession> getActiveSession() {
        return sessionManager.getCurrentSession();
    }

    public Optional<HallowedSepulchreSession> getMostRecentSession() {
        return sessionManager.getMostRecentSession();
    }

    public boolean isInLobby() {
        final Optional<Player> player = Optional.ofNullable(client.getLocalPlayer());

        return player.isPresent() && player.get().getWorldLocation().getRegionID() == REGION_LOBBY;
    }

    public boolean isInSepulchre() {
        return getActiveSession().isPresent();
    }
}