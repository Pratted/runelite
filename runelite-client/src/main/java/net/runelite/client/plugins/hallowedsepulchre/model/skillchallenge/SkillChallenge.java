package net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge;

import static net.runelite.api.ItemID.HALLOWED_GRAPPLE;
import static net.runelite.api.ItemID.MITH_GRAPPLE;
import static net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2.Requirements.*;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.requirement2.Requirement;
import net.runelite.client.plugins.hallowedsepulchre.model.skillchallenge.magic.Spell;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public enum SkillChallenge {

    GRAPPLE(
            skill(Skill.RANGED, 62),
            allOf(
                    anyOf(equipment(MITH_GRAPPLE), equipment(HALLOWED_GRAPPLE)),
                    anyOf(
                            equipment(ItemID.CROSSBOW),
                            equipment(ItemID.ADAMANT_CROSSBOW),
                            equipment(ItemID.ARMADYL_CROSSBOW_23611),
                            equipment(ItemID.ARMADYL_CROSSBOW),
                            equipment(ItemID.BLURITE_CROSSBOW),
                            equipment(ItemID.BRONZE_CROSSBOW),
                            equipment(ItemID.DORGESHUUN_CROSSBOW),
                            equipment(ItemID.DRAGON_CROSSBOW),
                            equipment(ItemID.DRAGON_HUNTER_CROSSBOW),
                            equipment(ItemID.HUNTERS_CROSSBOW),
                            equipment(ItemID.IRON_CROSSBOW),
                            equipment(ItemID.KARILS_CROSSBOW),
                            equipment(ItemID.KARILS_CROSSBOW_0),
                            equipment(ItemID.KARILS_CROSSBOW_25),
                            equipment(ItemID.KARILS_CROSSBOW_50),
                            equipment(ItemID.KARILS_CROSSBOW_75),
                            equipment(ItemID.KARILS_CROSSBOW_100),
                            equipment(ItemID.PHOENIX_CROSSBOW),
                            equipment(ItemID.RUNE_CROSSBOW),
                            equipment(ItemID.RUNE_CROSSBOW_23601),
                            equipment(ItemID.STEEL_CROSSBOW))
            )
    ),
    PRAYER (
            skill(Skill.PRAYER, 54),
            anyOf(
                    // 1 Vampyre Dust and Hallowed symbol
                    allOf(item(ItemID.VAMPYRE_DUST), equipment(ItemID.HALLOWED_SYMBOL)),
                    // Or two vampyre dust.
                    allOf(xOfItem(ItemID.VAMPYRE_DUST, 2))
            )
    ),
    CONSTRUCTION (
            skill(Skill.PRAYER, 56),
            allOf(
                    item(ItemID.SAW),
                    anyOf(item(ItemID.HAMMER), item(ItemID.HALLOWED_HAMMER)),
                    anyOf(
                            allOf(xOfItem(ItemID.PLANK, 2), xOfItem(ItemID.STEEL_NAILS, 5)),
                            allOf(xOfItem(ItemID.OAK_PLANK, 2), xOfItem(ItemID.MITHRIL_NAILS, 5)),
                            allOf(xOfItem(ItemID.TEAK_PLANK, 2), xOfItem(ItemID.ADAMANTITE_NAILS, 5)),
                            allOf(xOfItem(ItemID.MAHOGANY_PLANK, 2), xOfItem(ItemID.RUNE_NAILS, 5))
                    )
            )
    ),
    MAGIC (
            // The skill levels are defined in each spell because each spell requires a different level.
            anyOf (
                    spell(Spell.LVL_1_ENCHANT), spell(Spell.LVL_2_ENCHANT), spell(Spell.LVL_3_ENCHANT),
                    spell(Spell.LVL_4_ENCHANT), spell(Spell.LVL_5_ENCHANT), spell(Spell.LVL_6_ENCHANT),
                    spell(Spell.LVL_7_ENCHANT)
            )
    );

    private Set<Requirement> requirements;

    @Getter
    private Set<TileObject> obstacles;

    @Setter
    @Getter
    private boolean isCompleted;

    SkillChallenge(Requirement... requirements) {
        this.requirements = Arrays.stream(requirements).collect(Collectors.toSet());
        this.obstacles = new HashSet<>();
        this.isCompleted = false;
    }

    public boolean canComplete(Client client) {
        return requirements.stream()
                .allMatch(req -> req.isFulfilledBy(client));
    }

    public void addTileObject(TileObject tileObject) {
        obstacles.add(tileObject);
    }


    public Requirement<?> getRequirement() {
        return null;
    }
}
