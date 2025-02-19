package net.botwithus.rs3.world;

import net.botwithus.rs3.entities.LocalPlayer;

import java.util.function.BiFunction;

public enum Distance {

    EUCLIDIAN(Distance::euclidean),
    MANHATTAN(Distance::manhattan),
    CHEBYSHEV(Distance::chebyshev);

    private final BiFunction<Coordinate, Coordinate, Double> algorithm;

    Distance(BiFunction<Coordinate, Coordinate, Double> algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Gets the distance between two Locatable entities that exist on the world graph.
     * If either Locatable is not on the world graph or they're on different Z levels, the method returns Double.NaN.
     *
     * @param from      a Locatable on the world graph to act as the source.
     * @param to        a Locatable on the world graph as the destination.
     * @param metric an optional algorithm to use (defaults to Distance#EUCLIDIAN)
     * @return The distance between the two Locatables, or Double.NaN if either Locatable is not on the world graph or they're on different Z levels.
     */
    public static double between(Locatable from, Locatable to, Distance metric) {
        if (from == null || to == null) {
            return Double.NaN;
        }
        Coordinate pos1 = from.getCoordinate();
        Coordinate pos2 = to.getCoordinate();
        if (pos1.z() != pos2.z()) {
            return Double.NaN;
        }
        return metric.algorithm.apply(pos1, pos2);
    }

    public static double between(Locatable from, Locatable to) {
        return between(from, to, Distance.EUCLIDIAN);
    }

    public static double to(Locatable to, Distance metric) {
        return between(LocalPlayer.self(), to, metric);
    }

    public static double to(Locatable to) {
        return between(LocalPlayer.self(), to, Distance.EUCLIDIAN);
    }

    private static double euclidean(Coordinate pos1, Coordinate pos2) {
        return Math.hypot(pos2.x() - pos1.x(), pos2.y() - pos1.y());
    }

    private static double euclideanSquared(Coordinate pos1, Coordinate pos2) {
        return Math.sqrt(Math.pow(pos1.x() - pos2.x(), 2) + Math.pow(pos1.y() - pos2.y(), 2));
    }

    private static double manhattan(Coordinate pos1, Coordinate pos2) {
        return Math.abs(pos1.x() - pos2.x()) + Math.abs(pos1.y() - pos2.y());
    }

    private static double chebyshev(Coordinate pos1, Coordinate pos2) {
        return Math.max(Math.abs(pos1.x() - pos2.x()), Math.abs(pos1.y() - pos2.y()));
    }
}