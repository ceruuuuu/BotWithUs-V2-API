package net.botwithus.rs3.entities;

import net.botwithus.rs3.entities.types.HeadbarType;

public record Headbar(int id, int value) {

    public HeadbarType getType() {
        return HeadbarType.fromId(id);
    }
}
