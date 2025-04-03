package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FareGateProtectionInteractorTest {

    @Mock
    private FareGateSignRepository fareGateSignRepository;

    private FareGateProtectionInteractor fareGateProtectionInteractor;

    @BeforeEach
    void setUp() {
        fareGateProtectionInteractor = new FareGateProtectionInteractor(fareGateSignRepository);
    }

    @Test
    void canOpenGate_ShouldReturnTrue_WhenGateHasNoFareGateSign() {
        GateRequestModel gate = new GateRequestModel(165, 70, 148, "west");
        PositionDto signPosition = new PositionDto(166, 71, 149);
        when(fareGateSignRepository.findAt(signPosition)).thenReturn(Optional.empty());

        boolean result = fareGateProtectionInteractor.canOpenGate(gate);

        assertThat(result).isTrue();
    }

    @Test
    void canOpenGate_ShouldReturnFalse_WhenGateHasFareGateSign() {
        GateRequestModel gate = new GateRequestModel(165, 70, 148, "west");
        PositionDto signPosition = new PositionDto(166, 71, 149);
        FareGateDto fareGateDto = new FareGateDto(signPosition, "entry", "station");
        when(fareGateSignRepository.findAt(signPosition)).thenReturn(Optional.of(fareGateDto));

        boolean result = fareGateProtectionInteractor.canOpenGate(gate);

        assertThat(result).isFalse();
    }

}