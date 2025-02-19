package net.botwithus.rs3.client;

import net.botwithus.rs3.login.World;

public record WorldTransition(int oldWorld, int newWorld) implements StateTransition {

    public WorldTransition(int worldId) {
        this(-1, worldId);
    }

    public WorldTransition(World world) {
        this(-1, world.getId());
    }
}
