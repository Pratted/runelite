package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2;

import net.runelite.api.Item;
import net.runelite.api.Skill;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2.impl.*;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.magic.Spell;

public class Requirements {

    public static AnyOfRequirement anyOf(Requirement ...requirements) {
        return new AnyOfRequirement(requirements);
    }

    public static AllOfRequirement allOf(Requirement ...requirements) {
        return new AllOfRequirement(requirements);
    }

    public static ItemRequirement item(int id) {
        return new ItemRequirement(new Item(id, 1));
    }

    public static ItemQuantityRequirement xOfItem(Item item) {
        return new ItemQuantityRequirement(item);
    }

    public static ItemQuantityRequirement xOfItem(int itemId, int qty) {
        return new ItemQuantityRequirement(new Item(itemId, qty));
    }

    public static EquipmentRequirement equipment(Item item) {
        return new EquipmentRequirement(item);
    }

    public static EquipmentRequirement equipment(int id) {
        return new EquipmentRequirement(new Item(id, 1));
    }

    public static SkillRequirement skill(Skill skill, int lvl) {
        return new SkillRequirement(skill, lvl);
    }

    public static SpellRequirement spell(Spell spell) {
        return new SpellRequirement(spell);
    }
}
