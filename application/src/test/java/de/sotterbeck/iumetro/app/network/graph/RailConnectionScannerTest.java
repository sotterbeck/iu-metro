package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.common.PositionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static de.sotterbeck.iumetro.app.network.graph.RailRepository.RailShape.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RailConnectionScannerTest {

    @Mock
    private RailRepository railRepository;

    private RailConnectionScanner underTest;

    @BeforeEach
    void setUp() {
        underTest = new RailConnectionScanner(railRepository);
        when(railRepository.findRailAt(any())).thenReturn(NONE);
    }

    @Test
    void getConnectingRails_ShouldReturnNone_WhenBlockIsNotARail() {
        var result = underTest.getConnectingRails(new PositionDto(0, 0, 0));

        assertThat(result).isEmpty();
    }

    @Test
    void getConnectingRails_ShouldReturnNone_WhenNeighboringBlocksAreNotRails() {
        when(railRepository.findRailAt(new PositionDto(0, 0, 0))).thenReturn(NORTH_SOUTH);

        var result = underTest.getConnectingRails(new PositionDto(0, 0, 0));

        assertThat(result).isEmpty();
    }

    @Test
    void getConnectingRails_ShouldReturnNeighbors_WhenRailsConnect() {
        when(railRepository.findRailAt(new PositionDto(0, 0, 0))).thenReturn(NORTH_SOUTH);
        when(railRepository.findRailAt(new PositionDto(0, 0, 1))).thenReturn(NORTH_SOUTH);
        when(railRepository.findRailAt(new PositionDto(0, 0, -1))).thenReturn(NORTH_SOUTH);

        var result = underTest.getConnectingRails(new PositionDto(0, 0, 0));

        assertThat(result).containsExactlyInAnyOrder(
                new PositionDto(0, 0, 1),
                new PositionDto(0, 0, -1)
        );
    }

    @Test
    void getConnectingRails_ShouldReturnAscendingRails_WhenRailsConnectOnSameLevel() {
        when(railRepository.findRailAt(new PositionDto(0, 0, 0))).thenReturn(NORTH_SOUTH);
        when(railRepository.findRailAt(new PositionDto(0, 0, -1))).thenReturn(ASCENDING_NORTH);
        when(railRepository.findRailAt(new PositionDto(0, 0, 1))).thenReturn(ASCENDING_SOUTH);

        var result = underTest.getConnectingRails(new PositionDto(0, 0, 0));

        assertThat(result).containsExactlyInAnyOrder(
                new PositionDto(0, 0, -1),
                new PositionDto(0, 0, 1)
        );
    }

    @Test
    void getConnectingRails_ShouldReturnAscendingRails_WhenRailsConnectOnDifferentLevels() {
        when(railRepository.findRailAt(new PositionDto(0, 0, 0))).thenReturn(NORTH_SOUTH);
        when(railRepository.findRailAt(new PositionDto(0, -1, -1))).thenReturn(ASCENDING_SOUTH);
        when(railRepository.findRailAt(new PositionDto(0, -1, 1))).thenReturn(ASCENDING_NORTH);

        var result = underTest.getConnectingRails(new PositionDto(0, 0, 0));

        assertThat(result).containsExactlyInAnyOrder(
                new PositionDto(0, -1, -1),
                new PositionDto(0, -1, 1)
        );
    }

    @Test
    void getConnectingRails_ShouldReturnAscendingRails_WhenRailsConnectOnDifferentLevelsInBothDirections() {
        when(railRepository.findRailAt(new PositionDto(0, 0, 0))).thenReturn(ASCENDING_NORTH);
        when(railRepository.findRailAt(new PositionDto(0, -1, 1))).thenReturn(ASCENDING_NORTH);
        when(railRepository.findRailAt(new PositionDto(0, 1, -1))).thenReturn(ASCENDING_NORTH);

        var result = underTest.getConnectingRails(new PositionDto(0, 0, 0));

        assertThat(result).containsExactlyInAnyOrder(
                new PositionDto(0, -1, 1),
                new PositionDto(0, 1, -1)
        );
    }

}