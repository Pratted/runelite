package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.old;

import net.runelite.api.*;

import java.util.Optional;

public class PrayerSkillChallenge extends SkillChallenge {

    public PrayerSkillChallenge(Client client) {
        super(client);
    }

    @Override
    public Skill getSkill() {
        return Skill.PRAYER;
    }

    @Override
    public int skillLevelRequirement() {
        return 54;
    }

    @Override
    protected boolean hasMaterials() {
        final Optional<ItemContainer> inventory = getInventory();
        final Optional<ItemContainer> equipment = getEquipment();

        final int vampyreDustQty = inventory
                .map(inv -> inv.count(ItemID.VAMPYRE_DUST))
                .orElse(0);

        final int quantityNeeded = equipment
                .map(e -> e.count(ItemID.HALLOWED_SYMBOL))
                .map(amuletQty -> amuletQty == 1 ? 1 : 2) // If the symbol is equipped, we only need 1 dust, otherwise 2.
                .orElse(2);

        return vampyreDustQty > quantityNeeded;
    }
}
