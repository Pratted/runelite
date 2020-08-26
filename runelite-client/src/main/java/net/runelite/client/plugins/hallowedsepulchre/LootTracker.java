package net.runelite.client.plugins.hallowedsepulchre;

import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.loottracker.LootReceived;
import net.runelite.http.api.loottracker.LootRecordType;

import java.time.Duration;

@Slf4j
public class LootTracker {

    private static final String HALLOWED_SEPULCHRE_COFFIN_EVENT = "Coffin (Hallowed Sepulchre)";

    private final ItemManager itemManager;

    private final Stopwatch stopwatch;

    @Getter
    private long totalGpLooted;

    @Getter
    private long totalMarksLooted;

    public LootTracker(final ItemManager itemManager) {
        this.itemManager = itemManager;
        this.stopwatch = Stopwatch.createUnstarted();

        totalGpLooted = 0;
        totalMarksLooted = 0;
    }

    public void reset() {
        totalGpLooted = 0;
        totalMarksLooted = 0;

        stopwatch.reset();
    }

    public double getGpPerHour() {
        // Default to 60 to prevent division by 0.
        long seconds = Math.max(stopwatch.elapsed().getSeconds(), 60);

        return (totalGpLooted * 1.0 / seconds) * 3600;
    }

    public double getMarksPerHour() {
        long seconds = Math.max(stopwatch.elapsed().getSeconds(), 60);

        return (totalMarksLooted * 1.0d / seconds) * 3600;
    }

    @Subscribe
    public void onLootReceived(final LootReceived lootReceived) {
        if (lootReceived.getType().equals(LootRecordType.EVENT) &&
                HALLOWED_SEPULCHRE_COFFIN_EVENT.equals(lootReceived.getName())) {

            // First loot event triggers the stopwatch.
            if (!stopwatch.isRunning()) {
                stopwatch.start();
            }

            lootReceived.getItems().forEach(itemStack -> {
                int itemId = itemStack.getId();

                if (itemId == ItemID.HALLOWED_MARK) {
                    log.debug("Received {} Hallow Marks", itemStack.getQuantity());
                    totalMarksLooted += itemStack.getQuantity();
                } else {
                    long estimatedValue = itemManager.getItemPrice(itemId) * itemStack.getQuantity();

                    log.debug("Estimated Loot Value:", estimatedValue);
                    totalGpLooted += estimatedValue;
                }
            });
        }
    }
}
