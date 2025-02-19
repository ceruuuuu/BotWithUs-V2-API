package net.botwithus.rs3.item;

import net.botwithus.rs3.cache.assets.ConfigManager;
import net.botwithus.rs3.cache.assets.items.ItemDefinition;
import net.botwithus.rs3.cache.assets.items.StackType;
import net.botwithus.rs3.interfaces.Component;
import net.botwithus.rs3.interfaces.InterfaceAddress;
import net.botwithus.rs3.interfaces.InterfaceManager;
import net.botwithus.rs3.item.internal.MutableItem;
import net.botwithus.rs3.minimenu.Interactable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

public sealed abstract class Item implements Interactable permits InventoryItem, MutableItem {

    private static final Logger log = Logger.getLogger(Item.class.getName());

    private final InterfaceAddress address;

    protected int id;
    protected int quantity;

    private Component component;

    public Item(int id, int amount, InterfaceAddress address) {
        this.id = id;
        this.quantity = amount;
        this.address = address;
    }

    public Item(int id, int amount) {
        this(id, amount, InterfaceAddress.INVALID);
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public ItemDefinition getDefinition() {
        return id == -1 ? null : ConfigManager.getItemProvider().provide(id);
    }

    public int getCategory() {
        ItemDefinition definition = getDefinition();
        return definition == null ? -1 : definition.getCategory();
    }

    public String getName() {
        ItemDefinition definition = getDefinition();
        return definition == null ? "" : definition.getName();
    }

    public StackType getStackType() {
        ItemDefinition definition = getDefinition();
        return definition == null ? StackType.NEVER : definition.getStackability();
    }

    public Component getComponent() {
        if (component == null) {
            // Lazy-load to prevent the extra overhead from interfaces by default
            component = InterfaceManager.getComponent(address);
        }
        return component;
    }

    @Override
    public List<String> getOptions() {
        Component component = getComponent();
        return component == null ? Optional.ofNullable(getDefinition())
                .map(ItemDefinition::getBackpackOptions)
                .orElse(Collections.emptyList()) : component.getOptions();
    }

    @Override
    public boolean interact(Predicate<String> predicate) {
        Component component = getComponent();
        if (component == null) {
            throw new UnsupportedOperationException("Interacting with this item is not supported");
        }
        return component.interact(predicate);
    }
}