package net.botwithus.events.types;

import net.botwithus.events.Event;
import net.botwithus.rs3.entities.types.EntityType;
import net.botwithus.rs3.minimenu.Action;

public record InteractionEvent(Action action, int param1, int param2, int param3, int itemId, EntityType type, int entityId, int x, int y, int plane) implements Event {

}
