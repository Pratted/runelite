package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2.impl;

import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.Skill;

import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2.IndividualRequirement;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.magic.RuneUtil;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.magic.Spell;

import java.util.List;

public class SpellRequirement extends IndividualRequirement<Spell> {

    public SpellRequirement(Spell spell) {
        super(spell);
    }

    @Override
    public String getDescription() {
        return "Spell";
    }

    @Override
    public boolean isFulfilledBy(Client client) {
        // Gather all runes from the player's inventory/rune pouch/equipment.
        final List<Item> runesAvailable = RuneUtil.getEffectiveRunes(client);

        return getRequirement().canBeCasted(client.getBoostedSkillLevel(Skill.MAGIC), runesAvailable);
    }
}
