package net.runelite.client.plugins.hallowedsepulchre;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.runelite.api.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.*;

import static net.runelite.client.plugins.hallowedsepulchre.SpellUtil.Rune;
import static net.runelite.client.plugins.hallowedsepulchre.SpellUtil.RuneSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MagicUtilTest {

    private static final Integer UNLIMITED = Integer.MAX_VALUE;

    /**
     * Show that runes are counted correctly from the player's inventory.
     */
    @Test
    public void getRunesAvailable_inventory() {
        List<Item> inventory = ImmutableList.of(
                new Item(Rune.AIR.getId(), 1),
                new Item(Rune.WATER.getId(), 3),
                new Item(Rune.LAVA.getId(), 10),
                new Item(Rune.LAW.getId(), 3),
                new Item(Rune.CHAOS.getId(), 6)
        );

        Map<SpellUtil.Rune, Integer> expected = ImmutableMap.of(
                Rune.AIR, 1,
                Rune.WATER, 3,
                Rune.LAVA, 10,
                Rune.LAW, 3,
                Rune.CHAOS, 6
        );

        final Map<Rune, Integer> result = SpellUtil.getRunesAvailable(inventory, Collections.emptyList());

        assertEquals(expected, result);
    }


    /**
     * Show that runes are counted correctly from the player's inventory with equipment.
     */
    @Test
    public void getRunesAvailable_withEquipment() {
        final List<Item> inventory = ImmutableList.of(
                new Item(Rune.AIR.getId(), 1),
                new Item(Rune.MUD.getId(), 10),
                new Item(Rune.WATER.getId(), 3),
                new Item(Rune.CHAOS.getId(), 6)
        );

        final List<Item> equipment = ImmutableList.of(
                new Item(RuneSource.MUD_BATTLESTAFF.getId(), 1),
                new Item(RuneSource.TOME_OF_FIRE.getId(), 1)
        );

        final Map<SpellUtil.Rune, Integer> expected = ImmutableMap.of(
                Rune.AIR, 1,
                Rune.MUD, UNLIMITED, // Unlimited mud runes from mud battle staff.
                Rune.WATER, 3,
                Rune.CHAOS, 6,
                Rune.FIRE, UNLIMITED // Unlimited fire runes from tome of fire.
        );

        final Map<Rune, Integer> result = SpellUtil.getRunesAvailable(inventory, equipment);

        assertEquals(expected, result);
    }

    /**
     * Show that every piece of equipment provides an unlimited source of runes.
     */
    @ParameterizedTest
    @EnumSource(RuneSource.class)
    public void equipment(RuneSource equipment) {
        final Set<Item> equipped = Collections.singleton(new Item(equipment.getId(), 1));

        final Map<Rune, Integer> result = SpellUtil.getRunesAvailable(Collections.emptySet(), equipped);

        assertEquals(UNLIMITED, result.getOrDefault(equipment.getRune(), 0));
    }

    /**
     * Show that a combination rune and combination staves map to the same elemental rune.
     *
     * For example, any lava rune/ lava staff should
     */
    @Test
    public void comboRune_lavaRune() {
        final List<Item> inventory = ImmutableList.of(
                new Item(Rune.LAVA.getId(), 1)
        );

        // The player can't actually have all 4 staffs equipped at once,
        // but this shows that combo rune staff maps to the correct combo rune.
        final List<Item> equipment = ImmutableList.of(
                new Item(RuneSource.LAVA_BATTLESTAFF.getId(), 1),
                new Item(RuneSource.LAVA_BATTLESTAFF_OR.getId(), 1),
                new Item(RuneSource.MYSTIC_LAVA_STAFF.getId(), 1),
                new Item(RuneSource.MYSTIC_LAVA_STAFF_OR.getId(), 1)
        );

        final Map<Rune, Integer> runes = SpellUtil.getRunesAvailable(inventory, equipment);

        // Only the lava rune should appear for lava runes and lava staffs.
        final Map<Rune, Integer> expected = ImmutableMap.of(Rune.LAVA, UNLIMITED);

        assertEquals(expected, runes);
    }

    /**
     * Show that steam runes and staffs map to the steam elemental rune.
     */
    @Test
    public void comboRune_steamRune() {
        final List<Item> inventory = ImmutableList.of(
                new Item(Rune.STEAM.getId(), 1)
        );

        final List<Item> equipment = ImmutableList.of(
                new Item(RuneSource.STEAM_BATTLESTAFF.getId(), 1),
                new Item(RuneSource.STEAM_BATTLESTAFF_OR.getId(), 1),
                new Item(RuneSource.MYSTIC_STEAM_STAFF.getId(), 1),
                new Item(RuneSource.MYSTIC_STEAM_STAFF_OR.getId(), 1)
        );

        final Map<Rune, Integer> runes = SpellUtil.getRunesAvailable(inventory, equipment);
        final Map<Rune, Integer> expected = ImmutableMap.of(Rune.STEAM, UNLIMITED);

        assertEquals(expected, runes);
    }

    /**
     * Show that mud runes and staffs map to the steam elemental rune.
     */
    @Test
    public void comboRune_mudRune() {
        final List<Item> inventory = ImmutableList.of(
                new Item(Rune.MUD.getId(), 1)
        );

        final List<Item> equipment = ImmutableList.of(
                new Item(RuneSource.MUD_BATTLESTAFF.getId(), 1),
                new Item(RuneSource.MYSTIC_MUD_STAFF.getId(), 1)
        );

        final Map<Rune, Integer> runes = SpellUtil.getRunesAvailable(inventory, equipment);
        final Map<Rune, Integer> expected = ImmutableMap.of(Rune.MUD, UNLIMITED);

        assertEquals(expected, runes);
    }

    /**
     * Show that mist runes and staffs map to the steam elemental rune.
     */
    @Test
    public void comboRune_mistRune() {
        final List<Item> inventory = ImmutableList.of(
                new Item(Rune.MIST.getId(), 1)
        );

        final List<Item> equipment = ImmutableList.of(
                new Item(RuneSource.MIST_BATTLESTAFF.getId(), 1),
                new Item(RuneSource.MYSTIC_MIST_STAFF.getId(), 1)
        );

        final Map<Rune, Integer> runes = SpellUtil.getRunesAvailable(inventory, equipment);
        final Map<Rune, Integer> expected = ImmutableMap.of(Rune.MIST, UNLIMITED);

        assertEquals(expected, runes);
    }


    /**
     * Show that dust runes and staffs map to the steam elemental rune.
     */
    @Test
    public void comboRune_dustRune() {
        final List<Item> inventory = ImmutableList.of(
                new Item(Rune.DUST.getId(), 1)
        );

        final List<Item> equipment = ImmutableList.of(
                new Item(RuneSource.DUST_BATTLESTAFF.getId(), 1),
                new Item(RuneSource.MYSTIC_DUST_STAFF.getId(), 1)
        );

        final Map<Rune, Integer> runes = SpellUtil.getRunesAvailable(inventory, equipment);
        final Map<Rune, Integer> expected = ImmutableMap.of(Rune.DUST, UNLIMITED);

        assertEquals(expected, runes);
    }

    /**
     * Show that smoke runes and staffs map to the steam elemental rune.
     */
    @Test
    public void comboRune_smokeRune() {
        final List<Item> inventory = ImmutableList.of(
                new Item(Rune.SMOKE.getId(), 1)
        );

        final List<Item> equipment = ImmutableList.of(
                new Item(RuneSource.SMOKE_BATTLESTAFF.getId(), 1),
                new Item(RuneSource.MYSTIC_SMOKE_STAFF.getId(), 1)
        );

        final Map<Rune, Integer> runes = SpellUtil.getRunesAvailable(inventory, equipment);
        final Map<Rune, Integer> expected = ImmutableMap.of(Rune.SMOKE, UNLIMITED);

        assertEquals(expected, runes);
    }

    /**
     * Show that combination runes are split into their elemental rune counterparts.
     */
    @ParameterizedTest
    @CsvSource({
            "LAVA, EARTH, FIRE",
            "STEAM, WATER, FIRE",
            "MUD, WATER, EARTH",
            "MIST, AIR, WATER",
            "DUST, AIR, EARTH",
            "SMOKE, AIR, FIRE"
    })
    public void splitComboRunes(Rune combinationRune, Rune elementalRune1, Rune elementalRune2) {
        // Need a mutable map since splitComboRunes modifies it.
        final Map<Rune, Integer> runes = new HashMap<>();
        runes.put(combinationRune, 1);

        // The one combination rune should be split into two elemental runes.
        final Map<Rune, Integer> expected = ImmutableMap.of(
                elementalRune1, 1,
                elementalRune2, 1
        );

        SpellUtil.splitComboRunes(runes);

        assertEquals(expected, runes);
    }

    /**
     * Show that combo runes are split into their elemental rune counterparts.
     */
    @Test
    public void splitComboRunes_withExisting() {
        final Map<Rune, Integer> runes = new HashMap<>();

        // Splitting these will result in 3 of each elemental rune.
        runes.put(Rune.LAVA, 1); // 1 Earth, 1 Fire
        runes.put(Rune.STEAM, 1);  // 1 Water, 1 Fire
        runes.put(Rune.MUD, 1);  // 1 Water, 1 Earth
        runes.put(Rune.MIST, 1); // 1 Air, 1 Water
        runes.put(Rune.DUST, 1);  // 1 Air, 1 Earth
        runes.put(Rune.SMOKE, 1); // 1 Air, 1 Fire

        // Additional elemental runes.
        runes.put(Rune.AIR, 0);
        runes.put(Rune.WATER, 1);
        runes.put(Rune.EARTH, 2);
        runes.put(Rune.FIRE, 3);

        final Map<Rune, Integer> expected = ImmutableMap.of(
                Rune.AIR, 3 + 0, // 3 from the combo runes + zero additional air.
                Rune.WATER, 3 + 1,
                Rune.EARTH, 3 + 2,
                Rune.FIRE, 3 + 3
        );

        SpellUtil.splitComboRunes(runes);

        assertEquals(expected, runes);
    }

    @Test
    public void lvl1Enchant_canBeCasted_true() {
        final Map<Rune, Integer> runes = ImmutableMap.of(
                Rune.COSMIC, 1,
                Rune.WATER, 1
        );

        assertTrue(SpellUtil.EnchantmentSpell.LVL_1_ENCHANT.canBeCasted(99, runes));
    }

    @Test
    public void lvl1Enchant_canBeCasted_missingRunes() {
        final Map<Rune, Integer> runes = ImmutableMap.of(
                Rune.AIR, 1, // Wrong rune.
                Rune.WATER, 1
        );

        assertFalse(SpellUtil.EnchantmentSpell.LVL_1_ENCHANT.canBeCasted(99, runes));
    }

    @Test
    public void lvl1Enchant_canBeCasted_skillLevelTooLow() {
        final Map<Rune, Integer> runes = ImmutableMap.of(
                Rune.COSMIC, 1,
                Rune.WATER, 1
        );

        assertFalse(SpellUtil.EnchantmentSpell.LVL_1_ENCHANT.canBeCasted(1, runes));
    }
}
