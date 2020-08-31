package net.runelite.client.plugins.hallowedsepulchre.overlay;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

import net.runelite.client.plugins.hallowedsepulchre.HallowedSepulchreConfig;
import net.runelite.client.plugins.hallowedsepulchre.HallowedSepulchrePlugin;
import net.runelite.client.plugins.hallowedsepulchre.LootTracker;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.function.Supplier;

public class LootOverlay extends OverlayPanel {

    private final HallowedSepulchrePlugin plugin;
    private final HallowedSepulchreConfig config;

    @Inject
    private LootOverlay(
            HallowedSepulchrePlugin plugin,
            HallowedSepulchreConfig config) {
        super(plugin);

        setPosition(OverlayPosition.BOTTOM_LEFT);
        setPriority(OverlayPriority.LOW);

        this.plugin = plugin;
        this.config = config;

        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "HS_LOOT_OVERLAY"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        // We only want to show HS information if the player is in the HS or in the lobby.
        if (plugin.isInLobby() || plugin.getMostRecentSession().isPresent()) {
            final LootTracker lootTracker = plugin.getLootTracker();
            final double totalProfit = lootTracker.getTotalGpLooted();
            final double gpPerHour = lootTracker.getGpPerHour();
            final double totalMarks = lootTracker.getTotalMarksLooted();
            final double marksPerHour = lootTracker.getMarksPerHour();

            // Only show the row if we've actually made profit (i.e. looted something).
            if (config.showTotalGpProfit() && totalProfit > 0d) {
                addLootComponent("Total Profit:", () -> formatGp(totalProfit));
            }

            if (config.showGpHrProfit() && gpPerHour > 0d) {
                addLootComponent("Gp/Hr:", () -> formatGp(gpPerHour));
            }

            if (config.showTotalMarks() && gpPerHour > 0d) {
                addLootComponent("Marks Collected:", () -> formatMarks(totalMarks));
            }

            if (config.showMarksHr() && marksPerHour > 0d) {
                addLootComponent("Marks/Hr:", () -> formatMarks(marksPerHour));
            }

            if (!panelComponent.getChildren().isEmpty()) {
                panelComponent.getChildren().add(0, TitleComponent.builder()
                        .text("Loot Tracker")
                        .color(Color.GREEN)
                        .build());
            }
        }

        return super.render(graphics);
    }

    private void addLootComponent(final String left, final Supplier<String> right) {
        LineComponent lootComponent = LineComponent.builder()
                .left(left)
                .right(right.get())
                .build();

        panelComponent.getChildren().add(lootComponent);
    }

    private static String formatGp(double gp) {
        if (gp == 0) {
            return "--";
        } else if (gp < 1000) {
            return String.valueOf(gp);
        } else {
            double k = gp / 1000.0d;

            DecimalFormat formatter = new DecimalFormat(k > 1000.0d ? "#" : "#.#");
            formatter.setRoundingMode(RoundingMode.HALF_UP);

            return formatter.format(k) + "k";
        }
    }

    private static String formatMarks(double marks) {
        if (marks == 0) {
            return "--";
        } else {
            DecimalFormat formatter = new DecimalFormat("#.#");
            formatter.setRoundingMode(RoundingMode.HALF_UP);

            return formatter.format(marks);
        }
    }
}