package de.sotterbeck.iumetro.usecase.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

}