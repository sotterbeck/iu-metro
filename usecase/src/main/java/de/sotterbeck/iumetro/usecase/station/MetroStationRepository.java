package de.sotterbeck.iumetro.usecase.station;

import java.util.Collection;
import java.util.Optional;

public interface MetroStationRepository {

    Collection<MetroStationDto> getAll();

    Optional<MetroStationDto> getByName(String name);

    void save(MetroStationDto station);

    boolean existsByName(String name);

    void deleteByName(String name);

}
