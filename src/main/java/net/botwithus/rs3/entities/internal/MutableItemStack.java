package net.botwithus.rs3.entities.internal;

import net.botwithus.rs3.cache.assets.items.StackType;
import net.botwithus.rs3.entities.ItemStack;
import net.botwithus.rs3.entities.types.EntityType;
import net.botwithus.rs3.item.Item;
import net.botwithus.rs3.item.internal.MutableItem;
import net.botwithus.rs3.world.Area;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.Direction;

public final class MutableItemStack extends ItemStack {
    public MutableItemStack(EntityType type) {
        super(type);
    }

    public void addItem(Item item) {
        for (MutableItem i : items) {
            if (i.getId() == item.getId() && i.getStackType() == StackType.ALWAYS) {
                i.setAmount(i.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(new MutableItem(item.getId(), item.getQuantity()));
    }

    public void clear() {
        items.clear();
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setActive(boolean active) {
        this.isValid = active;
    }

    @Override
    public Area getArea() {
        return new Area.Singular(coordinate);
    }
}
