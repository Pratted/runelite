package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement.impl;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.runelite.api.Item;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static net.runelite.api.ItemID.*;

public enum Spell {
    LVL_1_ENCHANT(7, rune(COSMIC_RUNE, 1), rune(WATER_RUNE, 1)),
    LVL_2_ENCHANT(27, rune(COSMIC_RUNE, 1), rune(AIR_RUNE, 3)),
    LVL_3_ENCHANT(49, rune(COSMIC_RUNE, 1), rune(FIRE_RUNE, 5)),
    LVL_4_ENCHANT(57, rune(COSMIC_RUNE, 1), rune(EARTH_RUNE, 10)),
    LVL_5_ENCHANT(68, rune(COSMIC_RUNE, 1), rune(EARTH_RUNE, 10), rune(WATER_RUNE, 10)),
    LVL_6_ENCHANT(87, rune(COSMIC_RUNE, 1), rune(EARTH_RUNE, 20), rune(FIRE_RUNE, 20)),
    LVL_7_ENCHANT(93, rune(COSMIC_RUNE, 1), rune(BLOOD_RUNE, 20), rune(SOUL_RUNE, 20));

    private int levelRequired;
    Item[] runesRequired;

    Spell(int levelRequired, Item... runesRequired) {
        this.levelRequired = levelRequired;
        this.runesRequired = runesRequired;
    }

    public static Item rune(int itemId, int qty) {
        return new Item(itemId, qty);
    }

    /**
     * Determines if this spell can be casted using the magic level and the
     * runes available.
     *
     * @param magicLevel
     * @param runesAvailable - The runes that can be used to cast this spell.
     * @return
     */
    public boolean canBeCasted(int magicLevel, Collection<Item> runesAvailable) {
        // A combination rune is implicitly two individual elemental runes.
        // This helper method splits the combo runes into elemental runes
        // because elemental runes are used in the spell requirements.
        final Multiset<Integer> runes = splitComboRunes(runesAvailable);

        for (final Item rune: runesRequired) {
            // There are not enough runes to cast this spell.
            if (runes.count(rune.getId()) < rune.getQuantity()) {
                return false;
            }
        }

        return magicLevel >= levelRequired;
    }

    private static Multiset<Integer> splitComboRunes(final Collection<Item> runes) {
        // Grab all of the combo runes
        final Multiset<Integer> runeQuantities = HashMultiset.create();

        for (final Item rune : runes) {
            switch (rune.getId()) {
                case LAVA_RUNE:
                    safeAdd(runeQuantities, EARTH_RUNE, rune.getQuantity());
                    safeAdd(runeQuantities, FIRE_RUNE, rune.getQuantity());
                    break;
                case MUD_RUNE:
                    safeAdd(runeQuantities, WATER_RUNE, rune.getQuantity());
                    safeAdd(runeQuantities, EARTH_RUNE, rune.getQuantity());
                    break;
                case DUST_RUNE:
                    safeAdd(runeQuantities, AIR_RUNE, rune.getQuantity());
                    safeAdd(runeQuantities, EARTH_RUNE, rune.getQuantity());
                    break;
                case MIST_RUNE:
                    safeAdd(runeQuantities, AIR_RUNE, rune.getQuantity());
                    safeAdd(runeQuantities, WATER_RUNE, rune.getQuantity());
                    break;
                case STEAM_RUNE:
                    safeAdd(runeQuantities, WATER_RUNE, rune.getQuantity());
                    safeAdd(runeQuantities, FIRE_RUNE, rune.getQuantity());
                    break;
                case SMOKE_RUNE:
                    safeAdd(runeQuantities, AIR_RUNE, rune.getQuantity());
                    safeAdd(runeQuantities, FIRE_RUNE,rune.getQuantity());
                    break;
                default:
                    safeAdd(runeQuantities, rune.getId(), rune.getQuantity());
            }
        }

        return runeQuantities;
    }

    private static void safeAdd(Multiset<Integer> runeQuantities, int itemId, long amountToAdd) {
        long existing = runeQuantities.count(itemId);
        long sum = existing + amountToAdd;

        // Check for overflow and just return MAX_VALUE if it does.
        int val = sum > Integer.MAX_VALUE ?
                Integer.MAX_VALUE :
                (int) sum; // this cast is safe because sum is <= MAX_VALUE based on the predicate.

        runeQuantities.setCount(itemId, val);
    }
}