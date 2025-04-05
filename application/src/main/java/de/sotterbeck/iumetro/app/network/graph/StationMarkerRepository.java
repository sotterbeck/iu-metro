package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.common.PositionDto;

import java.util.Collection;
import java.util.Optional;

public interface StationMarkerRepository {

    boolean existsByPosition(PositionDto position);

    void save(String stationName, PositionDto position);

    Collection<MarkerDto> findAllByStation(String stationName);

    Optional<MarkerDto> findByPosition(PositionDto position);

    void deleteByPosition(PositionDto position);

}
