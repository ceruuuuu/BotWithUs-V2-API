package net.botwithus.rs3.login;

import net.botwithus.rs3.login.internal.MutableWorld;

public sealed abstract class World permits MutableWorld {

    protected int worldId;
    protected int population;
    protected int properties;
    protected int ping;
    protected String activity;

    public int getId() {
        return worldId;
    }

    public int getPopulation() {
        return population;
    }

    public int getProperties() {
        return properties;
    }

    public int getPing() {
        return ping;
    }

    public String getActivity() {
        return activity;
    }

    public boolean isMembers() {
        return (properties & 0x1) != 0;
    }

    public boolean isFull() {
        return population >= 1980;
    }
}
