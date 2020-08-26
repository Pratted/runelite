package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement;

import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement.impl.Spell;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement.impl.ItemRequirement;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement.impl.SkillLevelRequirement;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement.impl.EquipmentRequirement;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement.impl.SpellRequirement;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Requirements {


    public static AllOfRequirement allOf(Requirement... requirements) {
        return new AllOfRequirement(requirements);
    }

    public static AnyOfRequirement anyOf(Requirement... requirements) {
        return new AnyOfRequirement(requirements);
    }

    public static AnyOfRequirement anyEquipment(Integer... equipmentIds) {
        return new AnyOfRequirement();
    }

    public static IndividualRequirement item(int itemId) {
        return new IndividualRequirement(new ItemRequirement(itemId));
    }

    public static IndividualRequirement equipment(int itemId) {
        return new IndividualRequirement(new EquipmentRequirement(itemId));
    }

    public static IndividualRequirement skillLevel(Skill skill, int levelRequired) {
        return new IndividualRequirement(new SkillLevelRequirement(skill, levelRequired));
    }

    public static IndividualRequirement xOfItem(int itemId, int qty) {
        return new IndividualRequirement(new ItemRequirement(itemId, qty));
    }

    public static IndividualRequirement spell(Spell spell) {
        return new IndividualRequirement(new SpellRequirement(spell));
    }

    enum SkillChallenge {
        GRAPPLE(
                allOf(
                        skillLevel(Skill.RANGED, 62),
                        anyOf(equipment(1), equipment(1)),
                        anyOf(equipment(2), equipment(2), equipment(3)))
        ),
        PRAYER (
                anyOf(
                        skillLevel(Skill.PRAYER, 54),
                        allOf(item(1), equipment(1)),
                        allOf(item(1))
                )
        ),
        CONSTRUCTION (
                allOf(
                        skillLevel(Skill.PRAYER, 56),
                        item(1),
                        anyOf(item(1), item(2)),
                        anyOf(
                                allOf(xOfItem(3, 2), xOfItem(4, 5)),
                                allOf(xOfItem(3, 2), xOfItem(4, 5)),
                                allOf(xOfItem(3, 2), xOfItem(4, 5)),
                                allOf(xOfItem(3, 2), xOfItem(4, 5))
                        )
                )
        ),
        MAGIC (
                anyOf (
                        spell(Spell.LVL_1_ENCHANT), spell(Spell.LVL_2_ENCHANT), spell(Spell.LVL_3_ENCHANT),
                        spell(Spell.LVL_4_ENCHANT), spell(Spell.LVL_5_ENCHANT), spell(Spell.LVL_6_ENCHANT),
                        spell(Spell.LVL_7_ENCHANT)
                )
        );

        private Set<Requirement> requirements;

        SkillChallenge(Requirement... requirements) {
            this.requirements = Arrays.stream(requirements).collect(Collectors.toSet());
        }

        boolean canComplete(Client client) {
            return requirements.stream()
                    .allMatch(req -> req.isFulfilledBy(client));
        }
    }
}
