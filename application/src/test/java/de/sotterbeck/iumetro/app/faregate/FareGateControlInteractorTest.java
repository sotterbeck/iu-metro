package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareGateControlInteractorTest {

    @Mock
    private GateRepository gateRepository;

    @Mock
    private GateControlAdapter gateControlAdapter;

    private FareGateControlInteractor underTest;

    @BeforeEach
    void setUp() {
        underTest = new FareGateControlInteractor(gateRepository, gateControlAdapter);
    }

    @Test
    void openGate_ShouldNotOpenGate_WhenGateRepositoryDoesNotReturnGate() {
        PositionDto signPosition = new PositionDto(166, 71, 149);
        PositionDto gatePosition = new PositionDto(165, 70, 148);
        FareGateControlRequestModel request = new FareGateControlRequestModel(signPosition, "east");
        when(gateRepository.findAt(gatePosition)).thenReturn(Optional.empty());

        underTest.openGate(request);

        verify(gateControlAdapter, never()).openGate(gatePosition);
    }

    @Test
    void openGate_ShouldOpenGate_WhenGateRepositoryReturnsGate() {
        PositionDto signPosition = new PositionDto(166, 71, 149);
        PositionDto gatePosition = new PositionDto(165, 70, 148);
        GateDto gate = new GateDto(gatePosition, "west", false);
        FareGateControlRequestModel request = new FareGateControlRequestModel(signPosition, "east");
        when(gateRepository.findAt(gatePosition)).thenReturn(Optional.of(gate));

        underTest.openGate(request);

        verify(gateControlAdapter).openGate(gatePosition);
    }

}