package net.runelite.client.plugins.hallowedsepulchre;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup("hallowedsepulchre")
public interface HallowedSepulchreConfig extends Config {


    @ConfigSection(
            name = "Timers",
            description = "Timers related to the Hallowed Sepulchre",
            position = 0
    )
    String timerSection = "Timers";

    @ConfigItem(
            position = 0,
            keyName = "showFloorTime",
            name = "Show current floor time",
            description = "Shows the current floor time.",
            section = timerSection
    )
    default boolean showFloorTime() {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "showOverallTime",
            name = "Show overall time",
            description = "Shows the overall time.",
            section = timerSection
    )
    default boolean showOverallTime() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "showCompletedFloorTimes",
            name = "Show completed floor times",
            description = "Show the floor times of the completed floors.",
            section = timerSection
    )
    default boolean showCompletedFloorTimes() {
        return false;
    }

    @ConfigSection(
            name = "Loot Tracker",
            description = "Tracks the loot obtained from the hallowed sepulchre",
            position = 3
    )
    String lootingSection = "Loot Tracker";

    @ConfigItem(
            position = 1,
            keyName = "showTotalGpProfit",
            name = "Show total profit",
            description = "Shows the total profit using current GE prices.",
            section = lootingSection
    )
    default boolean showTotalGpProfit() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "showGpHr",
            name = "Show approximate GP/hr",
            description = "Shows the approximate GP/hr using current GE prices.",
            section = lootingSection
    )
    default boolean showGpHrProfit() {
        return false;
    }

    @ConfigItem(
            position = 3,
            keyName = "showTotalMarks",
            name = "Show total marks collected",
            description = "Shows the total number of hallowed marks collected.",
            section = lootingSection
    )
    default boolean showTotalMarks() {
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = "showMarksHr",
            name = "Show hallowed marks/hr",
            description = "Shows the total profit using current GE prices.",
            section = lootingSection
    )
    default boolean showMarksHr() {
        return false;
    }

    @ConfigSection(
            name = "Obstacle Highlighting",
            description = "Obstacle Highlighting",
            position = 5
    )
    String obstacleSection = "Obstacle Highlighting";

    @ConfigItem(
            keyName = "sepulchreHighlightColor",
            name = "Obstacle Color",
            description = "Overlay color for arrows, swords and statues",
            position = 5,
            section = obstacleSection
    )
    default Color highlightColor()
    {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "highlightSepulchreProjectiles",
            name = "Highlight Projectiles",
            description = "Highlights arrows and swords.",
            position = 4,
            section = obstacleSection
    )
    default boolean highlightSepulchreProjectiles()
    {
        return true;
    }

    @ConfigItem(
            keyName = "highlightSepulchreStaircases",
            name = "Highlight Obstacles",
            description = "Highlights pillars and stairs.",
            position = 5,
            section = obstacleSection
    )
    default boolean highlightSepulchreStaircases()
    {
        return true;
    }

    @ConfigItem(
            keyName = "highlightSepulchreSkilling",
            name = "Highlight Skill Challenges",
            description = "Highlights skilling challenges.",
            position = 6,
            section = obstacleSection
    )
    default boolean highlightSepulchreSkilling()
    {
        return true;
    }

    @ConfigItem(
            keyName = "highlightSepulchreStatues",
            name = "Highlight Crossbowman Statues",
            description = "Highlights the crossbowman statues when their animation begins.",
            position = 7,
            section = obstacleSection
    )
    default boolean highlightSepulchreStatues()
    {
        return true;
    }

    @ConfigItem(
            position = 8,
            keyName = "highlightStrangeTiles",
            name = "Highlight strange tiles",
            description = "Highlight the blue and yellow strange tiles in hallowed sepulchre.",
            section = obstacleSection
    )
    default boolean highlightStrangeTiles() {
        return false;
    }

    @ConfigItem(
            position = 9,
            keyName = "showStrangeTileTimers",
            name = "Show strange tile timers",
            description = "Show the time remaining on the strange tile.",
            section = obstacleSection
    )
    default boolean showStrangeTileTimers() {
        return false;
    }

    @ConfigSection(
            name = "Developer",
            description = "Developer",
            position = 20
    )
    String developerSection = "Obstacle Highlighting";

    @ConfigItem(
            position = 21,
            keyName = "hsDeveloper",
            name = "Developer Mode",
            description = "Shows developer information for debugging purposes.",
            section = developerSection
    )
    default boolean developerModeEnabled() {
        return false;
    }
}
