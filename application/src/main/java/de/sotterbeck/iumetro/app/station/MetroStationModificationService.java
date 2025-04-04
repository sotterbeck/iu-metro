package de.sotterbeck.iumetro.app.station;

import de.sotterbeck.iumetro.app.common.PositionDto;

public class MetroStationModificationService {

    private final MetroStationRepository repository;

    public MetroStationModificationService(MetroStationRepository repository) {
        this.repository = repository;
    }

    public Status saveAlias(String stationName, String alias) {
        if (!repository.existsByName(stationName)) {
            return Status.NOT_FOUND;
        }

        if (repository.getAllAliases().contains(alias)) {
            return Status.ALREADY_EXISTS;
        }

        repository.saveAlias(stationName, alias);

        return Status.SUCCESS;
    }

    public Status savePosition(String stationName, PositionDto positionDto) {
        if (!repository.existsByName(stationName)) {
            return Status.NOT_FOUND;
        }

        repository.savePosition(stationName, positionDto);

        return Status.SUCCESS;
    }

    public Status deleteAlias(String stationName) {
        if (!repository.existsByName(stationName)) {
            return Status.NOT_FOUND;
        }

        repository.deleteAliasByName(stationName);
        return Status.SUCCESS;
    }

    public Status deletePosition(String stationName) {
        if (!repository.existsByName(stationName)) {
            return Status.NOT_FOUND;
        }

        repository.deletePositionByName(stationName);
        return Status.SUCCESS;
    }

    public enum Status {
        NOT_FOUND, ALREADY_EXISTS, SUCCESS
    }

}
