package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2;

import net.runelite.api.Client;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AllOfRequirement implements Requirement<AllOfRequirement> {

    private Set<Requirement<?>> requirements;

    public AllOfRequirement(Requirement<?>... requirements) {
        this.requirements = Arrays.stream(requirements).collect(Collectors.toSet());
    }

    @Override
    public String getDescription() {
        return "AllOfRequirement";
    }

    @Override
    public AllOfRequirement getRequirement() {
        return this;
    }

    @Override
    public boolean isFulfilledBy(Client client) {
        return requirements.stream()
                .allMatch(req -> req.isFulfilledBy(client));
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof AllOfRequirement) {
            return Objects.equals(requirements, ((AllOfRequirement) obj).requirements);
        }

        return false;
    }
}
