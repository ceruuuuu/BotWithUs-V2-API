package net.botwithus.rs3.entities;

import net.botwithus.rs3.entities.types.HitmarkType;

public record Hitmark(int id, int value, int cycle) {

    public HitmarkType getType() {
        return HitmarkType.fromId(id);
    }
}
