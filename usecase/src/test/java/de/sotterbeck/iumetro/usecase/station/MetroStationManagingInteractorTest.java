package de.sotterbeck.iumetro.usecase.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetroStationManagingInteractorTest {

    @Mock
    private MetroStationRepository metroStationRepository;

    private MetroStationManagingInteractor metroStationManagingInteractor;

    @BeforeEach
    void setUp() {
        metroStationManagingInteractor = new MetroStationManagingInteractor(metroStationRepository);
    }

    @Test
    void save_ShouldNotSaveNewStation_WhenStationWithSameNameExists() {
        String stationName = "Station 1";
        UUID id = UUID.fromString("755796fb-4240-4612-961d-a528ec884a7a");
        MetroStationRequestModel request = new MetroStationRequestModel(stationName);
        when(metroStationRepository.existsByName(stationName)).thenReturn(true);
        when(metroStationRepository.getByName(stationName)).thenReturn(Optional.of(new MetroStationDto(id, stationName)));

        MetroStationResponseModel reponse = metroStationManagingInteractor.save(request);

        verify(metroStationRepository, never()).save(any());
        assertThat(reponse).isNotNull();
    }

    @Test
    void save_ShouldSaveNewStation_WhenStationWithNameDoesNotExists() {
        String stationName = "Station 1";
        MetroStationRequestModel request = new MetroStationRequestModel(stationName);
        when(metroStationRepository.existsByName(stationName)).thenReturn(false);

        MetroStationResponseModel response = metroStationManagingInteractor.save(request);

        verify(metroStationRepository).save(any());
        assertThat(response).isNotNull();
    }

    @Test
    void getAll_ShouldReturnAllStations_WhenStationExists() {
        when(metroStationRepository.getAll()).thenReturn(List.of(
                new MetroStationDto(UUID.fromString("2acd6e2a-b4da-4aa4-b19f-92805c14a1a3"), "Station 1"),
                new MetroStationDto(UUID.fromString("bcc38a6a-0816-473a-85be-3186560c7e5d"), "Station 2")));

        List<MetroStationResponseModel> response = metroStationManagingInteractor.getAll();

        assertThat(response).hasSize(2);
    }

    @Test
    void getAll_ShouldReturnAllStationsCorrectlyFormatted_WhenStationsHaveAliases() {
        when(metroStationRepository.getAll()).thenReturn(List.of(
                new MetroStationDto(UUID.fromString("2acd6e2a-b4da-4aa4-b19f-92805c14a1a3"), "Station 1", "ST"),
                new MetroStationDto(UUID.fromString("bcc38a6a-0816-473a-85be-3186560c7e5d"), "Station 2", "ST2")));

        List<MetroStationResponseModel> response = metroStationManagingInteractor.getAll();
        assertThat(response).containsExactlyInAnyOrder(
                new MetroStationResponseModel("2acd6e2a-b4da-4aa4-b19f-92805c14a1a3", "2acd6e2a / ST", "Station 1", null),
                new MetroStationResponseModel("bcc38a6a-0816-473a-85be-3186560c7e5d", "bcc38a6a / ST2", "Station 2", null)

        );
    }

    @Test
    void delete_ShouldReturnFalse_WhenStationDoesNotExist() {
        String stationName = "Station 1";
        when(metroStationRepository.existsByName(stationName)).thenReturn(false);

        boolean deleted = metroStationManagingInteractor.delete(stationName);

        assertThat(deleted).isFalse();
    }

    @Test
    void delete_ShouldDeleteStationAndReturnTrue_WhenStationExists() {
        String stationName = "Station 1";
        when(metroStationRepository.existsByName(stationName)).thenReturn(true);

        boolean deleted = metroStationManagingInteractor.delete(stationName);

        assertThat(deleted).isTrue();
    }
}