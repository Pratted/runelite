package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.old;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;

import java.util.Optional;

public abstract class SkillChallenge {

    private final Client client;

    protected SkillChallenge(Client client) {
        this.client = client;
    }

    /**
     * @return The {@link Skill} this challenge is for.
     */
    public abstract Skill getSkill();

    /**
     * @return The minimum skill level needed to complete this challenge.
     */
    public abstract int skillLevelRequirement();

    /**
     * @return True if the player has all items and equipment required to complete this challenge, otherwise false.
     */
    protected abstract boolean hasMaterials();

    /**
     * @return The player's inventory wrapped in an Optional.
     */
    protected Optional<ItemContainer> getInventory() {
        return Optional.ofNullable(client.getItemContainer(InventoryID.INVENTORY));
    }

    /**
     * @return The player's equipment wrapped in an Optional.
     */
    protected Optional<ItemContainer> getEquipment() {
        return Optional.ofNullable(client.getItemContainer(InventoryID.EQUIPMENT));
    }

    /**
     * @return The player's level for this {@link #getSkill()}.
     */
    protected int getSkillLevel(Client client) {
        return client.getRealSkillLevel(getSkill());
    }

    /**
     * @return True if the player can complete this skill challenge, otherwise false.
     */
    public boolean canCompleteChallenge(Client client) {
        return meetsSkillLevelRequirement(client) & hasMaterials();
    }

    /**
     * @return True if the player has a high enough skill level to attempt this challenge, otherwise false.
     */
    private boolean meetsSkillLevelRequirement(Client client) {
        return getSkillLevel(client) >= skillLevelRequirement();
    }
}