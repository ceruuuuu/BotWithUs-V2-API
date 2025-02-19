package net.botwithus.rs3.entities.internal;

import net.botwithus.rs3.entities.Projectile;
import net.botwithus.rs3.entities.types.EntityType;
import net.botwithus.rs3.world.Area;
import net.botwithus.rs3.world.Coordinate;

public final class MutableProjectile extends Projectile {
    public MutableProjectile(EntityType type) {
        super(type);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSourceIndex(int sourceIndex) {
        this.sourceIndex = sourceIndex;
    }

    public void setSourceType(EntityType sourceType) {
        this.sourceType = sourceType;
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    public void setTargetType(EntityType targetType) {
        this.targetType = targetType;
    }

    public void setStartCycle(int startCycle) {
        this.startCycle = startCycle;
    }

    public void setEndCycle(int endCycle) {
        this.endCycle = endCycle;
    }

    public void setEnd(Coordinate end) {
        this.end = end;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setActive(boolean active) {
        this.isValid = active;
    }

    @Override
    public Area getArea() {
        return new Area.Singular(coordinate);
    }
}
