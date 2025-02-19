package net.botwithus.events.types;

import net.botwithus.events.Event;
import net.botwithus.rs3.inventories.Inventory;
import net.botwithus.rs3.item.InventoryItem;

public record InventoryEvent(InventoryItem oldItem, InventoryItem newItem, Inventory inventory) implements Event {

}
