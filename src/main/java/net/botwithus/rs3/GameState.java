package net.botwithus.rs3;

import net.botwithus.rs3.client.internal.MutableClient;

public enum GameState {
    STARTING(0),
    LOGIN_SCREEN(10),
    LOBBY(20),
    LOGGED_IN(30),
    HOPPING(37),
    UNKNOWN(-1);

    private final int id;

    GameState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        int resolvedId = id == -1 ? MutableClient.CLIENT_STATE : id;
        return "GameState[id=" + resolvedId + ", name=" + name() + "]";
    }

    private static final GameState[] values = values();

    public static GameState fromId(int id) {
        for (GameState state : values) {
            if (state.getId() == id) {
                return state;
            }
        }
        return GameState.UNKNOWN;
    }
}
