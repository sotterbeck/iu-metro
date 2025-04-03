package de.sotterbeck.iumetro.app.station;

import de.sotterbeck.iumetro.app.common.PositionDto;

import java.util.List;
import java.util.Optional;

public class MetroStationTeleportInteractor {

    private final MetroStationRepository metroStationRepository;

    public MetroStationTeleportInteractor(MetroStationRepository metroStationRepository) {
        this.metroStationRepository = metroStationRepository;
    }

    public List<String> getAllTeleportableStationNames() {
        return metroStationRepository.getAll().stream()
                .filter(metroStationDto -> metroStationDto.position().isPresent())
                .map(MetroStationDto::name)
                .toList();
    }

    public boolean isTeleportable(String stationName) {
        Optional<MetroStationDto> station = metroStationRepository.getByName(stationName);
        if (station.isEmpty()) {
            return false;
        }

        Optional<PositionDto> position = station.orElseThrow().position();

        return position.isPresent();
    }

    public Optional<PositionDto> getPosition(String stationName) {
        if (!isTeleportable(stationName)) {
            return Optional.empty();
        }

        return metroStationRepository.getByName(stationName).flatMap(MetroStationDto::position);
    }

}
