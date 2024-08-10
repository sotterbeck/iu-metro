package de.sotterbeck.iumetro.entity.common;

public enum Orientation {
    NORTH, EAST, SOUTH, WEST;

    public static Orientation fromString(String orientation) {
        return switch (orientation.toLowerCase()) {
            case "north" -> NORTH;
            case "east" -> EAST;
            case "south" -> SOUTH;
            case "west" -> WEST;
            default -> throw new IllegalArgumentException("Unknown orientation: " + orientation);
        };
    }

    public Position getRelativePosition(Position offset) {
        return switch (this) {
            case NORTH -> new Position(offset.x(), offset.y(), -offset.z());
            case EAST -> new Position(offset.z(), offset.y(), offset.x());
            case SOUTH -> new Position(-offset.x(), offset.y(), offset.z());
            case WEST -> new Position(-offset.z(), offset.y(), -offset.x());
        };
    }

    public Orientation opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case EAST -> WEST;
            case SOUTH -> NORTH;
            case WEST -> EAST;
        };
    }
}
