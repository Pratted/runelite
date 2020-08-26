package net.runelite.client.plugins.hallowedsepulchre.model;

import com.google.common.base.Stopwatch;
import net.runelite.api.GraphicsObject;
import net.runelite.api.GroundObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.util.RSTimeUnit;

import java.time.Duration;
import java.time.Instant;

public class StrangeTile {

    public static final Duration MAX_ACTIVE_DURATION = Duration.of(4, RSTimeUnit.GAME_TICKS);

    public static final int STRANGE_TILE_YELLOW_ID = 38447;
    public static final int STRANGE_TILE_BLUE_ID = 38448;

    public static Color getColorForId(int id) {
        if (id == STRANGE_TILE_YELLOW_ID) {
            return Color.YELLOW;
        } else if (id == STRANGE_TILE_BLUE_ID) {
            return Color.BLUE;
        } else {
            throw new IllegalArgumentException(id + " is not a Strange Tile id.");
        }
    }

    private final LocalPoint localPoint;
    private final Color color;

    private Stopwatch stopwatch = Stopwatch.createUnstarted();

    public StrangeTile(final LocalPoint localPoint, Color color) {
        this.localPoint = localPoint;
        this.color = color;
    }

    public StrangeTile(final GroundObject groundObject) {
        this.localPoint = groundObject.getLocalLocation();
        this.color = getColorForId(groundObject.getId());
    }

    public void activate() {
        stopwatch.start();
    }

    public void deactivate() {
        stopwatch.reset();
    }

    public double getProgress() {
        return stopwatch.elapsed().toMillis() * 1.0 / MAX_ACTIVE_DURATION.toMillis();
    }

    public boolean isActivated() {
        boolean timeout = stopwatch.elapsed().toMillis() > MAX_ACTIVE_DURATION.toMillis();

        if (timeout) {
            deactivate();
        }

        return stopwatch.isRunning();
    }

    public LocalPoint getLocalLocation() {
        return localPoint;
    }

    public Color getColor() {
        return color;
    }

    public enum Color {
        YELLOW,
        BLUE
    }
}
