package net.runelite.client.plugins.hallowedsepulchre.model;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.Renderable;

import java.awt.*;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public abstract class AnimatableGameObject {

    private GameObject gameObject;
    private Renderable renderable;

    boolean isSynchronized = false;

    private Stopwatch stopwatch = Stopwatch.createUnstarted();

    public abstract Duration getMaxFrameDuration();

    public abstract Duration getAnimationDuration();

    protected AnimatableGameObject(final GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public Shape getClickbox() {
        return gameObject.getClickbox();
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public boolean isAnimating() {
        if (isSynchronized) {
            if (renderable != gameObject.getRenderable()) {
                renderable = gameObject.getRenderable();

                if (!stopwatch.isRunning()) {
                    // Begin animation.
                    stopwatch.start();
                }
            }

            // end animation.
            if (stopwatch.elapsed().toMillis() >= getAnimationDuration().toMillis()) {
                stopwatch.reset();
                return false;
            }

            return true;
        } else {
            // The renderable changing indicates the transition to another frame
            // in the animation.
            if (renderable != gameObject.getRenderable()) {
                //log.debug("Changing frame.");
                renderable = gameObject.getRenderable();

                stopwatch.reset();
                stopwatch.start();
            }

            // End of animation
            if (stopwatch.elapsed().toMillis() > getMaxFrameDuration().toMillis()) {
                // isSynchronized = true;
                // stopwatch.stop();
            }

            return stopwatch.elapsed().toMillis() < getMaxFrameDuration().toMillis();
        }
    }
}
