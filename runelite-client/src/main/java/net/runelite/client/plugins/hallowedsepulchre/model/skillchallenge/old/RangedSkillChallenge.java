package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.old;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.*;

import java.util.Optional;
import java.util.Set;

public class RangedSkillChallenge extends SkillChallenge {

    private static final Set<Integer> GRAPPLES = ImmutableSet.of(
            ItemID.MITH_GRAPPLE, ItemID.HALLOWED_GRAPPLE
    );

    private static final Set<Integer> CROSSBOWS = ImmutableSet.of(
            ItemID.CROSSBOW,
            ItemID.ADAMANT_CROSSBOW,
            ItemID.ARMADYL_CROSSBOW_23611, ItemID.ARMADYL_CROSSBOW,
            ItemID.BLURITE_CROSSBOW,
            ItemID.BRONZE_CROSSBOW,
            ItemID.DORGESHUUN_CROSSBOW,
            ItemID.DRAGON_CROSSBOW,
            ItemID.DRAGON_HUNTER_CROSSBOW,
            ItemID.HUNTERS_CROSSBOW,
            ItemID.IRON_CROSSBOW,
            ItemID.KARILS_CROSSBOW, ItemID.KARILS_CROSSBOW_0, ItemID.KARILS_CROSSBOW_25,
            ItemID.KARILS_CROSSBOW_50, ItemID.KARILS_CROSSBOW_75, ItemID.KARILS_CROSSBOW_100,
            ItemID.PHOENIX_CROSSBOW,
            ItemID.RUNE_CROSSBOW, ItemID.RUNE_CROSSBOW_23601,
            ItemID.STEEL_CROSSBOW
    );

    public RangedSkillChallenge(Client client) {
        super(client);
    }

    @Override
    public Skill getSkill() {
        return Skill.RANGED;
    }

    @Override
    public int skillLevelRequirement() {
        return 62;
    }

    @Override
    protected boolean hasMaterials() {
        final Optional<ItemContainer> maybeEquipment = getEquipment();

        if (maybeEquipment.isPresent()) {
            final ItemContainer equipment = maybeEquipment.get();

            Optional<Item> maybeAmmo = Optional.ofNullable(equipment.getItem(EquipmentInventorySlot.AMMO.getSlotIdx()));
            Optional<Item> maybeCrossbow = Optional.ofNullable(equipment.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx()));

            boolean hasCrossbow = maybeCrossbow
                    .map(crossbow -> CROSSBOWS.contains(crossbow.getId()))
                    .orElse(false);

            boolean hasGrapple = maybeAmmo
                    .map(grapple -> GRAPPLES.contains(grapple.getId()))
                    .orElse(false);

            // A crossbow and grapple need to be present to complete the challenge.
            return hasCrossbow && hasGrapple;
        }

        return false;
    }
}
