package de.sotterbeck.iumetro.usecase.station;

import de.sotterbeck.iumetro.usecase.faregate.PositionDto;
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
class MetroStationModifyInteractorTest {

    @Mock
    private MetroStationRepository repository;

    private MetroStationModifyInteractor metroStationModifyInteractor;

    @BeforeEach
    void setUp() {
        metroStationModifyInteractor = new MetroStationModifyInteractor(repository);
    }

    @Test
    void saveAlias_ShouldReturnNotFound_WhenStationDoesNotExist() {
        String stationName = "Station 1";
        when(repository.existsByName(stationName)).thenReturn(false);

        MetroStationModifyStatus status = metroStationModifyInteractor.saveAlias(stationName, "any");

        assertThat(status).isEqualTo(MetroStationModifyStatus.NOT_FOUND);
    }

    @Test
    void saveAlias_ShouldReturnAlreadyExists_WhenAliasAlreadyExists() {
        String stationName = "Station 1";
        String alias = "ST";
        when(repository.existsByName(stationName)).thenReturn(true);
        when(repository.getAllAliases()).thenReturn(List.of(alias));

        MetroStationModifyStatus status = metroStationModifyInteractor.saveAlias(stationName, alias);


        assertThat(status).isEqualTo(MetroStationModifyStatus.ALREADY_EXISTS);
    }

    @Test
    void saveAlias_ShouldSaveAliasAndReturnSuccess_WhenAliasIsValid() {
        String stationName = "Station 1";
        String alias = "ST";
        when(repository.existsByName(stationName)).thenReturn(true);
        when(repository.getAllAliases()).thenReturn(Collections.emptyList());

        MetroStationModifyStatus status = metroStationModifyInteractor.saveAlias(stationName, alias);

        verify(repository).saveAlias(stationName, alias);
        assertThat(status).isEqualTo(MetroStationModifyStatus.SUCCESS);
    }

    @Test
    void savePosition_ShouldReturnNotFound_WhenStationDoesNotExist() {
        String stationName = "Station 1";
        PositionDto position = new PositionDto(0, 0, 0);
        when(repository.existsByName(stationName)).thenReturn(false);

        MetroStationModifyStatus status = metroStationModifyInteractor.savePosition(stationName, position);

        assertThat(status).isEqualTo(MetroStationModifyStatus.NOT_FOUND);
    }

    @Test
    void savePosition_ShouldSavePositionAndReturnSuccess_WhenStationExists() {
        String stationName = "Station 1";
        PositionDto position = new PositionDto(0, 0, 0);
        when(repository.existsByName(stationName)).thenReturn(true);

        MetroStationModifyStatus status = metroStationModifyInteractor.savePosition(stationName, position);

        verify(repository).savePosition(stationName, position);
        assertThat(status).isEqualTo(MetroStationModifyStatus.SUCCESS);
    }

    @Test
    void deleteAlias_ShouldReturnNotFound_WhenStationDoesNotExist() {
        String stationName = "Station 1";
        when(repository.existsByName(stationName)).thenReturn(false);

        MetroStationModifyStatus status = metroStationModifyInteractor.deleteAlias(stationName);

        assertThat(status).isEqualTo(MetroStationModifyStatus.NOT_FOUND);
    }

    @Test
    void deleteAlias_ShouldDeleteAliasAndReturnSuccess_WhenStationExists() {
        String stationName = "Station 1";
        when(repository.existsByName(stationName)).thenReturn(true);

        MetroStationModifyStatus status = metroStationModifyInteractor.deleteAlias(stationName);

        verify(repository).deleteAliasByStationName(stationName);
        assertThat(status).isEqualTo(MetroStationModifyStatus.SUCCESS);
    }

    @Test
    void deletePosition_ShouldReturnNotFound_WhenStationDoesNotExist() {
        String stationName = "Station 1";
        when(repository.existsByName(stationName)).thenReturn(false);

        MetroStationModifyStatus status = metroStationModifyInteractor.deletePosition(stationName);

        assertThat(status).isEqualTo(MetroStationModifyStatus.NOT_FOUND);
    }

    @Test
    void deletePosition_ShouldDeletePositionAndReturnSuccess_WhenStationExists() {
        String stationName = "Station 1";
        when(repository.existsByName(stationName)).thenReturn(true);

        MetroStationModifyStatus status = metroStationModifyInteractor.deletePosition(stationName);

        verify(repository).deletePositionByStationName(stationName);
        assertThat(status).isEqualTo(MetroStationModifyStatus.SUCCESS);
    }

}
