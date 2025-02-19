package net.botwithus.rs3.entities;

import net.botwithus.rs3.entities.internal.MutableProjectile;
import net.botwithus.rs3.entities.types.EntityType;
import net.botwithus.rs3.world.Coordinate;

public abstract sealed class Projectile extends Entity permits MutableProjectile {
    protected Coordinate end;

    protected int id;

    protected int sourceIndex;
    protected EntityType sourceType;

    protected int targetIndex;
    protected EntityType targetType;

    protected int startCycle;
    protected int endCycle;

    protected Projectile(EntityType type) {
        super(type);
    }

    public Coordinate getStart() {
        return super.getCoordinate();
    }

    public Coordinate getEnd() {
        return end;
    }

    public int getId() {
        return id;
    }

    public int getSourceIndex() {
        return sourceIndex;
    }

    public EntityType getSourceType() {
        return sourceType;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public EntityType getTargetType() {
        return targetType;
    }

    public int getStartCycle() {
        return startCycle;
    }

    public int getEndCycle() {
        return endCycle;
    }
}
