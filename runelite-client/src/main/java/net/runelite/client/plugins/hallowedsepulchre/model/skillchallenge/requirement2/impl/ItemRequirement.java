package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2.impl;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2.IndividualRequirement;

import java.util.Arrays;
import java.util.Objects;

public class ItemRequirement extends IndividualRequirement<Item> {

    public ItemRequirement(Item item) {
        super(item);
    }

    @Override
    public String getDescription() {
        return "Item";
    }

    @Override
    public boolean isFulfilledBy(Client client) {
        return isFulfilledBy(client.getItemContainer(InventoryID.INVENTORY).getItems());
    }

    public boolean isFulfilledBy(Item item) {
        return getRequirement().getId() == item.getId();
    }

    public boolean isFulfilledBy(Item[] items) {
        return Arrays.stream(items)
                .filter(Objects::nonNull)
                .anyMatch(this::isFulfilledBy);
    }
}
