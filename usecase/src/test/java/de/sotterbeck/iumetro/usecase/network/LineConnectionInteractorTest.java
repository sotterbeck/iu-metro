package de.sotterbeck.iumetro.usecase.network;

import de.sotterbeck.iumetro.usecase.common.CommonPresenter;
import de.sotterbeck.iumetro.usecase.faregate.PositionDto;
import de.sotterbeck.iumetro.usecase.network.graph.ConnectionRequestModel;
import de.sotterbeck.iumetro.usecase.network.graph.MetroNetworkRepository;
import de.sotterbeck.iumetro.usecase.network.line.LineConnectionDto;
import de.sotterbeck.iumetro.usecase.network.line.LineConnectionInteractor;
import de.sotterbeck.iumetro.usecase.station.MetroStationDto;
import de.sotterbeck.iumetro.usecase.station.MetroStationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineConnectionInteractorTest {

    @Mock
    private MetroNetworkRepository metroNetworkRepository;
    @Mock
    private MetroStationRepository metroStationRepository;
    @Mock
    private CommonPresenter presenter;

    private LineConnectionInteractor lineConnectionInteractor;

    @BeforeEach
    void setUp() {
        lineConnectionInteractor = new LineConnectionInteractor(metroNetworkRepository, metroStationRepository, presenter);
    }

    @Test
    void saveConnection_ShouldPresentFailView_WhenStationIdsDontExist() {
        UUID station1Id = UUID.fromString("4c09f0af-d2f5-443c-bdec-6abcc7cca1a3");
        UUID station2Id = UUID.fromString("2032a5d3-dffc-450f-a6cf-3efffa246096");
        when(metroStationRepository.getById(station1Id)).thenReturn(Optional.empty());
        ConnectionRequestModel request = new ConnectionRequestModel(station1Id, station2Id, List.of());

        lineConnectionInteractor.saveConnection(request);

        verify(presenter).prepareFailView("Station " + station1Id + " not found");
    }

    @Test
    void saveConnection_ShouldPresentFailView_WhenConnectionBetweenStationsAlreadyExists() {
        UUID station1Id = UUID.fromString("4c09f0af-d2f5-443c-bdec-6abcc7cca1a3");
        UUID station2Id = UUID.fromString("2032a5d3-dffc-450f-a6cf-3efffa246096");
        when(metroStationRepository.getById(station1Id)).thenReturn(Optional.of(new MetroStationDto(station1Id, "Station 1")));
        when(metroStationRepository.getById(station2Id)).thenReturn(Optional.of(new MetroStationDto(station2Id, "Station 2")));
        when(metroNetworkRepository.existsConnection(station1Id, station2Id)).thenReturn(true);
        ConnectionRequestModel request = new ConnectionRequestModel(station1Id, station2Id, List.of());

        lineConnectionInteractor.saveConnection(request);

        verify(presenter).prepareFailView("Connection between stations " + station1Id + " and " + station2Id + " already exists");
    }

    @Test
    void saveConnection_ShouldPresentFailView_WhenEitherStationDoesNotHaveAPosition() {
        UUID station1Id = UUID.fromString("4c09f0af-d2f5-443c-bdec-6abcc7cca1a3");
        UUID station2Id = UUID.fromString("2032a5d3-dffc-450f-a6cf-3efffa246096");
        when(metroStationRepository.getById(station1Id)).thenReturn(Optional.of(new MetroStationDto(station1Id, "Station 1")));
        when(metroStationRepository.getById(station2Id)).thenReturn(Optional.of(new MetroStationDto(station2Id, "Station 2")));
        when(metroNetworkRepository.existsConnection(station1Id, station2Id)).thenReturn(false);
        ConnectionRequestModel request = new ConnectionRequestModel(station1Id, station2Id, List.of());

        lineConnectionInteractor.saveConnection(request);

        verify(presenter).prepareFailView("Station " + station1Id + " does not have a position");

    }

    @Test
    void saveConnection_ShouldPresentFailView_WhenLineDoesNotExist() {
        UUID station1Id = UUID.fromString("4c09f0af-d2f5-443c-bdec-6abcc7cca1a3");
        UUID station2Id = UUID.fromString("2032a5d3-dffc-450f-a6cf-3efffa246096");
        LineConnectionDto line = new LineConnectionDto("M1", 0);
        when(metroStationRepository.getById(station1Id)).thenReturn(Optional.of(new MetroStationDto(station1Id, "Station 1")));
        when(metroStationRepository.getById(station2Id)).thenReturn(Optional.of(new MetroStationDto(station2Id, "Station 2")));
        when(metroNetworkRepository.existsLineByName(line.name())).thenReturn(false);

        lineConnectionInteractor.saveConnection(new ConnectionRequestModel(station1Id, station2Id, List.of(line)));

        verify(presenter).prepareFailView("Line " + line.name() + " does not exist");
    }

    @Test
    void saveConnection_ShouldSaveConnectionWithCorrectDistance_WhenBothStationsExistAndHavePositions() {
        UUID station1Id = UUID.fromString("4c09f0af-d2f5-443c-bdec-6abcc7cca1a3");
        UUID station2Id = UUID.fromString("2032a5d3-dffc-450f-a6cf-3efffa246096");
        LineConnectionDto line = new LineConnectionDto("M1", 0);
        PositionDto station2Position = new PositionDto(0, 0, 0);
        PositionDto station1Position = new PositionDto(1, 1, 1);
        when(metroStationRepository.getById(station1Id)).thenReturn(Optional.of(new MetroStationDto(station1Id, "Station 1", "ST1", station1Position)));
        when(metroStationRepository.getById(station2Id)).thenReturn(Optional.of(new MetroStationDto(station2Id, "Station 2", "ST2", station2Position)));
        when(metroNetworkRepository.existsConnection(station1Id, station2Id)).thenReturn(false);
        when(metroNetworkRepository.existsLineByName(line.name())).thenReturn(true);
        ConnectionRequestModel request = new ConnectionRequestModel(station1Id, station2Id, List.of(line));

        lineConnectionInteractor.saveConnection(request);

        verify(metroNetworkRepository).saveConnection(station1Id, station2Id, 2, List.of(line));
    }

}