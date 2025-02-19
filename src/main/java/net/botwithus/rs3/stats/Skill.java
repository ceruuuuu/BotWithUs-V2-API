package net.botwithus.rs3.stats;

import net.botwithus.rs3.stats.internal.MutableStat;

public enum Skill {
    ATTACK,
    DEFENSE,
    STRENGTH,
    CONSTITUTION,
    RANGED,
    PRAYER,
    MAGIC,
    COOKING,
    WOODCUTTING,
    FLETCHING,
    FISHING,
    FIREMAKING,
    CRAFTING,
    SMITHING,
    MINING,
    HERBLORE,
    AGILITY,
    THIEVING,
    SLAYER,
    FARMING,
    RUNECRAFTING,
    HUNTER,
    CONSTRUCTION,
    SUMMONING,
    DUNGEONEERING,
    DIVINATION,
    INVENTION,
    ARCHAEOLOGY,
    NECROMANCY;

    private final MutableStat stat;

    Skill() {
        this.stat = new MutableStat(ordinal());
    }

    public Stat getStat() {
        return stat;
    }

    public int getId() {
        return stat.getId();
    }

    private static final Skill[] values = values();

    public static Skill fromId(int id) {
        for (Skill value : values) {
            if (value.getId() == id) {
                return value;
            }
        }
        return null;
    }
}
