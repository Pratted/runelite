package net.runelite.client.plugins.hallowedsepulchre;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;
import lombok.Getter;
import net.runelite.api.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static net.runelite.api.ItemID.*;
import static net.runelite.api.ItemID.SMOKE_RUNE;
import static net.runelite.api.ItemID.WRATH_RUNE;

public class SpellUtil {

    public enum Rune {
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

        private Optional<Pair<Rune, Rune>> elementalRunes;

        Rune(int id, int itemId) {
            this.id = id;
            this.itemId = itemId;
            elementalRunes = Optional.empty();
        }

        Rune(int id, int itemId, Rune elementalRune1, Rune elementalRune2) {
            this.id = id;
            this.itemId = itemId;
            this.elementalRunes = Optional.of(Pair.of(elementalRune1, elementalRune2));
        }

        public static Optional<Rune> getRune(int id) {
            for (Rune rune : values()) {
                if (rune.id == id) {
                    return Optional.of(rune);
                }
            }

            return Optional.empty();
        }

        public Pair<Rune, Rune> getComposition() {
            checkState(elementalRunes.isPresent(), this + " is not a combination rune so it does not have any elemental runes.");

            return elementalRunes.get();
        }

        public boolean isComboRune() {
            return elementalRunes.isPresent();
        }

        public static boolean isComboRune(int id) {
            return getRune(id)
                    .map(Rune::isComboRune)
                    .orElse(false);
        }

        public static boolean isRune(int id) {
            return getRune(id).isPresent();
        }


        public static Rune fromId(int id) {
            return getRune(id).orElseThrow(() -> new IllegalArgumentException(id + " is not a Rune id"));
        }
    }


    public enum RuneSource {
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

        private static Optional<RuneSource> getEquipment(int id) {
            for (RuneSource equipment : values()) {
                if (equipment.id == id) {
                    return Optional.of(equipment);
                }
            }

            return Optional.empty();
        }

        public static boolean isEquipment(int id) {
            return getEquipment(id).isPresent();
        }

        public static RuneSource fromId(int id) {
            return getEquipment(id).orElseThrow(() -> new IllegalArgumentException(id + " is not a valid Equipment id"));
        }
    }

    public static Map<Rune, Integer> getRunesAvailable(Client client) {

        final List<Item> inventory = Optional.ofNullable(client.getItemContainer(InventoryID.INVENTORY))
                .map(items -> Arrays.asList(items.getItems()))
                .orElse(Collections.emptyList());

        final List<Item> equipment = Optional.ofNullable(client.getItemContainer(InventoryID.EQUIPMENT))
                .map(items -> Arrays.asList(items.getItems()))
                .orElse(Collections.emptyList());

        return getRunesAvailable(inventory, equipment);
    }

    public static Map<Rune, Integer> getRunesAvailable(Collection<Item> inventory, Collection<Item> equipment) {
        final Map<Rune, Integer> runesAvailable = new HashMap<>();

        inventory.stream()
                .filter(Objects::nonNull)
                .filter(item -> Rune.isRune(item.getId()))
                .forEach(item -> runesAvailable.put(Rune.fromId(item.getId()), item.getQuantity()));

        List<Item> runepouch = getRunesFromRunePouch();

        runepouch.stream()
                .filter(Objects::nonNull)
                .filter(item -> Rune.isRune(item.getId()))
                .forEach(item -> {
                    final Rune rune = Rune.fromId(item.getId());
                    final int prev = runesAvailable.getOrDefault(rune, 0);

                    runesAvailable.put(rune, prev + item.getQuantity());
                });

        // All equipment provides unlimited runes for the sake of enchantment spells.
        // These take precedence and may overwrite any previous values.
        equipment.stream()
                .filter(Objects::nonNull)
                .filter(item -> RuneSource.isEquipment(item.getId()))
                .forEach(item -> runesAvailable.put(RuneSource.fromId(item.getId()).getRune(), Integer.MAX_VALUE));

        return runesAvailable;
    }

    public static Multiset<Rune> getRunesAvailable2(Collection<Item> inventory, Collection<Item> equipment) {
        final Multiset<Rune> runesAvailable = HashMultiset.create();

        inventory.stream()
                .filter(Objects::nonNull)
                .filter(item -> Rune.isRune(item.getId()))
                .forEach(item -> runesAvailable.add(Rune.fromId(item.getId()), item.getQuantity()));

        List<Item> runepouch = getRunesFromRunePouch();

        runepouch.stream()
                .filter(Objects::nonNull)
                .filter(item -> Rune.isRune(item.getId()))
                .forEach(item -> runesAvailable.add(Rune.fromId(item.getId()), item.getQuantity()));

        // All equipment provides unlimited runes for the sake of enchantment spells.
        // These take precedence and may overwrite any previous values.
        equipment.stream()
                .filter(Objects::nonNull)
                .filter(item -> RuneSource.isEquipment(item.getId()))
                .forEach(item -> runesAvailable.setCount(RuneSource.fromId(item.getId()).getRune(), Integer.MAX_VALUE));

        return runesAvailable;
    }

