package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement.impl;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;

public class ItemRequirement extends PlayerRequirement {

    private Item item;

    public ItemRequirement(Item item) {
        this.item = item;
    }

    public ItemRequirement(int itemId, int qty) {
        this.item = new Item(itemId, qty);
    }

    public ItemRequirement(int itemId) {
        this.item = new Item(itemId, 0);
    }

    @Override
    public boolean isFulfilledBy(Client client) {
        int qty = client.getItemContainer(InventoryID.INVENTORY).count(item.getId());
        return qty > 0;
    }
}
