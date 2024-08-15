package de.sotterbeck.iumetro.usecase.station;

import de.sotterbeck.iumetro.usecase.faregate.PositionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetroStationTeleportInteractorTest {

    @Mock
    private MetroStationRepository metroStationRepository;

    private MetroStationTeleportInteractor metroStationTeleportInteractor;

    @BeforeEach
    void setUp() {
        metroStationTeleportInteractor = new MetroStationTeleportInteractor(metroStationRepository);
    }

    @Test
    void getAllTeleportableStationNames_ShouldOnlyReturnStationNamesWithPositions() {
        MetroStationDto stationWithPosition = new MetroStationDto(UUID.fromString("2acd6e2a-b4da-4aa4-b19f-92805c14a1a3"),
                "Station 1",
                "ST",
                new PositionDto(0, 0, 0));
        MetroStationDto stationWithoutPosition = new MetroStationDto(UUID.fromString("bcc38a6a-0816-473a-85be-3186560c7e5d"),
                "Station 2",
                "ST2");
        when(metroStationRepository.getAll()).thenReturn(List.of(
                stationWithPosition,
                stationWithoutPosition));

        List<String> response = metroStationTeleportInteractor.getAllTeleportableStationNames();

        assertThat(response).containsOnly(stationWithPosition.name());
    }

    @Test
    void isTeleportable_ShouldReturnFalse_WhenStationDoesNotExist() {
        String stationName = "Station 1";
        when(metroStationRepository.getByName(stationName)).thenReturn(Optional.empty());

        boolean teleportable = metroStationTeleportInteractor.isTeleportable(stationName);

        assertThat(teleportable).isFalse();
    }

    @Test
    void isTeleportable_ShouldReturnFalse_WhenStationExitsButHasNoPosition() {
        String stationName = "Station 2";
        MetroStationDto stationWithoutPosition = new MetroStationDto(UUID.fromString("bcc38a6a-0816-473a-85be-3186560c7e5d"),
                stationName,
                "ST2");
        when(metroStationRepository.getByName(stationName)).thenReturn(Optional.of(stationWithoutPosition));

        boolean teleportable = metroStationTeleportInteractor.isTeleportable(stationName);

        assertThat(teleportable).isFalse();
    }

    @Test
    void isTeleportable_ShouldReturnTrue_WhenStationExitsAndHasPosition() {
        String stationName = "Station 1";
        MetroStationDto stationWithPosition = new MetroStationDto(UUID.fromString("2acd6e2a-b4da-4aa4-b19f-92805c14a1a3"),
                stationName,
                "ST",
                new PositionDto(0, 0, 0));
        when(metroStationRepository.getByName(stationName)).thenReturn(Optional.of(stationWithPosition));

        boolean teleportable = metroStationTeleportInteractor.isTeleportable(stationName);

        assertThat(teleportable).isTrue();
    }

    @Test
    void getPosition_ShouldReturnOptionalEmpty_WhenStationIsNotTeleportable() {
        String stationName = "Station 2";
        MetroStationDto stationWithoutPosition = new MetroStationDto(UUID.fromString("bcc38a6a-0816-473a-85be-3186560c7e5d"),
                stationName,
                "ST2");
        when(metroStationRepository.getByName(stationName)).thenReturn(Optional.of(stationWithoutPosition));

        Optional<PositionDto> position = metroStationTeleportInteractor.getPosition(stationName);

        assertThat(position).isEmpty();
    }

    @Test
    void getPosition_ShouldReturnPosition_WhenStationIsTeleportable() {
        String stationName = "Station 1";
        PositionDto stationPosition = new PositionDto(0, 0, 0);
        MetroStationDto stationWithPosition = new MetroStationDto(UUID.fromString("2acd6e2a-b4da-4aa4-b19f-92805c14a1a3"),
                stationName,
                "ST",
                stationPosition);
        when(metroStationRepository.getByName(stationName)).thenReturn(Optional.of(stationWithPosition));

        Optional<PositionDto> position = metroStationTeleportInteractor.getPosition(stationName);

        assertThat(position).hasValue(stationPosition);
    }

}