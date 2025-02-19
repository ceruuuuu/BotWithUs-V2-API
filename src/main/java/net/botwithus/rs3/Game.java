package net.botwithus.rs3;

import net.botwithus.rs3.client.GameStateTransition;
import net.botwithus.rs3.client.internal.MutableClient;

public class Game {

    public static int getTickCount() {
        return MutableClient.SERVER_TICK;
    }

    public static int getClientCycle() {
        return MutableClient.CLIENT_CYCLE;
    }

    public static GameState getState() {
        return GameState.fromId(MutableClient.CLIENT_STATE);
    }

    public static boolean logout(boolean lobby) {
        return lobby ? transitionTo(GameState.LOBBY) : transitionTo(GameState.LOGIN_SCREEN);
    }

    public static boolean logout() {
        return logout(false);
    }

    private static boolean transitionTo(GameState state) {
        GameState current = getState();
        return current != state && MutableClient.STATE_CHANGES.offer(new GameStateTransition(current, state));
    }
}
