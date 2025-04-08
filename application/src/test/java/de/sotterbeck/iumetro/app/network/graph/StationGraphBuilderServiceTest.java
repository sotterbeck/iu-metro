package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.common.PositionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationGraphBuilderServiceTest {

    @Mock
    private StationGraphBuilder stationGraphBuilder;
    @Mock
    private StationMarkerRepository markerRepository;
    @Mock
    private MetroNetworkRepository metroNetworkRepository;

    private StationGraphBuilderService stationGraphBuilderService;

    @BeforeEach
    void setUp() {
        stationGraphBuilderService = new StationGraphBuilderService(stationGraphBuilder, markerRepository, metroNetworkRepository);
    }

    @Test
    void discoverConnections_ShouldRespondWithFailure_WhenNotAllStationsHaveMarkers() {
        when(markerRepository.existsForAllStations()).thenReturn(false);
        when(markerRepository.findAll()).thenReturn(Collections.emptyMap());

        var result = stationGraphBuilderService.discoverConnections();

        assertThat(result).isEqualTo(StationGraphBuilderService.Response.Failure.MARKERS_MISSING);
    }

    @Test
    void discoverConnections_ShouldRespondWithSuccess_WhenAllStationsHaveMarkers() {
        var markers = Map.of(
                "Station 1", List.of(new MarkerDto("Station 1", new PositionDto(0, 0, 0))),
                "Station 2", List.of(new MarkerDto("Station 2", new PositionDto(1, 1, 0)))
        );
        when(markerRepository.existsForAllStations()).thenReturn(true);
        when(markerRepository.findAll()).thenReturn(markers);
        when(stationGraphBuilder.buildGraph(any())).thenReturn(Map.of());

        var result = stationGraphBuilderService.discoverConnections();

        verify(metroNetworkRepository).saveNetwork(Map.of());
        assertThat(result).isEqualTo(new StationGraphBuilderService.Response.Success(Map.of()));
    }

}