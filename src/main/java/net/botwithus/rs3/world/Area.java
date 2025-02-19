package net.botwithus.rs3.world;

import net.botwithus.util.Random;

import java.util.*;

/**
 * An Area represents a group of Coordinates in the world graph.
 */
public abstract sealed class Area implements Locatable permits Area.Singular, Area.Rectangular, Area.Circular, Area.Polygonal {
    @Override
    public Area getArea() {
        return this;
    }

    public final Set<Coordinate> getOverlap(Area area) {
        return this.getOverlap(area, false);
    }

    /**
     * Gets a set of the overlapping coordinates between this area and the provided area.
     *
     * @param area        The area to compare with for overlap. If it's null or it has no coordinates, an empty set is returned.
     * @param ignorePlane If true, only X and Y coordinates are considered for overlap. If false, the Z (plane) coordinate is also considered.
     * @return A set of overlapping coordinates. If there are no overlapping coordinates, an empty set is returned.
     */
    public Set<Coordinate> getOverlap(Area area, boolean ignorePlane) {
        if (area == null) {
            return Collections.emptySet();
        }
        Set<Coordinate> otherCoordinates = new HashSet<>(area.getCoordinates());
        if (otherCoordinates.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Coordinate> overlapping = new HashSet<>();
        for (Coordinate coordinate1 : this.getCoordinates()) {
            for (Coordinate coordinate2 : otherCoordinates) {
                if (coordinate1.x() == coordinate2.x() && coordinate1.y() == coordinate2.y() && (ignorePlane || coordinate1.z() == coordinate2.z())) {
                    overlapping.add(coordinate1);
                    break;
                }
            }
        }
        return overlapping;
    }

    public final boolean overlaps(Locatable locatable) {
        if (locatable == null) {
            return false;
        }
        return this.overlaps(locatable.getArea(), false);
    }

    public boolean overlaps(Area area, boolean ignorePlane) {
        if (area == null) {
            return false;
        }
        List<Coordinate> currentCoordinates = getCoordinates();
        if (currentCoordinates.isEmpty()) {
            return false;
        }
        List<Coordinate> otherCoordinates = area.getCoordinates();
        if (otherCoordinates.isEmpty()) {
            return false;
        }
        return currentCoordinates.stream().anyMatch(coordinate1 -> currentCoordinates.stream().anyMatch(
                coordinate2 -> coordinate1.x() == coordinate2.x() && coordinate1.y() == coordinate2.y() && (ignorePlane || coordinate1.z() == coordinate2.z())));
    }

    /**
     * Gets this area as an Area.Singular which is comprised of a single Coordinate
     *
     * @return This area as an Area.Singular if it has a single Coordinate, otherwise null.
     */
    public Singular toSingular() {
        List<Coordinate> coordinates = getCoordinates();
        if (coordinates.size() != 1) {
            return null;
        }
        return new Singular(coordinates.get(0));
    }

    public Coordinate getRandomCoordinate() {
        var coords = getCoordinates();
        return coords.get(Random.nextInt(coords.size()));
    }

    public abstract Area.Rectangular toRectangular();

    public abstract Area.Polygonal toPolygonal();

    public abstract Area.Circular toCircular();

    public abstract boolean contains(Locatable locatable);

    public abstract List<Coordinate> getCoordinates();

    /**
     * This method calculates and returns the centroid of a set of coordinates. The centroid is the arithmetic mean position of all the points in the shape.
     * The method first checks if the list of coordinates is empty. If it is, it returns null.
     * If the list is not empty, it calculates the total sum of x, y, and z coordinates separately.
     * After that, it calculates the average of x, y, and z by dividing the total sum by the number of coordinates.
     * The method then returns a new Coordinate object with the average x, y, and z values.
     *
     * @return The centroid of the set of coordinates as a Coordinate object. If the list of coordinates is empty, returns null.
     */
    public Coordinate getCentroid() {
        List<Coordinate> coordinates = this.getCoordinates();
        if (coordinates.isEmpty()) {
            return null;
        }
        int xTotal = 0;
        int yTotal = 0;
        int planeTotal = 0;
        for (Coordinate coordinate : coordinates) {
            xTotal += coordinate.x();
            yTotal += coordinate.y();
            planeTotal += coordinate.z();
        }
        int amount = coordinates.size();
        return new Coordinate(xTotal / amount, yTotal / amount, planeTotal / amount);
    }

    public static final class Rectangular extends Area {
        private Coordinate bottomLeft, topRight;

        public Rectangular(Coordinate bottomLeft, Coordinate topRight) {
            this.bottomLeft = new Coordinate(Math.min(bottomLeft.x(), topRight.x()), Math.min(bottomLeft.y(), topRight.y()),
                                             Math.max(bottomLeft.z(), topRight.z()));
            this.topRight = new Coordinate(Math.max(bottomLeft.x(), topRight.x()), Math.max(bottomLeft.y(), topRight.y()),
                                           Math.max(bottomLeft.z(), topRight.z()));
        }

        public Rectangular(Coordinate bottomLeft, int width, int height) {
            this.bottomLeft = bottomLeft;
            this.topRight = new Coordinate(bottomLeft.x() + width, bottomLeft.y() + height, bottomLeft.z());
        }

        @Override
        public Rectangular toRectangular() {
            return this;
        }

        @Override
        public Polygonal toPolygonal() {
            return new Polygonal(bottomLeft, getBottomRight(), topRight, getTopLeft());
        }

        @Override
        public Circular toCircular() {
            int radius = Math.min(topRight.x() - bottomLeft.x(), topRight.y() - bottomLeft.y());
            return new Circular(getCoordinate(), radius);
        }

        @Override
        public boolean contains(Locatable locatable) {
            Coordinate c = locatable.getCoordinate();
            if (c == null) {
                return false;
            }
            return bottomLeft.x() <= c.x() && bottomLeft.y() <= c.y() && topRight.x() >= c.x() && topRight.y() >= c.y() && bottomLeft.z() == c.z();
        }

        @Override
        public List<Coordinate> getCoordinates() {
            List<Coordinate> coordinates = new ArrayList<>();
            for (int plane = this.bottomLeft.z(); plane <= this.topRight.z(); ++plane) {
                for (int x = this.bottomLeft.x(); x <= this.topRight.x(); ++x) {
                    for (int y = this.bottomLeft.y(); y <= this.topRight.y(); ++y) {
                        coordinates.add(new Coordinate(x, y, plane));
                    }
                }
            }
            return coordinates;
        }

        @Override
        public Coordinate getRandomCoordinate() {
            return new Coordinate(
                    bottomLeft.x() + Random.nextInt(getBottomRight().x() - bottomLeft.x()),
                    bottomLeft.y() + Random.nextInt(getTopLeft().y() - bottomLeft.y()),
                    bottomLeft.z()
            );
        }
        
        public Rectangular derive(int xOffset, int yOffset, int planeOffset) {
            bottomLeft = bottomLeft.derive(-xOffset, -yOffset, -planeOffset);
            topRight = topRight.derive(xOffset, yOffset, planeOffset);
            return this;
        }

        public Coordinate getBottomRight() {
            return new Coordinate(topRight.x(), bottomLeft.y(), bottomLeft.z());
        }

        public Coordinate getTopLeft() {
            return new Coordinate(bottomLeft.x(), topRight.y(), topRight.z());
        }

        @Override
        public Coordinate getCoordinate() {
            int xMiddle = ((topRight.x() - bottomLeft.x()) / 2) + bottomLeft.x();
            int yMiddle = ((topRight.y() - bottomLeft.y()) / 2) + bottomLeft.y();
            return new Coordinate(xMiddle, yMiddle, topRight.z());
        }

        @Override
        public String toString() {
            return "Bottom-Left: " + bottomLeft.toString() + " | Top-Right: " + topRight.toString();
        }
    }

    public static final class Polygonal extends Area {
        private final List<Coordinate> vertices;
        private final int plane;
        private List<Coordinate> coordinates = null;

        public Polygonal(Coordinate... vertices) {
            if (vertices == null || vertices.length < 3) {
                throw new IllegalArgumentException("A polygon must have at least three vertices.");
            }

            this.plane = vertices[0].z();
            for (Coordinate vertex : vertices) {
                if (vertex.z() != this.plane) {
                    throw new IllegalArgumentException("All vertices must be on the same plane.");
                }
            }
            this.vertices = Arrays.asList(vertices);
        }

        @Override
        public Rectangular toRectangular() {
            int minX = vertices.stream().mapToInt(Coordinate::x).min().orElseThrow();
            int minY = vertices.stream().mapToInt(Coordinate::y).min().orElseThrow();
            int maxX = vertices.stream().mapToInt(Coordinate::x).max().orElseThrow();
            int maxY = vertices.stream().mapToInt(Coordinate::y).max().orElseThrow();

            return new Rectangular(new Coordinate(minX, minY, plane), new Coordinate(maxX, maxY, plane));
        }

        @Override
        public Polygonal toPolygonal() {
            return this;
        }

        @Override
        public Circular toCircular() {
            return toRectangular().toCircular();
        }

        @Override
        public boolean contains(Locatable locatable) {
            Coordinate c = locatable.getCoordinate();
            if (c == null || c.z() != plane) {
                return false;
            }
            return isPointInPolygon(c.x(), c.y());
        }

        @Override
        public List<Coordinate> getCoordinates() {
            if (this.coordinates == null || this.coordinates.isEmpty()) {
                this.coordinates = new ArrayList<>();
                Rectangular boundingBox = this.toRectangular();
                for (int x = boundingBox.bottomLeft.x(); x <= boundingBox.topRight.x(); x++) {
                    for (int y = boundingBox.bottomLeft.y(); y <= boundingBox.topRight.y(); y++) {
                        if (isPointInPolygon(x, y)) {
                            this.coordinates.add(new Coordinate(x, y, plane));
                        }
                    }
                }
            }
            return this.coordinates;
        }

        @Override
        public Coordinate getCoordinate() {
            return toRectangular().getCoordinate();
        }

        private boolean isPointInPolygon(int x, int y) {
            boolean inside = false;
            int n = vertices.size();
            for (int i = 0, j = n - 1; i < n; j = i++) {
                Coordinate vi = vertices.get(i);
                Coordinate vj = vertices.get(j);

                boolean intersect = (vi.y() > y) != (vj.y() > y)
                        && (x < (vj.x() - vi.x()) * (y - vi.y()) / (vj.y() - vi.y()) + vi.x());
                if (intersect) {
                    inside = !inside;
                }
            }
            return inside;
        }
    }

    public static final class Circular extends Area {
        private final double radius;
        private final Coordinate center;
        private List<Coordinate> coordinates;


        public Circular(Coordinate center, double radius) {
            this.center = center;
            this.radius = radius;
        }

        @Override
        public Rectangular toRectangular() {
            return new Rectangular(center.derive(-(int) radius, -(int) radius, 0), center.derive((int) radius, (int) radius, 0));
        }

        @Override
        public Polygonal toPolygonal() {
            return toRectangular().toPolygonal();
        }

        @Override
        public Circular toCircular() {
            return this;
        }

        @Override
        public boolean contains(Locatable locatable) {
            return Distance.between(center, locatable) <= radius;
        }

        public Circular derive(int xOffset, int yOffset, int planeOffset) {
            return new Circular(center.derive(xOffset, yOffset, -planeOffset), radius);
        }

        @Override
        public List<Coordinate> getCoordinates() {
            if (this.coordinates == null) {
                List<Coordinate> coordinates = new ArrayList<>();
                int centerX = center.x();
                int centerY = center.y();
                int z = center.z();

                for (double angle = 0; angle < 360; angle += 1) {
                    double radianAngle = Math.toRadians(angle);
                    int x = (int) Math.round(centerX + radius * Math.cos(radianAngle));
                    int y = (int) Math.round(centerY + radius * Math.sin(radianAngle));
                    coordinates.add(new Coordinate(x, y, z));
                }
                this.coordinates = coordinates;
            }
            return this.coordinates;
        }

        @Override
        public Coordinate getRandomCoordinate() {
            // Calculate a random angle between 0 and 360 degrees
            double randomAngle = Math.toRadians(Random.nextDouble(0, 360));

            // Calculate the random x and y coordinates on the circumference of the circle
            int x = (int) Math.round(center.x() + radius * Math.cos(randomAngle));
            int y = (int) Math.round(center.y() + radius * Math.sin(randomAngle));

            // Get the z coordinate from the center coordinate
            int z = center.z();

            return new Coordinate(x, y, z);
        }

        public double getRadius() {
            return radius;
        }

        /**
         * Gets the center {@link Coordinate} of this {@link Circular} {@link Area}.
         *
         * @return the center {@link Coordinate} of this {@link Circular} {@link Area}.
         */
        @Override
        public Coordinate getCoordinate() {
            return center;
        }
    }

    public static final class Singular extends Area {
        private final Coordinate coordinate;

        public Singular(Coordinate coordinate) {
            this.coordinate = coordinate;
        }

        /**
         * Returns the position of the entity on the world graph.
         *
         * @return The position of the entity, or null if it cannot be determined or is no longer on the world graph.
         */
        @Override
        public Coordinate getCoordinate() {
            return coordinate;
        }

        @Override
        public Area.Rectangular toRectangular() {
            return new Rectangular(coordinate, coordinate);
        }

        @Override
        public Area.Polygonal toPolygonal() {
            return new Polygonal(coordinate);
        }

        @Override
        public Area.Circular toCircular() {
            return new Circular(coordinate, 0);
        }

        @Override
        public boolean contains(Locatable locatable) {
            return Objects.equals(coordinate, locatable.getCoordinate());
        }

        @Override
        public List<Coordinate> getCoordinates() {
            return Collections.singletonList(coordinate);
        }
    }
}
