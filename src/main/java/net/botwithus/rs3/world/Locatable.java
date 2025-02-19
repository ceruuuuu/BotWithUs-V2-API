package net.botwithus.rs3.world;


import net.botwithus.rs3.entities.LocalPlayer;

public interface Locatable {

    /**
     * Returns the position of the entity on the world graph.
     *
     * @return The position of the entity, or null if it cannot be determined or is no longer on the world graph.
     */
    Coordinate getCoordinate();

    /**
     * Gets the area that the entity occupies on the world graph
     *
     * @return The area of the entity, or null if it cannot be determined or is no longer on the world graph.
     */
    default Area getArea() {
        return new Area.Singular(getCoordinate());
    }

    default double distance(Locatable target) {
        return Distance.between(this, target);
    }

    default double distance() {
        return distance(LocalPlayer.self());
    }
}
