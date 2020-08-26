package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.old;

import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.client.plugins.hallowedsepulchre.SpellUtil;

import java.util.*;
import java.util.stream.Collectors;

public class MagicSkillChallenge extends SkillChallenge {

    public MagicSkillChallenge(Client client) {
        super(client);
    }

    @Override
    public Skill getSkill() {
        return Skill.MAGIC;
    }

    @Override
    public int skillLevelRequirement() {
        return 7;
    }

    @Override
    protected boolean hasMaterials() {
        List<Item> inventory = toList(getInventory());
        List<Item> equipment = toList(getEquipment());

        Map<SpellUtil.Rune, Integer> runes = SpellUtil.getRunesAvailable(inventory, equipment);

        return true; // SpellUtil.canCastAnyEnchantmentSpell(getSkill(), runes);
    }

    private static List<Item> toList(Optional<ItemContainer> maybeItemContainer) {
        return maybeItemContainer
                .map(inv -> Arrays.stream(inv.getItems())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
