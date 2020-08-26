package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2.impl;

import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2.IndividualRequirement;

public class SkillRequirement extends IndividualRequirement<Integer> {

    Skill skill;

    public SkillRequirement(Skill skill, int levelNeeded) {
        super(levelNeeded);
        this.skill = skill;
    }

    @Override
    public String getDescription() {
        return skill + " Level";
    }

    @Override
    public boolean isFulfilledBy(Client client) {
        return client.getRealSkillLevel(Skill.PRAYER) >= getRequirement();
    }
}
