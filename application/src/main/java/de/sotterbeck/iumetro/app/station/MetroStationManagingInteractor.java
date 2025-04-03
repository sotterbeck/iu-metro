package de.sotterbeck.iumetro.app.station;

import de.sotterbeck.iumetro.app.common.PositionDto;

import java.util.List;
import java.util.UUID;

public class MetroStationManagingInteractor {

    private final MetroStationRepository metroStationRepository;

    public MetroStationManagingInteractor(MetroStationRepository metroStationRepository) {
        this.metroStationRepository = metroStationRepository;
    }

    public MetroStationResponseModel save(MetroStationRequestModel request) {
        String stationName = request.name();
        if (metroStationRepository.existsByName(stationName)) {
            MetroStationDto metroStationDto = metroStationRepository.getByName(stationName).orElseThrow();
            return toResponseModel(metroStationDto);
        }

        MetroStationDto metroStationDto = new MetroStationDto(UUID.randomUUID(), stationName);
        metroStationRepository.save(metroStationDto);
        return toResponseModel(metroStationDto);
    }

    public List<MetroStationResponseModel> getAll() {
        return metroStationRepository.getAll().stream()
                .map(this::toResponseModel)
                .toList();
    }

    public List<MetroStationResponseModel> getAllPositioned() {
        return metroStationRepository.getAll().stream()
                .filter(metroStationDto -> metroStationDto.position().isPresent())
                .map(this::toResponseModel)
                .toList();
    }

    public List<String> getAllStationNames() {
        return metroStationRepository.getAllStationNames().stream()
                .toList();
    }

    public boolean delete(String stationName) {
        if (!metroStationRepository.existsByName(stationName)) {
            return false;
        }

        metroStationRepository.deleteByName(stationName);
        return true;
    }

    private MetroStationResponseModel toResponseModel(MetroStationDto metroStationDto) {
        String alias = metroStationDto.alias()
                .map(a -> " / " + a)
                .orElse("");

        String id = metroStationDto.id().toString();
        String displayId = metroStationDto.id().toString().substring(0, 8) + alias;
        PositionDto position = metroStationDto.position().orElse(null);
        return new MetroStationResponseModel(id, displayId, metroStationDto.name(), position);
    }

}
