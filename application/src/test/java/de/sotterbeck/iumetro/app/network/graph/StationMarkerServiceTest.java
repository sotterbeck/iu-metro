package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.station.MetroStationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationMarkerServiceTest {

    @Mock
    private RailRepository railRepository;
    @Mock
    private MetroStationRepository metroStationRepository;
    @Mock
    private StationMarkerRepository markerRepository;
    @Mock
    private MarkerHighlighter highlighter;

    private StationMarkerService stationMarkerService;

    @BeforeEach
    void setUp() {
        stationMarkerService = new StationMarkerService(railRepository, markerRepository, metroStationRepository, highlighter);
    }

    @Test
    void add_ShouldNotAdd_WhenBlockIsInvalid() {
        var position = new PositionDto(0, 0, 0);
        var name = "Station";
        when(railRepository.findRailAt(position)).thenReturn(RailRepository.RailShape.NORTH_EAST);

        var response = stationMarkerService.add(name, position);

        assertThat(response).isEqualTo(StationMarkerService.Response.INVALID_BLOCK);
    }

    @Test
    void add_ShouldNotAdd_WhenAlreadyMarked() {
        var position = new PositionDto(0, 0, 0);
        var name = "Station";
        when(markerRepository.existsByPosition(position)).thenReturn(true);

        var response = stationMarkerService.add(name, position);

        assertThat(response).isEqualTo(StationMarkerService.Response.ALREADY_MARKED);
    }

    @Test
    void add_ShouldAddCreateStationAndHighlight_WhenStationDoesNotExist() {
        var position = new PositionDto(0, 0, 0);
        var name = "Station";
        when(metroStationRepository.existsByName(any())).thenReturn(false);
        when(markerRepository.existsByPosition(position)).thenReturn(false);
        when(markerRepository.findAllByStation(name)).thenReturn(List.of(new MarkerDto(name, position)));
        when(railRepository.findRailAt(position)).thenReturn(RailRepository.RailShape.NORTH_SOUTH);

        var response = stationMarkerService.add(name, position);

        verify(metroStationRepository).save(any());
        verify(markerRepository).save(name, position);
        verify(highlighter).highlight(any());
        assertThat(response).isEqualTo(StationMarkerService.Response.SUCCESS_ADDED_STATION);
    }

    @Test
    void add_ShouldAddAndHighlight_WhenEverythingValid() {
        var position = new PositionDto(0, 0, 0);
        var name = "Station";
        when(metroStationRepository.existsByName(any())).thenReturn(true);
        when(markerRepository.existsByPosition(position)).thenReturn(false);
        when(markerRepository.findAllByStation(name)).thenReturn(List.of(new MarkerDto(name, position)));
        when(railRepository.findRailAt(position)).thenReturn(RailRepository.RailShape.NORTH_SOUTH);

        var response = stationMarkerService.add(name, position);

        verify(markerRepository).save(name, position);
        verify(highlighter).highlight(any());
        assertThat(response).isEqualTo(StationMarkerService.Response.SUCCESS);
    }

    @Test
    void remove_ShouldReturnFalse_WhenBlockIsNotMarked() {
        var position = new PositionDto(0, 0, 0);
        when(markerRepository.existsByPosition(position)).thenReturn(false);

        var deleted = stationMarkerService.remove(position);

        assertThat(deleted).isFalse();
    }

    @Test
    void remove_ShouldRemoveAndHighlight_WhenBlockIsMarked() {
        var position = new PositionDto(0, 0, 0);
        var name = "Station";
        when(markerRepository.existsByPosition(position)).thenReturn(true);
        when(markerRepository.findByPosition(position)).thenReturn(Optional.of(new MarkerDto(name, position)));

        var deleted = stationMarkerService.remove(position);

        verify(highlighter).highlight(any());
        verify(markerRepository).deleteByPosition(position);
        assertThat(deleted).isTrue();
    }

    @Test
    void highlightAll_ShouldReturnFalse_WhenStationDoesNotExist() {
        var name = "Station";
        when(metroStationRepository.existsByName(name)).thenReturn(false);

        var response = stationMarkerService.highlightAll(name);

        assertThat(response).isFalse();
    }

    @Test
    void highlightAll_ShouldHighlightAllMarker_WhenStationExists() {
        var name = "Station";
        when(metroStationRepository.existsByName(name)).thenReturn(true);
        when(markerRepository.findAllByStation(name)).thenReturn(List.of(
                new MarkerDto(name, new PositionDto(0, 0, 0)),
                new MarkerDto(name, new PositionDto(0, 0, 1))
        ));

        var response = stationMarkerService.highlightAll(name);

        verify(highlighter).highlight(any());
        assertThat(response).isTrue();
    }

}