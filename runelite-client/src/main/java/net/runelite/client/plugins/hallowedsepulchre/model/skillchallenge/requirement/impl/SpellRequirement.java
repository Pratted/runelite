package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement.impl;

import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.Skill;

import java.util.List;

public class SpellRequirement extends PlayerRequirement {

    private Spell spell;

    public SpellRequirement(Spell spell) {
        this.spell = spell;
    }

    @Override
    public boolean isFulfilledBy(Client client) {
        // Gather all runes from the player's inventory/rune pouch/equipment.
        final List<Item> runesAvailable = RuneUtil.getEffectiveRunes(client);

        return spell.canBeCasted(client.getBoostedSkillLevel(Skill.MAGIC), runesAvailable);
    }
}
