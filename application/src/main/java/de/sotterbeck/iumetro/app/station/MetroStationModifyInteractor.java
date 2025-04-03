package de.sotterbeck.iumetro.app.station;

import de.sotterbeck.iumetro.app.common.PositionDto;

public class MetroStationModifyInteractor {

    private final MetroStationRepository repository;

    public MetroStationModifyInteractor(MetroStationRepository repository) {
        this.repository = repository;
    }

    public MetroStationModifyStatus saveAlias(String stationName, String alias) {
        if (!repository.existsByName(stationName)) {
            return MetroStationModifyStatus.NOT_FOUND;
        }

        if (repository.getAllAliases().contains(alias)) {
            return MetroStationModifyStatus.ALREADY_EXISTS;
        }

        repository.saveAlias(stationName, alias);

        return MetroStationModifyStatus.SUCCESS;
    }

    public MetroStationModifyStatus savePosition(String stationName, PositionDto positionDto) {
        if (!repository.existsByName(stationName)) {
            return MetroStationModifyStatus.NOT_FOUND;
        }

        repository.savePosition(stationName, positionDto);

        return MetroStationModifyStatus.SUCCESS;
    }

    public MetroStationModifyStatus deleteAlias(String stationName) {
        if (!repository.existsByName(stationName)) {
            return MetroStationModifyStatus.NOT_FOUND;
        }

        repository.deleteAliasByStationName(stationName);
        return MetroStationModifyStatus.SUCCESS;
    }

    public MetroStationModifyStatus deletePosition(String stationName) {
        if (!repository.existsByName(stationName)) {
            return MetroStationModifyStatus.NOT_FOUND;
        }

        repository.deletePositionByStationName(stationName);
        return MetroStationModifyStatus.SUCCESS;
    }

}
