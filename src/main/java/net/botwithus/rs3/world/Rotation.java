package net.botwithus.rs3.world;

public record Rotation(float dirX, float dirY, float dirZ) {

    public static final int DEGREE_OFFSET = 90;

    public float yaw() {
        return (float) Math.toDegrees(Math.atan2(dirZ, dirX)) + DEGREE_OFFSET;
    }

    public float pitch() {
        return (float) Math.toDegrees(Math.atan2(dirY, Math.sqrt(dirX * dirX + dirZ * dirZ)));
    }

    public int angular() {
        return Math.round((yaw() + 360) % 360);
    }

    public boolean facing(Coordinate position, Coordinate position1) {
        // Vector from position to position1
        float segmentX = position1.x() - position.x();
        float segmentY = position1.y() - position.y();
        float segmentZ = position1.z() - position.z();

        // Normalize the direction vector
        float dirMagnitude = (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        float normDirX = dirX / dirMagnitude;
        float normDirY = dirY / dirMagnitude;
        float normDirZ = dirZ / dirMagnitude;

        // Normalize the segment vector
        float segmentMagnitude = (float) Math.sqrt(segmentX * segmentX + segmentY * segmentY + segmentZ * segmentZ);
        float normSegmentX = segmentX / segmentMagnitude;
        float normSegmentY = segmentY / segmentMagnitude;
        float normSegmentZ = segmentZ / segmentMagnitude;

        // Calculate the dot product of the normalized vectors
        float dotProduct = normDirX * normSegmentX + normDirY * normSegmentY + normDirZ * normSegmentZ;

        // Check if the dot product is positive, indicating the vectors are aligned
        return dotProduct > 0;
    }
}