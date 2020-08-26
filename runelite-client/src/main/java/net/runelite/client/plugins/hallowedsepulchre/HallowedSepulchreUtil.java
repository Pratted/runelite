package net.runelite.client.plugins.hallowedsepulchre;

import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility functions for the Hallowed Sepulchre.
 */
public class HallowedSepulchreUtil {

    private static final Pattern HS_PB_PATTERN = Pattern.compile("Floor (?<floor>\\d) time: <col=ff0000>(?<floortime>[0-9:]+)</col>(?: \\(new personal best\\)|. Personal best: (?<floorpb>[0-9:]+))" +
            "(?:<br>Overall time: <col=ff0000>(?<otime>[0-9:]+)</col>(?: \\(new personal best\\)|. Personal best: (?<opb>[0-9:]+)))?");

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
}
