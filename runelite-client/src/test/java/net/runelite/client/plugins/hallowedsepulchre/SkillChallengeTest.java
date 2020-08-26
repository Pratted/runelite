package net.runelite.client.plugins.hallowedsepulchre;

import net.runelite.api.*;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.SkillChallenge;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2.Requirement;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2.Requirements;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2.Requirements.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SkillChallengeTest {

    private static Item HALLOWED_SYMBOL = new Item(ItemID.HALLOWED_SYMBOL, 1);

    public static class TestContainer implements ItemContainer {

        final List<Item> items;

        public TestContainer(Item... items) {
            this.items = Arrays.stream(items).collect(Collectors.toList());
        }

        public static TestContainer of(Item... items) {
            return new TestContainer(items);
        }

        @Nonnull
        @Override
        public Item[] getItems() {
            Item[] copy = new Item[items.size()];
            return items.toArray(copy);
        }

        @Nullable
        @Override
        public Item getItem(int slot) {
            throw new UnsupportedOperationException("Not implemented for tests.");
        }

        @Override
        public boolean contains(int itemId) {
            throw new UnsupportedOperationException("Not implemented for tests.");
        }

        @Override
        public int count(int itemId) {
            return (int) items.stream()
                    .filter(Objects::nonNull)
                    .filter(item -> item.getId() == itemId)
                    .count();
            //throw new UnsupportedOperationException("Not implemented for tests.");
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Node getNext() {
            throw new UnsupportedOperationException("Not implemented for tests.");
        }

        @Override
        public Node getPrevious() {
            throw new UnsupportedOperationException("Not implemented for tests.");
        }

        @Override
        public long getHash() {
            throw new UnsupportedOperationException("Not implemented for tests.");
        }
    }


    @Test
    void eric() {
        assertEquals(allOf(), allOf());
    }

    @Test
    void colin() {

        Client client = mock(Client.class);

        when(client.getRealSkillLevel(eq(Skill.PRAYER))).thenReturn(99);

        when(client.getItemContainer(eq(InventoryID.INVENTORY))).thenReturn(TestContainer.of(
            new Item(ItemID.VAMPYRE_DUST, 1)
        ));

        when(client.getItemContainer(eq(InventoryID.EQUIPMENT))).thenReturn(TestContainer.of(
                // HALLOWED_SYMBOL
        ));

        assertTrue(SkillChallenge.PRAYER.canComplete(client));
    }

}
