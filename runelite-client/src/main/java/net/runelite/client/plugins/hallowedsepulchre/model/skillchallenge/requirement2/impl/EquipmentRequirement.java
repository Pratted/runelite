package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2.impl;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;

public class EquipmentRequirement extends ItemRequirement {

    public EquipmentRequirement(Item item) {
        super(item);
    }

    @Override
    public boolean isFulfilledBy(Client client) {
        return isFulfilledBy(client.getItemContainer(InventoryID.EQUIPMENT).getItems());
    }
}
