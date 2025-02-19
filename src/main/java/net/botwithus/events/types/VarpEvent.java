package net.botwithus.events.types;

import net.botwithus.events.Event;

public record VarpEvent(int id, int oldValue, int newValue, boolean isVarbit) implements Event {

    @Override
    public String toString() {
        return "VarpEvent[id=" + id + ",old=" + oldValue + ",new=" + newValue + "]";
    }
}
