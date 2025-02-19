package net.botwithus.rs3.entities.internal;

import net.botwithus.rs3.entities.SceneObject;
import net.botwithus.rs3.entities.types.EntityType;
import net.botwithus.rs3.world.Area;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.Direction;

public final class MutableSceneObject extends SceneObject {
    public MutableSceneObject(EntityType type) {
        super(type);
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public void setAnimationId(int animationId) {
        this.animationId = animationId;
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

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public Area getArea() {
        return new Area.Singular(coordinate);
    }
}
