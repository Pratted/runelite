package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement;

import net.runelite.api.Client;

public interface Requirement {

    default AllOfRequirement and(Requirement other) {
        return new AllOfRequirement(this, other);
    }

    default AnyOfRequirement or(Requirement other) {
        return new AnyOfRequirement(this, other);
    }

    boolean isFulfilledBy(Client client);
}
