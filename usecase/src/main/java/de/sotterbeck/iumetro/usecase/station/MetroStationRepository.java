package de.sotterbeck.iumetro.usecase.station;

import de.sotterbeck.iumetro.usecase.faregate.PositionDto;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface MetroStationRepository {

    Collection<MetroStationDto> getAll();

    Collection<String> getAllAliases();

    Collection<String> getAllStationNames();

    Optional<MetroStationDto> getByName(String name);

    Optional<MetroStationDto> getById(UUID id);

    void save(MetroStationDto station);

    void saveAlias(String stationName, String alias);

    void savePosition(String stationName, PositionDto position);

    boolean existsById(UUID id);

    boolean existsByName(String name);

    void deleteByName(String name);

    void deleteAliasByStationName(String stationName);

    void deletePositionByStationName(String stationName);

}
