package de.sotterbeck.iumetro.app.station;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.network.line.LineDto;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class MetroStationDto {

    private final UUID id;
    private final String name;
    private final String alias;
    private final PositionDto position;
    private final List<LineDto> lines;

    public MetroStationDto(UUID id, String name, String alias, PositionDto position, List<LineDto> lines) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.position = position;
        this.lines = lines;
    }

    public MetroStationDto(UUID uuid, String name, String alias, PositionDto position) {
        this(uuid, name, alias, position, List.of());
    }

    public MetroStationDto(UUID id, String name, String alias) {
        this(id, name, alias, null, List.of());
    }

    public MetroStationDto(UUID id, String name) {
        this(id, name, null, null, List.of());
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

    public List<LineDto> lines() {
        return lines;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MetroStationDto that = (MetroStationDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MetroStationDto[" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", position=" + position +
                ", lines=" + lines +
                ']';
    }

}
