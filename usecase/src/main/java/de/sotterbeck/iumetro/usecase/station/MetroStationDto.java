package de.sotterbeck.iumetro.usecase.station;

import de.sotterbeck.iumetro.usecase.faregate.PositionDto;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class MetroStationDto {

    private final UUID id;
    private final String name;
    private final String alias;
    private final PositionDto position;

    public MetroStationDto(UUID id, String name, String alias, PositionDto position) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.position = position;
    }

    public MetroStationDto(UUID uuid, String stationName, String alias) {
        this(uuid, stationName, alias, null);
    }

    public MetroStationDto(UUID id, String stationName) {
        this(id, stationName, null, null);
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Optional<String> alias() {
        return Optional.ofNullable(alias);
    }

    public Optional<PositionDto> position() {
        return Optional.ofNullable(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetroStationDto that = (MetroStationDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(alias, that.alias) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, alias, position);
    }

    @Override
    public String toString() {
        return "MetroStationDto[" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", position=" + position +
                ']';
    }

}
