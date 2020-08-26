package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2;

import net.runelite.api.Client;

public interface Requirement<T> {

    String getDescription();

    T getRequirement();

    boolean isFulfilledBy(Client client);
}
