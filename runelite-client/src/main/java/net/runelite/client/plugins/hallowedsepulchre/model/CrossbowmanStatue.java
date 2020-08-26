package net.runelite.client.plugins.hallowedsepulchre.model;

import net.runelite.api.GameObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.util.RSTimeUnit;

import java.time.Duration;

public class CrossbowmanStatue extends AnimatableGameObject {

    private LocalPoint localPoint;

    public CrossbowmanStatue(final GameObject gameObject) {
        super(gameObject);
        this.localPoint = gameObject.getLocalLocation();
    }

    public LocalPoint getLocalPoint() {
        return localPoint;
    }

    @Override
    public Duration getMaxFrameDuration() {
        return Duration.ofMillis(700);
    }

    @Override
    public Duration getAnimationDuration() {
        return Duration.of(2, RSTimeUnit.GAME_TICKS);
    }
}
