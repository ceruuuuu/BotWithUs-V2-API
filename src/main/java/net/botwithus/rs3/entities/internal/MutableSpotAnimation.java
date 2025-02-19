package net.botwithus.rs3.entities.internal;

import net.botwithus.rs3.entities.SpotAnimation;
import net.botwithus.rs3.entities.types.EntityType;
import net.botwithus.rs3.world.Area;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.Direction;

public final class MutableSpotAnimation extends SpotAnimation {
    public MutableSpotAnimation(EntityType type) {
        super(type);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setActive(boolean isActive) {
        this.isValid = isActive;
    }

    public void setAnimationId(int id) {
        this.id = id;
    }

    @Override
    public Area getArea() {
        return new Area.Singular(coordinate);
    }
}
