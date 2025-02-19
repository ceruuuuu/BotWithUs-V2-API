package net.botwithus.rs3.login;

import net.botwithus.rs3.GameState;
import net.botwithus.rs3.client.GameStateTransition;
import net.botwithus.rs3.client.WorldTransition;
import net.botwithus.rs3.client.internal.MutableClient;
import net.botwithus.rs3.login.internal.MutableLoginManager;

import java.util.ArrayList;
import java.util.Collection;

public class Worlds {

    public static Collection<World> getLoaded() {
        return new ArrayList<>(MutableLoginManager.GAME_WORLDS.values());
    }

    public static World getCurrent() {
        return null;
    }

    public static boolean switchTo(int world) {
        World current = getCurrent();
        if (current == null || current.getId() == world) {
            return false;
        }
        MutableClient.STATE_CHANGES.offer(new WorldTransition(current.getId(), world));
        MutableClient.STATE_CHANGES.offer(new GameStateTransition(GameState.LOGGED_IN, GameState.HOPPING));
        return true;
    }

    public static boolean switchTo(World world) {
        return switchTo(world.getId());
    }
}
