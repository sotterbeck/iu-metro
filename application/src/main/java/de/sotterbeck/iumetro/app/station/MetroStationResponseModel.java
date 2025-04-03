package de.sotterbeck.iumetro.app.station;

import de.sotterbeck.iumetro.app.common.PositionDto;

import java.util.Objects;
import java.util.Optional;

public final class MetroStationResponseModel {

    private final String id;
    private final String displayId;
    private final String name;
    private final PositionDto position;

    public MetroStationResponseModel(String id, String displayId, String name, PositionDto position) {
        this.id = Objects.requireNonNull(id);
        this.displayId = displayId;
        this.name = Objects.requireNonNull(name);
        this.position = position;
    }

    public String id() {
        return id;
    }

    public String displayId() {
        return displayId;
    }

    public String name() {
        return name;
    }

    public Optional<PositionDto> position() {
        return Optional.ofNullable(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetroStationResponseModel that = (MetroStationResponseModel) o;
        return Objects.equals(id, that.id) && Objects.equals(displayId, that.displayId) && Objects.equals(name, that.name) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, displayId, name, position);
    }

    @Override
    public String toString() {
        return "MetroStationResponseModel[" +
                "id='" + id + '\'' +
                ", displayId='" + displayId + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                ']';
    }

}
