package net.runelite.client.plugins.hallowedsepulchre.overlay;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.plugins.hallowedsepulchre.HallowedSepulchreConfig;
import net.runelite.client.plugins.hallowedsepulchre.HallowedSepulchrePlugin;
import net.runelite.client.plugins.hallowedsepulchre.HallowedSepulchreTimetracker;
import net.runelite.client.plugins.hallowedsepulchre.model.CompletedFloor;
import net.runelite.client.plugins.hallowedsepulchre.model.HallowedSepulchreFloor;
import net.runelite.client.plugins.hallowedsepulchre.model.HallowedSepulchreSession;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

@Slf4j
public class TimerOverlay extends OverlayPanel {

    private final Client client;
    private final HallowedSepulchrePlugin plugin;
    private final HallowedSepulchreConfig config;

    @Inject
    private TimerOverlay(
            Client client,
            HallowedSepulchrePlugin plugin,
            HallowedSepulchreConfig config) {
        super(plugin);

        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.LOW);

        this.client = client;
        this.plugin = plugin;
        this.config = config;

        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Hello World"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        // Nothing to render.
        if (!plugin.isInLobby() && !plugin.isInSepulchre()) {
            return super.render(graphics);
        }


        // We only want to show HS information if they are in the HS or in the lobby.
        if (plugin.isInLobby() || plugin.getMostRecentSession().isPresent()) {

            // If the player is in the lobby, no current session will be present,
            // but we still want to display the information so use most recent session.
            final Optional<HallowedSepulchreSession> maybeSession = plugin.getMostRecentSession();

            if (maybeSession.isPresent()) {
                final HallowedSepulchreSession session = maybeSession.get();
                final HallowedSepulchreTimetracker timetracker = session.getTimeTracker();

                final List<LineComponent> completedFloorTimes = new ArrayList<>();
                Optional<LineComponent> currentFloorTime = Optional.empty();
                Optional<LineComponent> overallTime = Optional.empty();

                if (config.showFloorTime()) {
                    if (session.getCurrentFloor().isPresent()) {
                        final HallowedSepulchreFloor floor = session.getCurrentFloor().get();

                        // If the completed floor times are displayed, use a green text color to highlight
                        // the current floor.
                        final Color textColor = config.showCompletedFloorTimes() ? Color.GREEN : Color.WHITE;

                        currentFloorTime = Optional.of(LineComponent.builder()
                                .left(String.format("Floor %d:", floor.getFloorNumber()))
                                .leftColor(textColor)
                                .right(toMMSS(timetracker.getCurrentFloorDuration()))
                                .rightColor(textColor)
                                .build());
                    }
                }

                final Duration overall = session.getOverallDuration();
                if (config.showOverallTime() && !overall.isZero()) {
                    overallTime = Optional.of(LineComponent.builder()
                            .left("Overall: ")
                            .leftColor(Color.YELLOW)
                            .right(toMMSS(overall))
                            .rightColor(Color.YELLOW)
                            .build());
                }

                if (shouldDisplayFloorTimes()) {
                    for (final CompletedFloor completedFloor : session.getCompletedFloorDurations()) {
                        final String durationString = shouldDisplaySplits() ?
                                String.format("%s (%s)", toMMSS(completedFloor.getDuration()), toMMSS(completedFloor.getSplit())) :
                                String.format("%s", toMMSS(completedFloor.getDuration()));

                        completedFloorTimes.add(LineComponent.builder()
                                .left(String.format("Floor %d:", completedFloor.getFloorNumber()))
                                .right(durationString)
                                .build());
                    }
                }

//                if (config.showCompletedFloorTimes()) {
//                    int floorNumber = 1;
//
//                    Duration cumulative = Duration.ZERO;
//
//                    for (final Duration duration : session.getCompletedFloorDurations()) {
//                        cumulative = cumulative.plus(duration);
//
//                        completedFloorTimes.add(LineComponent.builder()
//                                .left(String.format("Floor %d:", floorNumber++))
//                                .right(String.format("%s (%s)", toMMSS(duration), toMMSS(cumulative)))
//                                .build());
//                    }
//                }

                // Add the rows in the correct order.
                completedFloorTimes.forEach(row -> panelComponent.getChildren().add(row));
                currentFloorTime.ifPresent(row -> panelComponent.getChildren().add(row));
                overallTime.ifPresent(row -> panelComponent.getChildren().add(row));

                final String text = plugin.getActiveSession().isPresent() ?
                        "Current Session" :
                        "Last Session";

                panelComponent.getChildren().add(0, TitleComponent.builder()
                        .text("Hallowed Sepulchre")
                        .color(Color.GREEN)
                        .build());
            }
        }
        return super.render(graphics);
    }

    private boolean shouldDisplayFloorTimes() {
        switch (config.displayCompletedFloorTimes()) {
            case IN_LOBBY:
                return plugin.isInLobby();
            case ALWAYS:
                return plugin.isInLobby() || plugin.isInSepulchre();
            default:
                return false;
        }
    }

    private boolean shouldDisplaySplits() {
        switch (config.displaySplits()) {
            case IN_LOBBY:
                return plugin.isInLobby() && shouldDisplayFloorTimes();
            case ALWAYS:
                return (plugin.isInLobby() || plugin.isInSepulchre()) && shouldDisplayFloorTimes();
            default:
                return false;
        }
    }

    private static String toMMSS(final Duration duration) {
        if (duration.equals(Duration.ZERO)) {
            return "--";
        }

        long minutes = duration.getSeconds() / 60;
        long seconds = duration.getSeconds() % 60;

        long tenthsOfSecond = (duration.toMillis() % 1000) / 100;

        // round up.
        if (tenthsOfSecond >= 5) {
            seconds++;
        }

        return String.format("%01d:%02d", minutes, seconds);
    }
}