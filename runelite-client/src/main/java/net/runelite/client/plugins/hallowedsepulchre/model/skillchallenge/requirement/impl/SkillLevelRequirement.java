package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement.impl;

import net.runelite.api.Client;
import net.runelite.api.Skill;

public class SkillLevelRequirement extends PlayerRequirement {

    private final Skill skill;
    private final int levelRequired;

    public SkillLevelRequirement(Skill skill, int levelRequired) {
        this.skill = skill;
        this.levelRequired = levelRequired;
    }

    @Override
    public boolean isFulfilledBy(Client client) {
        int lvl = client.getRealSkillLevel(skill);
        return lvl >= levelRequired;
    }
}
