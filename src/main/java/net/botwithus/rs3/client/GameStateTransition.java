package net.botwithus.rs3.client;

import net.botwithus.rs3.Game;
import net.botwithus.rs3.GameState;

public record GameStateTransition(int oldId, int newId) implements StateTransition {

    public GameStateTransition(GameState from, GameState to) {
        this(from.getId(), to.getId());
    }

    public GameStateTransition(GameState state) {
        this(Game.getState(), state);
    }
}
