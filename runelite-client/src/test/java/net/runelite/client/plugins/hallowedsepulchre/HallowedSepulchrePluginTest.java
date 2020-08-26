package net.runelite.client.plugins.hallowedsepulchre;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import net.runelite.api.Client;
import net.runelite.client.plugins.hallowedsepulchre.overlay.ObstacleOverlay;
import net.runelite.client.plugins.hallowedsepulchre.overlay.TimerOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class HallowedSepulchrePluginTest {

    @Inject
    HallowedSepulchrePlugin plugin;

    @Mock
    @Bind
    HallowedSepulchreConfig config;

    @Mock
    @Bind
    Client client;

    @Mock
    @Bind
    ObstacleOverlay overlay;

    @Mock
    @Bind
    TimerOverlay timerOverlay;

    @Mock
    @Bind
    OverlayManager overlayManager;

    @Before
    public void before()
    {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
    }


    @Test
    public void getFloorCompletedTime() {
        final Map<String, Duration> values = ImmutableMap.of(
                "Floor 1 time: <col=ff0000>1:33</col>. Personal best: 0:26", Duration.ofSeconds(93),
                "Floor 1 time: <col=ff0000>1:33 (new personal best)</col>",  Duration.ofSeconds(93),
                "Floor 1 time: 1:33. Personal best: 0:26",  Duration.ofSeconds(93),
                "Floor 1 time: 1:33 (new personal best)",  Duration.ofSeconds(93)
        );

        // final Optional<Duration> duration = HallowedSepulchrePlugin.extractCompletedFloorTime(message);
        // final Optional<Duration> expected = Optional.of(Duration.ofSeconds(93));

        // assertEquals(expected, duration);
    }

    @Test
    public void checkState() {

    }
}
