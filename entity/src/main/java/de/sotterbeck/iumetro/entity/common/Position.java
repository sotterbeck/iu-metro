package de.sotterbeck.iumetro.entity.common;

public record Position(int x, int y, int z) {

    public Position translate(int dx, int dy, int dz) {
        return new Position(x + dx, y + dy, z + dz);
    }

    public Position translate(Position dPos) {
        return translate(dPos.x, dPos.y, dPos.z);
    }

    public Position multiplied(int scalar) {
        return new Position(x * scalar, y * scalar, z * scalar);
    }

}
