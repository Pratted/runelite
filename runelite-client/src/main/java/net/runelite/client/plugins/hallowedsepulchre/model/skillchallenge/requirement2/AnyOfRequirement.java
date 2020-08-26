package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2;

import net.runelite.api.Client;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class AnyOfRequirement implements Requirement<AnyOfRequirement> {

    private Set<Requirement<?>> requirements;

    public AnyOfRequirement(Requirement<?>... requirements) {
        this.requirements = Arrays.stream(requirements).collect(Collectors.toSet());
    }

    @Override
    public String getDescription() {
        return "AnyOfRequirement";
    }

    @Override
    public AnyOfRequirement getRequirement() {
        return this;
    }

    @Override
    public boolean isFulfilledBy(Client client) {
        return requirements.stream()
                .anyMatch(req -> req.isFulfilledBy(client));
    }
}
