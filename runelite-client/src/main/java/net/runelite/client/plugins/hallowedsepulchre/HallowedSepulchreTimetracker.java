package net.runelite.client.plugins.hallowedsepulchre;

import com.google.common.base.Stopwatch;
import lombok.Getter;
import net.runelite.client.util.RSTimeUnit;

import java.time.Duration;


public class HallowedSepulchreTimetracker {

    private int floorTicks;
    private int overallTicks;

    private Stopwatch backupStopwatch = Stopwatch.createUnstarted();

    @Getter
    private boolean paused = false;

    public void setFloorTicks(int ticks) {
        floorTicks = ticks;
    }

    public void setOverallTicks(int ticks) {
        overallTicks = ticks;
    }

    public void startBackupTimer() {
        if (!backupStopwatch.isRunning()) {
            backupStopwatch.start();
        }
    }

    public void pause() {
        if (backupStopwatch.isRunning()) {
            backupStopwatch.stop();
        }
    }

    public void reset() {
        backupStopwatch.reset();

        floorTicks = 0;
        overallTicks = 0;
    }

    public Duration getCurrentFloorDuration() {
        return Duration.of(floorTicks, RSTimeUnit.GAME_TICKS).plus(backupStopwatch.elapsed());
    }

    public Duration getOverallDuration() {
        return Duration.of(overallTicks, RSTimeUnit.GAME_TICKS).plus(backupStopwatch.elapsed());
    }
}
