package net.runelite.client.plugins.hallowedsepulchre;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.coords.LocalPoint;

import java.awt.*;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.runelite.api.Constants.CHUNK_SIZE;

/**
 * Utility functions for the Hallowed Sepulchre.
 */
public class HallowedSepulchreUtil {

    private static final Pattern HS_PB_PATTERN = Pattern.compile("Floor (?<floor>\\d) time: <col=ff0000>(?<floortime>[0-9:]+)</col>(?: \\(new personal best\\)|. Personal best: (?<floorpb>[0-9:]+))" +
            "(?:<br>Overall time: <col=ff0000>(?<otime>[0-9:]+)</col>(?: \\(new personal best\\)|. Personal best: (?<opb>[0-9:]+)))?");

    static int extractFloorNumber(final String message) {
        final Matcher matcher = HS_PB_PATTERN.matcher(message);

        if (matcher.find()) {
            Optional<String> floorNumber = Optional.ofNullable(matcher.group("floor"));

            if (floorNumber.isPresent()) {
                return Integer.valueOf(floorNumber.get());
            } else {
                throw new IllegalArgumentException("Could not extract floor number from message: " + message);
            }
        }

        throw new IllegalArgumentException("Could not extract time from message: " + message);
    }

    static Duration extractFloorTime(final String message) {
        final Matcher matcher = HS_PB_PATTERN.matcher(message);

        if (matcher.find()) {
            Optional<String> floortime = Optional.ofNullable(matcher.group("floortime"));

            if (floortime.isPresent()) {
                return extractTime(floortime.get());
            } else {
                throw new IllegalArgumentException("Could not extract floor time from message: " + message);
            }
        }

        throw new IllegalArgumentException("Could not extract time from message: " + message);
    }

    static Optional<Duration> extractOverallTime(final String message) {
        final Matcher matcher = HS_PB_PATTERN.matcher(message);

        if (matcher.find()) {
            Optional<String> overallTime = Optional.ofNullable(matcher.group("otime"));

            if (overallTime.isPresent()) {
                return Optional.of(extractTime(overallTime.get()));
            }
        }

        return Optional.empty();
    }

    private static Duration extractTime(final String mmss) {
        try {
            String minutes = mmss.split(":")[0];
            String seconds = mmss.split(":")[1];

            Duration duration = Duration.ofMinutes(Long.valueOf(minutes));
            duration = duration.plusSeconds(Long.valueOf(seconds));

            return duration;
        } catch (Exception e) {
            throw new IllegalArgumentException("Could convert the time to a Duration: " + mmss, e);
        }
    }

    public static Optional<Integer> getCurrentFloorNumber(final Client client) {
        return FloorPerimeter.getFloorPerimeter(client)
                .map(FloorPerimeter::getFloorNumber);
    }

    static boolean isInLobby(final Client client) {
        return client.getLocalPlayer().getWorldLocation().getRegionID() == 9565;
    }

    private enum FloorPerimeter {
        FLOOR_1(1, new Rectangle(2200, 5920, 128, 128)),
        FLOOR_2(2, new Rectangle(2464, 5912, 128, 128)),
        FLOOR_3(3, new Rectangle(2336, 5792, 128, 128)),
        FLOOR_4(4, new Rectangle(2464, 5784, 128, 128)),
        FLOOR_5(5, new Rectangle(2200, 5800, 100, 100));

        @Getter
        private final int floorNumber;
        private final Rectangle floorPerimeter;

        FloorPerimeter(int floorNumber, Rectangle rectangle) {
            this.floorNumber = floorNumber;
            this.floorPerimeter = rectangle;
        }

        public static Optional<FloorPerimeter> getFloorPerimeter(Client client) {
            if (client.isInInstancedRegion()) {
                final LocalPoint p = client.getLocalPlayer().getLocalLocation();

                int[][][] instanceTemplateChunks = client.getInstanceTemplateChunks();
                int z = client.getPlane();
                int chunkData = instanceTemplateChunks[z][p.getSceneX() / 8][p.getSceneY() / 8];

                // idfk
                int chunkY = (chunkData >> 3 & 0x7FF) * CHUNK_SIZE;
                int chunkX = (chunkData >> 14 & 0x3FF) * CHUNK_SIZE;

                for (FloorPerimeter region : values()) {
                    if (region.floorPerimeter.contains(chunkX, chunkY)) {
                        return Optional.of(region);
                    }
                }
            }

            return Optional.empty();
        }
    }
}
