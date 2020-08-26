package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2.impl;

import net.runelite.api.Item;

public class ItemQuantityRequirement extends ItemRequirement {

    public ItemQuantityRequirement(Item item) {
        super(item);
    }

    @Override
    public String getDescription() {
        return "Item";
    }

    @Override
    public boolean isFulfilledBy(Item item) {
        return getRequirement().getId() == item.getId() &&
                getRequirement().getQuantity() >= item.getQuantity();
    }
}
