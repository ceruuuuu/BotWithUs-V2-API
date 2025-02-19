package net.botwithus.rs3.stats;

import net.botwithus.rs3.stats.internal.MutableStat;

public sealed abstract class Stat permits MutableStat {

    protected int id;
    protected int level;
    protected int xp;

    protected int maxLevel;
    protected int currentLevel;

    protected boolean isElite;

    protected boolean isMembers;

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return xp;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}
