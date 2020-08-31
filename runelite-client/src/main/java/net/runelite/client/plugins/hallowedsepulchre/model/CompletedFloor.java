package net.runelite.client.plugins.hallowedsepulchre.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

@AllArgsConstructor
public class CompletedFloor {

    @Getter
    private int floorNumber;

    @Getter
    private Duration duration;

    @Getter
    private Duration split;
}
