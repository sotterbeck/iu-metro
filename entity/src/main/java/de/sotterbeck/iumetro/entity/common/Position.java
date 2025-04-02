package de.sotterbeck.iumetro.entity.common;

/**
 * Represents a 3D position in a Minecraft world, defined by X, Y, and Z coordinates.
 *
 * @param x The X-coordinate (east/west axis).
 * @param y The Y-coordinate (vertical axis, height).
 * @param z The Z-coordinate (north/south axis).
 */
public record Position(int x, int y, int z) {

    public Position translate(int dx, int dy, int dz) {
        return new Position(x + dx, y + dy, z + dz);
    }

    public Position translate(Position dPos) {
        return translate(dPos.x, dPos.y, dPos.z);
    }

    public Position translate(Direction direction, int distance) {
        return switch (direction) {
            case NORTH -> translate(0, 0, -distance);
            case EAST -> translate(distance, 0, 0);
            case SOUTH -> translate(0, 0, distance);
            case WEST -> translate(-distance, 0, 0);
        };
    }

    public Position multiplied(int scalar) {
        return new Position(x * scalar, y * scalar, z * scalar);
    }

    public double distanceTo(Position other) {
        int dx = other.x - this.x;
        int dy = other.y - this.y;
        int dz = other.z - this.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public enum Direction {
        NORTH, EAST, SOUTH, WEST;

        public Direction opposite() {
            return switch (this) {
                case NORTH -> SOUTH;
                case EAST -> WEST;
                case SOUTH -> NORTH;
                case WEST -> EAST;
            };
        }
    }

}
