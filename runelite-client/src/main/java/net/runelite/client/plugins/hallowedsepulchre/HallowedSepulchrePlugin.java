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
import net.runelite.client.plugins.hallowedsepulchre.model.CrossbowmanStatue;
import net.runelite.client.plugins.hallowedsepulchre.model.HallowedSepulchreFloor;
import net.runelite.client.plugins.hallowedsepulchre.model.HallowedSepulchreSession;
import net.runelite.client.plugins.hallowedsepulchre.model.StrangeTile;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.SkillChallenge;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.SkillChallengeManager;
import net.runelite.client.plugins.hallowedsepulchre.overlay.LootOverlay;
import net.runelite.client.plugins.hallowedsepulchre.overlay.ObstacleOverlay;
import net.runelite.client.plugins.hallowedsepulchre.overlay.StopwatchInfobox;
import net.runelite.client.plugins.hallowedsepulchre.overlay.TimerOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.RSTimeUnit;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.LocalTime;
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
    private OverlayManager overlayManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    private EventBus eventBus;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Getter
    private SessionManager sessionManager;

    @Getter
    private LootTracker lootTracker;

    @Getter
    private SkillChallengeManager skillChallengeManager;


    private StopwatchInfobox floorTimeInfobox;

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

//        final BufferedImage image = itemManager.getImage(ItemID.RING_OF_ENDURANCE, 1, false);
//        floorTimeInfobox = new StopwatchInfobox(image, this);
//        floorTimeInfobox.setText("1:31");

        overlayManager.add(overlay);
        overlayManager.add(timerOverlay);
        overlayManager.add(lootOverlay);

        // infoBoxManager.addInfoBox(floorTimeInfobox);

        eventBus.register(lootTracker);
    }

    @Override
    protected void shutDown() throws Exception {
        log.debug("Shutting down Hallowed Sepulchre Plugin");

        overlayManager.remove(overlay);
        overlayManager.remove(timerOverlay);
        overlayManager.remove(lootOverlay);

        // infoBoxManager.removeInfoBox(floorTimeInfobox);

        eventBus.unregister(lootTracker);
    }

    @Subscribe
    void onBeforeRender(BeforeRender beforeRender) {

//        if (sessionManager.getCurrentSession().isPresent()) {
//            long seconds = sessionManager.getCurrentFloor()
//                    .map(floor -> floor.getDuration().getSeconds())
//                    .orElse(0L);
//
//            LocalTime time = LocalTime.ofSecondOfDay(seconds);
//
//            floorTimeInfobox.setText(time.toString());
//        }
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
            final HallowedSepulchreFloor floor = sessionManager.getCurrentOrUpcomingFloor();
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
            final int floorTicksRemaining = client.getVarbitValue(10392);
            final boolean isTimerPaused = client.getVarbitValue(10413) == 1;

            // The timer starts when the server timer begins to tick.
            if (ticksElapsed == 1 && !floor.isTimerRunning()) {
                log.debug("Starting timer.");

                // The stopwatch starts 1 tick late.
                floor.setStopwatchOffset(Duration.of(1, RSTimeUnit.GAME_TICKS));
                floor.startTimer();
            }

            // Pauses the floor timer.
            if (isTimerPaused && floor.isTimerRunning()) {
                log.debug("Pausing timer.");
                floor.stopTimer();
            }

            // This flag gets set when the time expires so the skilling obstacles
            // can be highlighted a different color.
            if (floorTicksRemaining == 1) {
                floor.setHasTimeExpired(true);
            }
        });
    }

    @Subscribe
    public void onChatMessage(final ChatMessage chatMessage) {

        ChatEvent.fromChatMessage(chatMessage).ifPresent(chatEvent -> {
            log.debug("Chat event received: " + chatEvent);
            final String message = chatMessage.getMessage();

            switch (chatEvent) {
                case FLOOR_COMPLETED:
                    sessionManager.demandCurrentSession().setCurrentFloor(null);
                    break;
                case FLOOR_TIME_POSTED:
                    final Duration floorDuration = HallowedSepulchreUtil.extractFloorTime(message);
                    final Optional<Duration> overallDuration = HallowedSepulchreUtil.extractOverallTime(message);

                    sessionManager.demandCurrentSession().addCompletedFloorDuration(floorDuration);
                    overallDuration.ifPresent(duration -> sessionManager.demandCurrentSession().setOverallDuration(duration));
                    break;
                case OBELISK_EXIT:
                case FLOOR_EXIT:
                    sessionManager.demandCurrentSession().setCurrentFloor(null);
                    sessionManager.endSession();
                    break;
            }
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
            final HallowedSepulchreFloor floor = sessionManager.getCurrentOrUpcomingFloor();

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
}