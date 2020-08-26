package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement;

import net.runelite.api.Client;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement.impl.PlayerRequirement;

public class IndividualRequirement implements Requirement {

    // Composed with a player requirement.
    private final PlayerRequirement playerRequirement;

    public IndividualRequirement(PlayerRequirement playerRequirement) {
        this.playerRequirement = playerRequirement;
    }

    @Override
    public boolean isFulfilledBy(Client client) {
        return playerRequirement.isFulfilledBy(client);
    }
}
