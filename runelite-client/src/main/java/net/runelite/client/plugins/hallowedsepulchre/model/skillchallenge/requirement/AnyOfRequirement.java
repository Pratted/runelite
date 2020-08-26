package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement;

import net.runelite.api.Client;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AnyOfRequirement implements Requirement {

    private Set<Requirement> requirements;

    public AnyOfRequirement(Requirement... requirements) {
        this.requirements = Arrays.stream(requirements).collect(Collectors.toSet());
    }

    @Override
    public boolean isFulfilledBy(Client client) {
        return requirements.stream()
                .anyMatch(req -> req.isFulfilledBy(client));
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof AnyOfRequirement) {
            return Objects.equals(requirements, ((AnyOfRequirement) obj).requirements);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requirements);
    }
}