    private static List<Item> getRunesFromRunePouch() {
        return Collections.emptyList();
    }

    public static void splitComboRunes(final Map<Rune, Integer> runes) {
        // Grab all of the combo runes
        final Set<Map.Entry<Rune, Integer>> comboRunes = runes.entrySet().stream()
                .filter(entry -> Rune.isComboRune(entry.getKey().getId()))
                .collect(Collectors.toSet());

        // Create the two elemental runes for this combo rune and update their quantities.
        for (final Map.Entry<Rune, Integer> entry : comboRunes) {
            final Rune comboRune = entry.getKey();
            final int qty = entry.getValue();

            // Split the combo runes into their elemental runes
            // (e.g 4 Lava Runes = 4 Earth Runes and 4 Fire Runes
            final Rune elementalRune1 = comboRune.getComposition().getLeft();
            final Rune elementalRune2 = comboRune.getComposition().getRight();

            // The elemental rune may already exist, so we may have to add the quantities.
            final int qty1 = runes.getOrDefault(elementalRune1, 0);
            final int qty2 = runes.getOrDefault(elementalRune2,0);

            runes.put(elementalRune1, qty + qty1);
            runes.put(elementalRune2, qty + qty2);
        }

        // Remove the combo runes from the collection to avoid double counting.
        comboRunes.stream()
                .map(Map.Entry::getKey)
                .forEach(runes::remove);
    }

    public static void splitComboRunes2(final Multiset<Rune> runes) {
        runes.entrySet().stream()
                .filter(rune -> Rune.isComboRune(rune.getElement().getId()))
                .forEach(entry -> {
                    final Rune comboRune = entry.getElement();

                    // Split the combo runes into their elemental runes
                    // (e.g 4 Lava Runes = 4 Earth Runes and 4 Fire Runes
                    final Rune elementalRune1 = comboRune.getComposition().getLeft();
                    final Rune elementalRune2 = comboRune.getComposition().getRight();

                    runes.setCount(elementalRune1, Integer.MAX_VALUE);
                    runes.setCount(elementalRune2, Integer.MAX_VALUE);
                });

        // Remove the combo runes from the collection to avoid double counting.
        runes.removeIf(Rune::isComboRune);
    }

    public static boolean canCastAnyEnchantmentSpell(final Client client) {
        // Get all of the runes this player currently has access to too.
        final Map<Rune, Integer> runesAvailable = getRunesAvailable(client);

        // Transform combo runes into elemental runes for the sake of the algorithm.
        // This mutates the collection.
        splitComboRunes(runesAvailable);

        return canCastAnyEnchantmentSpell(client.getBoostedSkillLevel(Skill.MAGIC), runesAvailable);
    }

    public static boolean canCastAnyEnchantmentSpell(int magicLevel, Map<Rune, Integer> runesAvailable) {
        final Map<Rune, Integer> copy = new HashMap<>(runesAvailable);

        // Split combination runes into elemental runes because the spells are derived from elemental runes.
        // This mutates the collection.
        splitComboRunes(copy);

        return Arrays.stream(EnchantmentSpell.values())
                .anyMatch(spell -> spell.canBeCasted(magicLevel, copy));
    }

    public enum EnchantmentSpell {
        LVL_1_ENCHANT(7, ImmutableMap.of(
                Rune.COSMIC, 1,
                Rune.WATER, 1)),
        LVL_2_ENCHANT(27, ImmutableMap.of(
                Rune.COSMIC, 1,
                Rune.AIR, 3)),
        LVL_3_ENCHANT(49, ImmutableMap.of(
                Rune.COSMIC, 1,
                Rune.FIRE, 5)),
        LVL_4_ENCHANT(57, ImmutableMap.of(
                Rune.COSMIC, 1,
                Rune.EARTH, 10)),
        LVL_5_ENCHANT(68, ImmutableMap.of(
                Rune.COSMIC, 1,
                Rune.EARTH, 10,
                Rune.WATER, 10)),
        LVL_6_ENCHANT(87, ImmutableMap.of(
                Rune.COSMIC, 1,
                Rune.EARTH, 20,
                Rune.FIRE, 20)),
        LVL_7_ENCHANT(93, ImmutableMap.of(
                Rune.COSMIC, 1,
                Rune.BLOOD, 20,
                Rune.SOUL, 20));

        @Getter
        int magicLevel;

        @Getter
        Map<Rune, Integer> runes;

        EnchantmentSpell(int magicLevel, Map<Rune, Integer> runes) {
            this.magicLevel = magicLevel;
            this.runes = runes;
        }

        public boolean canBeCasted(int magicLevel, Map<Rune, Integer> runesAvailable) {

            for (Map.Entry<Rune, Integer> entry : runes.entrySet()) {
                final Rune rune = entry.getKey();
                int needed = entry.getValue();
                int available = runesAvailable.getOrDefault(rune, 0);

                if (available < needed) {
                    return false;
                }
            }

            return magicLevel >= this.magicLevel;
        }
    }
}