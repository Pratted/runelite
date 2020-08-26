package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement.impl;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemID;

import java.util.*;
import java.util.stream.Collectors;

import static net.runelite.api.ItemID.*;
import static net.runelite.api.ItemID.SMOKE_RUNE;
import static net.runelite.api.ItemID.WRATH_RUNE;

public class RuneUtil {

    private static final Integer INFINITY = Integer.MAX_VALUE;

    private enum Rune {
        AIR(1, AIR_RUNE),
        WATER(2, WATER_RUNE),
        EARTH(3, EARTH_RUNE),
        FIRE(4, FIRE_RUNE),
        MIND(5, MIND_RUNE),
        CHAOS(6, CHAOS_RUNE),
        DEATH(7, DEATH_RUNE),
        BLOOD(8, BLOOD_RUNE),
        COSMIC(9, COSMIC_RUNE),
        NATURE(10, NATURE_RUNE),
        LAW(11, LAW_RUNE),
        BODY(12, BODY_RUNE),
        SOUL(13, SOUL_RUNE),
        ASTRAL(14, ASTRAL_RUNE),

        // Combo Runes
        MIST(15, MIST_RUNE, AIR, WATER),
        MUD(16, MUD_RUNE, WATER, EARTH),
        DUST(17, DUST_RUNE, AIR, EARTH),
        LAVA(18, LAVA_RUNE, EARTH, FIRE),
        STEAM(19, STEAM_RUNE, WATER, FIRE),
        SMOKE(20, SMOKE_RUNE, AIR, FIRE),

        WRATH(21, WRATH_RUNE);

        @Getter
        private int id;

        @Getter
        private int itemId;

        Rune(int id, int itemId) {
            this.id = id;
            this.itemId = itemId;
        }

        Rune(int id, int itemId, Rune elementalRune1, Rune elementalRune2) {
            this.id = id;
            this.itemId = itemId;
        }

        public static Optional<Rune> getRune(int id) {
            for (Rune rune : values()) {
                if (rune.id == id) {
                    return Optional.of(rune);
                }
            }

            return Optional.empty();
        }

        public static boolean isRune(int id) {
            return getRune(id).isPresent();
        }
    }

    private enum RuneSource {
        // Elemental staves
        STAFF_OF_AIR(ItemID.STAFF_OF_AIR, Rune.AIR),
        STAFF_OF_WATER(ItemID.STAFF_OF_WATER, Rune.WATER),
        STAFF_OF_EARTH(ItemID.STAFF_OF_EARTH, Rune.EARTH),
        STAFF_OF_FIRE(ItemID.STAFF_OF_FIRE, Rune.FIRE),

        // Battlestaves
        AIR_BATTLESTAFF(ItemID.AIR_BATTLESTAFF, Rune.AIR),
        WATER_BATTLESTAFF(ItemID.WATER_BATTLESTAFF, Rune.WATER),
        EARTH_BATTLESTAFF(ItemID.EARTH_BATTLESTAFF, Rune.EARTH),
        FIRE_BATTLESTAFF(ItemID.FIRE_BATTLESTAFF, Rune.FIRE),
        LAVA_BATTLESTAFF(ItemID.LAVA_BATTLESTAFF, Rune.LAVA),
        LAVA_BATTLESTAFF_OR(ItemID.LAVA_BATTLESTAFF_21198, Rune.LAVA),
        MUD_BATTLESTAFF(ItemID.MUD_BATTLESTAFF, Rune.MUD),
        STEAM_BATTLESTAFF(ItemID.STEAM_BATTLESTAFF, Rune.STEAM),
        STEAM_BATTLESTAFF_OR(ItemID.STEAM_BATTLESTAFF_12795, Rune.STEAM),
        SMOKE_BATTLESTAFF(ItemID.SMOKE_BATTLESTAFF, Rune.SMOKE),
        MIST_BATTLESTAFF(ItemID.MIST_BATTLESTAFF, Rune.MIST),
        DUST_BATTLESTAFF(ItemID.DUST_BATTLESTAFF, Rune.DUST),

