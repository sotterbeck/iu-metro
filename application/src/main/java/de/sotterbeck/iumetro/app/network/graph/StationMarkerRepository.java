package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.common.PositionDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StationMarkerRepository {

    Map<String, List<MarkerDto>> findAll();

    Collection<MarkerDto> findAllByStation(String stationName);

    boolean existsByPosition(PositionDto position);

    boolean existsForAllStations();

    void save(String stationName, PositionDto position);

    Optional<MarkerDto> findByPosition(PositionDto position);

    void deleteByPosition(PositionDto position);

    Map<String, Integer> countMarkersByStation();
}
