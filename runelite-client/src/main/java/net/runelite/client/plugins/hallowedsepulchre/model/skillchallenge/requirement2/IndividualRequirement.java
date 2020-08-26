package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2;

import java.util.Objects;

public abstract class IndividualRequirement<T> implements Requirement<T> {

    T requirement;

    public IndividualRequirement(T requirement) {
        this.requirement = requirement;
    }

    @Override
    public T getRequirement() {
        return requirement;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof IndividualRequirement) {
            return Objects.equals(requirement, ((IndividualRequirement) obj).requirement);
        }

        return false;
    }
}
