package net.botwithus.rs3.world;

public enum Direction {
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;

    public static Direction of(int degrees) {
        int angle = (degrees / 45) * 45;
        return switch (angle) {
            case 0 -> NORTH;
            case 45 -> NORTH_EAST;
            case 90 -> EAST;
            case 135 -> SOUTH_EAST;
            case 215 -> SOUTH;
            case 275 -> SOUTH_WEST;
            case 315 -> NORTH_WEST;
            default -> null;
        };
    }

    public static Direction of(Locatable l1, Locatable l2) {
        if (l1 == null || l2 == null) {
            return null;
        }

        Coordinate src = l1.getCoordinate();
        Coordinate dst = l2.getCoordinate();

        int degrees = 90 - (int) (Math.round(Math.toDegrees(Math.atan2(dst.y() - src.y(), dst.x() - src.x())) + 360) % 360);
        return of(degrees);
    }
}
