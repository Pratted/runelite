package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement.impl;

import net.runelite.api.Client;

/**
 * A {@link PlayerRequirement} is a requirement
 */
public abstract class PlayerRequirement {

    public abstract boolean isFulfilledBy(Client client);
}
