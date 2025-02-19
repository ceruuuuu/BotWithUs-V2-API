package net.botwithus.rs3.inventories.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InventoryHandler {
    public static final Map<Integer, MutableInventory> INVENTORIES = new HashMap<>();
    public static final Set<Integer> UPDATING_INVENTORIES = new HashSet<>();

    static {
        INVENTORIES.put(93, new MutableInventory(93));
        INVENTORIES.put(94, new MutableInventory(94));
        INVENTORIES.put(95, new MutableInventory(95));
    }

    public static MutableInventory getOrCreate(int invId) {
        MutableInventory inventory = INVENTORIES.get(invId);
        if (inventory == null) {
            inventory = new MutableInventory(invId);
            inventory.setActive(true);
            INVENTORIES.put(invId, inventory);
        }
        return inventory;
    }

}
