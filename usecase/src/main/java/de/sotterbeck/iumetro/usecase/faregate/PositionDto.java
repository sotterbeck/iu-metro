package de.sotterbeck.iumetro.usecase.faregate;

public record PositionDto(int x, int y, int z) {

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

}
