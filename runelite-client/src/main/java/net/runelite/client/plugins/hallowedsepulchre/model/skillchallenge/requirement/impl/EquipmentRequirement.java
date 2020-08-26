package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement.impl;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;


public class EquipmentRequirement extends PlayerRequirement {

    private final Item item;

    public EquipmentRequirement(Item item) {
        this.item = item;
    }

    public EquipmentRequirement(int itemId) {
        this.item = new Item(itemId, 1);
    }

    @Override
    public boolean isFulfilledBy(Client client) {
        return client.getItemContainer(InventoryID.EQUIPMENT).contains(item.getId());
    }
}
