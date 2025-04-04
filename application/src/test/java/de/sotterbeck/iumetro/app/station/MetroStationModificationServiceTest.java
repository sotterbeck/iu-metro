package de.sotterbeck.iumetro.app.station;

import de.sotterbeck.iumetro.app.common.PositionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetroStationModificationServiceTest {

    @Mock
    private MetroStationRepository repository;

    private MetroStationModificationService metroStationModificationService;

    @BeforeEach
    void setUp() {
        metroStationModificationService = new MetroStationModificationService(repository);
    }

    @Test
    void saveAlias_ShouldReturnNotFound_WhenStationDoesNotExist() {
        String stationName = "Station 1";
        when(repository.existsByName(stationName)).thenReturn(false);

        MetroStationModificationService.Status status = metroStationModificationService.saveAlias(stationName, "any");

        assertThat(status).isEqualTo(MetroStationModificationService.Status.NOT_FOUND);
    }

    @Test
    void saveAlias_ShouldReturnAlreadyExists_WhenAliasAlreadyExists() {
        String stationName = "Station 1";
        String alias = "ST";
        when(repository.existsByName(stationName)).thenReturn(true);
        when(repository.getAllAliases()).thenReturn(List.of(alias));

        MetroStationModificationService.Status status = metroStationModificationService.saveAlias(stationName, alias);


        assertThat(status).isEqualTo(MetroStationModificationService.Status.ALREADY_EXISTS);
    }

    @Test
    void saveAlias_ShouldSaveAliasAndReturnSuccess_WhenAliasIsValid() {
        String stationName = "Station 1";
        String alias = "ST";
        when(repository.existsByName(stationName)).thenReturn(true);
        when(repository.getAllAliases()).thenReturn(Collections.emptyList());

        MetroStationModificationService.Status status = metroStationModificationService.saveAlias(stationName, alias);

        verify(repository).saveAlias(stationName, alias);
        assertThat(status).isEqualTo(MetroStationModificationService.Status.SUCCESS);
    }

    @Test
    void savePosition_ShouldReturnNotFound_WhenStationDoesNotExist() {
        String stationName = "Station 1";
        PositionDto position = new PositionDto(0, 0, 0);
        when(repository.existsByName(stationName)).thenReturn(false);

        MetroStationModificationService.Status status = metroStationModificationService.savePosition(stationName, position);

        assertThat(status).isEqualTo(MetroStationModificationService.Status.NOT_FOUND);
    }

    @Test
    void savePosition_ShouldSavePositionAndReturnSuccess_WhenStationExists() {
        String stationName = "Station 1";
        PositionDto position = new PositionDto(0, 0, 0);
        when(repository.existsByName(stationName)).thenReturn(true);

        MetroStationModificationService.Status status = metroStationModificationService.savePosition(stationName, position);

        verify(repository).savePosition(stationName, position);
        assertThat(status).isEqualTo(MetroStationModificationService.Status.SUCCESS);
    }

    @Test
    void deleteAlias_ShouldReturnNotFound_WhenStationDoesNotExist() {
        String stationName = "Station 1";
        when(repository.existsByName(stationName)).thenReturn(false);

        MetroStationModificationService.Status status = metroStationModificationService.deleteAlias(stationName);

        assertThat(status).isEqualTo(MetroStationModificationService.Status.NOT_FOUND);
    }

    @Test
    void deleteAlias_ShouldDeleteAliasAndReturnSuccess_WhenStationExists() {
        String stationName = "Station 1";
        when(repository.existsByName(stationName)).thenReturn(true);

        MetroStationModificationService.Status status = metroStationModificationService.deleteAlias(stationName);

        verify(repository).deleteAliasByName(stationName);
        assertThat(status).isEqualTo(MetroStationModificationService.Status.SUCCESS);
    }

    @Test
    void deletePosition_ShouldReturnNotFound_WhenStationDoesNotExist() {
        String stationName = "Station 1";
        when(repository.existsByName(stationName)).thenReturn(false);

        MetroStationModificationService.Status status = metroStationModificationService.deletePosition(stationName);

        assertThat(status).isEqualTo(MetroStationModificationService.Status.NOT_FOUND);
    }

    @Test
    void deletePosition_ShouldDeletePositionAndReturnSuccess_WhenStationExists() {
        String stationName = "Station 1";
        when(repository.existsByName(stationName)).thenReturn(true);

        MetroStationModificationService.Status status = metroStationModificationService.deletePosition(stationName);

        verify(repository).deletePositionByName(stationName);
        assertThat(status).isEqualTo(MetroStationModificationService.Status.SUCCESS);
    }

}
