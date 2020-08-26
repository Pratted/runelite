package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.old;


import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;

import java.util.Optional;

public class ConstructionSkillChallenge extends SkillChallenge {

    public ConstructionSkillChallenge(Client client) {
        super(client);
    }

    @Override
    public Skill getSkill() {
        return Skill.CONSTRUCTION;
    }

    @Override
    public int skillLevelRequirement() {
        return 56;
    }

    @Override
    protected boolean hasMaterials() {
        final Optional<ItemContainer> maybeInventory = getInventory();

        if (maybeInventory.isPresent()) {
            final ItemContainer inventory = maybeInventory.get();

            // The player needs 2+ planks and 5+ nails of the proper type.
            // We need to include the nails because only one type of nail can be used with a type of plank.
            boolean canUsePlanks = inventory.count(ItemID.PLANK) >= 2 && inventory.count(ItemID.STEEL_NAILS) >= 5;
            boolean canUseOakPlanks = inventory.count(ItemID.OAK_PLANK) >= 2 && inventory.count(ItemID.MITHRIL_NAILS) >= 5;
            boolean canUseTeakPlanks = inventory.count(ItemID.TEAK_PLANK) >= 2 && inventory.count(ItemID.ADAMANTITE_NAILS) >= 5;
            boolean canUseMahoganyPlanks = inventory.count(ItemID.MAHOGANY_PLANK) >= 2 && inventory.count(ItemID.RUNE_NAILS) >= 5;

            boolean hasPlanksAndNails = canUsePlanks || canUseOakPlanks || canUseTeakPlanks || canUseMahoganyPlanks;

            boolean hasSaw = inventory.contains(ItemID.SAW);
            boolean hasHammer = inventory.contains(ItemID.HAMMER) || inventory.contains(ItemID.HALLOWED_HAMMER);

            // The player needs a saw, hammer, 2 planks and 5 nails (of the correct type) to complete the challenge.
            return hasSaw && hasHammer && hasPlanksAndNails;
        }

        return false;
    }
}
