package net.runelite.client.plugins.hallowedsepulchre.overlay;

import net.runelite.api.Client;
import net.runelite.client.plugins.hallowedsepulchre.HallowedSepulchreConfig;
import net.runelite.client.plugins.hallowedsepulchre.HallowedSepulchrePlugin;
import net.runelite.client.plugins.hallowedsepulchre.HallowedSepulchreUtil;
import net.runelite.client.plugins.hallowedsepulchre.model.HallowedSepulchreSession;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;

import java.awt.*;
import java.util.Optional;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class DebugOverlay extends OverlayPanel {

    private final Client client;
    private final HallowedSepulchrePlugin plugin;
    private final HallowedSepulchreConfig config;

    @Inject
    private DebugOverlay(
            Client client,
            HallowedSepulchrePlugin plugin,
            HallowedSepulchreConfig config) {
        super(plugin);

        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.LOW);

        this.client = client;
        this.plugin = plugin;
        this.config = config;

        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "HS_DEBUG_OVERLAY"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        final Optional<HallowedSepulchreSession> maybeSession = plugin.getMostRecentSession();

        panelComponent.getChildren().add(TitleComponent.builder().text("Developer").build());

        if (config.developerModeEnabled() && maybeSession.isPresent()) {
            final HallowedSepulchreSession session = maybeSession.get();

            int statues = session.getCurrentFloor().map(floor -> floor.getStatues().size()).orElse(0);
            int pathObstacles = session.getCurrentFloor().map(floor -> floor.getProgressionObstacles().size()).orElse(0);
            int skillingObstacles = session.getCurrentFloor().map(floor -> floor.getSkillingObstacles().size()).orElse(0);
            int projectiles = session.getCurrentFloor().map(floor -> floor.getProjectiles().size()).orElse(0);
            int strangeTiles = session.getCurrentFloor().map(floor -> floor.getActiveStrangeTiles().size()).orElse(0);

            addLineComponent("Statues:", statues);
            addLineComponent("Path:", pathObstacles);
            addLineComponent("Skilling:", skillingObstacles);
            addLineComponent("Projectiles:", projectiles);
            addLineComponent("Strange Tiles:", strangeTiles);
        }

        addLineComponent("Region:", HallowedSepulchreUtil.getCurrentFloorNumber(client).toString());
//        addLineComponent("Chunk:", HallowedSepulchreUtil.FloorRegion.getPoint(client).getX());
//        addLineComponent("Chunk:", HallowedSepulchreUtil.FloorRegion.getPoint(client).getY());

        return super.render(graphics);
    }

    private void addLineComponent(String left, int right) {
        panelComponent.getChildren().add(makeLineComponent(left, right));
    }

    private void addLineComponent(String left, String right) {
        panelComponent.getChildren().add(makeLineComponent(left, right));
    }

    private static LineComponent makeLineComponent(String left, int right) {
        return LineComponent.builder()
                .left(left)
                .right(String.valueOf(right))
                .build();
    }

    private static LineComponent makeLineComponent(String left, String right) {
        return LineComponent.builder()
                .left(left)
                .right(right)
                .build();
    }
}