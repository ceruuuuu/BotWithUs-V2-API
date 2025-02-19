package net.botwithus.rs3.entities;

import net.botwithus.rs3.entities.types.EntityType;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.Locatable;
import net.botwithus.rs3.world.Rotation;

public abstract class Entity implements Locatable {

    protected EntityType type;
    protected Coordinate coordinate;

    protected Rotation rotation;

    protected boolean isValid;

    protected Entity(EntityType type) {
        this.type = type;
    }

    public EntityType getType() {
        return type;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public int getOrientation() {
        return rotation.angular();
    }

    public boolean isValid() {
        return isValid;
    }
}
