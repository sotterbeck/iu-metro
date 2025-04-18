package de.sotterbeck.iumetro.app.station;

import de.sotterbeck.iumetro.app.common.PositionDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MetroStationRepository {

    Collection<MetroStationDto> getAll();

    Collection<String> getAllAliases();

    Collection<String> getAllStationNames();

    Optional<MetroStationDto> getByName(String name);

    Optional<MetroStationDto> getById(UUID id);

    void save(MetroStationDto name);

    void saveAlias(String stationName, String alias);

    void savePosition(String stationName, PositionDto position);

    void saveLines(String stationName, List<String> lines);

    boolean existsById(UUID id);

    boolean existsByName(String name);

    void deleteByName(String name);

    void deleteAliasByName(String stationName);

    void deletePositionByName(String stationName);

}
