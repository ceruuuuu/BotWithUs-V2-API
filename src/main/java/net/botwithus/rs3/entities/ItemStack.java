package net.botwithus.rs3.entities;

import net.botwithus.rs3.entities.internal.MutableItemStack;
import net.botwithus.rs3.entities.types.EntityType;
import net.botwithus.rs3.item.Item;
import net.botwithus.rs3.item.internal.MutableItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract sealed class ItemStack extends Entity implements Iterable<Item> permits MutableItemStack {

    protected List<MutableItem> items;

    protected ItemStack(EntityType type) {
        super(type);
        this.items = new ArrayList<>();
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public Iterator<Item> iterator() {
        return new ArrayList<Item>(this.items).iterator();
    }
}