        // Mystic staves
        MYSTIC_AIR_STAFF(ItemID.MYSTIC_AIR_STAFF, Rune.AIR),
        MYSTIC_WATER_STAFF(ItemID.MYSTIC_WATER_STAFF, Rune.WATER),
        MYSTIC_EARTH_STAFF(ItemID.MYSTIC_EARTH_STAFF, Rune.EARTH),
        MYSTIC_FIRE_STAFF(ItemID.MYSTIC_FIRE_STAFF, Rune.FIRE),
        MYSTIC_LAVA_STAFF(ItemID.MYSTIC_LAVA_STAFF, Rune.LAVA),
        MYSTIC_LAVA_STAFF_OR(ItemID.MYSTIC_LAVA_STAFF_21200, Rune.LAVA),
        MYSTIC_MUD_STAFF(ItemID.MYSTIC_MUD_STAFF, Rune.MUD),
        MYSTIC_STEAM_STAFF(ItemID.MYSTIC_STEAM_STAFF, Rune.STEAM),
        MYSTIC_STEAM_STAFF_OR(ItemID.MYSTIC_STEAM_STAFF_12796, Rune.STEAM),
        MYSTIC_SMOKE_STAFF(ItemID.MYSTIC_SMOKE_STAFF, Rune.SMOKE),
        MYSTIC_MIST_STAFF(ItemID.MYSTIC_MIST_STAFF, Rune.MIST),
        MYSTIC_DUST_STAFF(ItemID.MYSTIC_DUST_STAFF, Rune.DUST),

        // Misc
        TOME_OF_FIRE(ItemID.TOME_OF_FIRE, Rune.FIRE),
        KODAI_WAND(ItemID.KODAI_WAND, Rune.WATER);

        @Getter
        private int id;

        @Getter
        private Rune rune;

        RuneSource(int id, Rune rune) {
            this.id = id;
            this.rune = rune;
        }

        private static Optional<RuneSource> getRuneSource(int id) {
            for (RuneSource equipment : values()) {
                if (equipment.id == id) {
                    return Optional.of(equipment);
                }
            }

            return Optional.empty();
        }

        public static boolean isRuneSource(int id) {
            return getRuneSource(id).isPresent();
        }

        public static RuneSource fromId(int id) {
            return getRuneSource(id).orElseThrow(() -> new IllegalArgumentException(id + " is not a rune source."));
        }
    }

    public static List<Item> getEffectiveRunes(Client client) {
        final List<Item> inventory = Optional.ofNullable(client.getItemContainer(InventoryID.INVENTORY))
                .map(items -> Arrays.asList(items.getItems()))
                .orElse(Collections.emptyList());

        final List<Item> equipment = Optional.ofNullable(client.getItemContainer(InventoryID.EQUIPMENT))
                .map(items -> Arrays.asList(items.getItems()))
                .orElse(Collections.emptyList());

        return getEffectiveRunes(inventory, equipment);
    }

    public static List<Item> getEffectiveRunes(Collection<Item> inventory, Collection<Item> equipment) {
        final Multiset<Rune> runesAvailable = HashMultiset.create();

        inventory.stream()
                .filter(Objects::nonNull)
                .filter(item -> Rune.isRune(item.getId()))
                .forEach(item -> runesAvailable.add(Rune.getRune(item.getId()).get(), item.getQuantity()));

        List<Item> runepouch = getRunesFromRunePouch();

        runepouch.stream()
                .filter(Objects::nonNull)
                .filter(item -> Rune.isRune(item.getId()))
                .forEach(item -> runesAvailable.add(Rune.getRune(item.getId()).get(), item.getQuantity()));

        // All equipment provides unlimited runes for the sake of enchantment spells.
        // These take precedence and may overwrite any previous values.
        equipment.stream()
                .filter(Objects::nonNull)
                .filter(item -> RuneSource.isRuneSource(item.getId()))
                .forEach(item -> runesAvailable.setCount(RuneSource.fromId(item.getId()).getRune(), INFINITY));

        return runesAvailable.entrySet().stream()
                .map((entry) -> new Item(entry.getElement().getItemId(), entry.getCount()))
                .collect(Collectors.toList());
    }

    private static List<Item> getRunesFromRunePouch() {
        return Collections.emptyList();
    }
}
