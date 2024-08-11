package de.sotterbeck.iumetro.usecase.station;

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

    public boolean delete(String stationName) {
        if (!metroStationRepository.existsByName(stationName)) {
            return false;
        }

        metroStationRepository.deleteByName(stationName);
        return true;
    }

    private MetroStationResponseModel toResponseModel(MetroStationDto metroStationDto) {
        return new MetroStationResponseModel(metroStationDto.id().toString(), metroStationDto.name());
    }

}
