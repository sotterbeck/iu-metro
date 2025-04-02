package de.sotterbeck.iumetro.usecase.network.line;

import de.sotterbeck.iumetro.entity.common.Position;
import de.sotterbeck.iumetro.usecase.common.CommonPresenter;
import de.sotterbeck.iumetro.usecase.network.graph.ConnectionRequestModel;
import de.sotterbeck.iumetro.usecase.network.graph.MetroNetworkRepository;
import de.sotterbeck.iumetro.usecase.station.MetroStationDto;
import de.sotterbeck.iumetro.usecase.station.MetroStationRepository;

import java.util.Optional;

public class LineConnectionInteractor {

    private final MetroNetworkRepository metroNetworkRepository;
    private final MetroStationRepository metroStationRepository;
    private final CommonPresenter presenter;

    public LineConnectionInteractor(MetroNetworkRepository metroNetworkRepository, MetroStationRepository metroStationRepository, CommonPresenter presenter) {
        this.metroNetworkRepository = metroNetworkRepository;
        this.metroStationRepository = metroStationRepository;
        this.presenter = presenter;
    }

    public void saveConnection(ConnectionRequestModel request) {
        Optional<MetroStationDto> optionalStation1 = metroStationRepository.getById(request.station1Id());
        Optional<MetroStationDto> optionalStation2 = metroStationRepository.getById(request.station2Id());

        for (LineConnectionDto line : request.lines()) {
            if (!metroNetworkRepository.existsLineByName(line.name())) {
                presenter.prepareFailView("Line " + line.name() + " does not exist");
                return;
            }
        }

        if (optionalStation1.isEmpty()) {
            presenter.prepareFailView("Station " + request.station1Id() + " not found");
            return;
        }

        if (optionalStation2.isEmpty()) {
            presenter.prepareFailView("Station " + request.station2Id() + " not found");
            return;
        }

        if (metroNetworkRepository.existsConnection(request.station1Id(), request.station2Id())) {
            presenter.prepareFailView("Connection between stations " + request.station1Id() + " and " + request.station2Id() + " already exists");
            return;
        }

        MetroStationDto station1 = optionalStation1.orElseThrow();
        if (station1.position().isEmpty()) {
            presenter.prepareFailView("Station " + request.station1Id() + " does not have a position");
            return;
        }

        MetroStationDto station2 = optionalStation2.orElseThrow();
        if (station2.position().isEmpty()) {
            presenter.prepareFailView("Station " + request.station2Id() + " does not have a position");
            return;
        }

        Position station1Position = station1.position()
                .map(dto -> new Position(dto.x(), dto.y(), dto.z())).orElseThrow();

        Position station2Position = station2.position()
                .map(dto -> new Position(dto.x(), dto.y(), dto.z())).orElseThrow();

        double distance = station1Position.distanceTo(station2Position);
        int roundedDistance = (int) Math.round(distance);

        metroNetworkRepository.saveConnection(station1.id(), station2.id(), roundedDistance, request.lines());
    }

}
