package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.ticket.*;
import de.sotterbeck.iumetro.domain.ticket.Ticket;
import de.sotterbeck.iumetro.domain.ticket.ValidationContext;
import de.sotterbeck.iumetro.domain.ticket.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareGateControlServiceTest {

    private static final PositionDto SIGN_POSITION = new PositionDto(166, 71, 149);
    private static final PositionDto GATE_POSITION = new PositionDto(165, 70, 148);
    private static final UUID TICKET_ID = UUID.fromString("cec416d0-36c7-4e73-b42f-077f1b4752dd");
    private static final UUID PLAYER_ID = UUID.fromString("ef88c7a8-2cf7-43b0-9fa3-7741a53bf669");
    private static final String STATION = "station";
    private static final String SIGN_ORIENTATION = "east";
    private static final ZonedDateTime REQUESTED_AT = ZonedDateTime.parse("2024-01-01T10:15:30Z");

    @Mock
    private GateRepository gateRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketItemRepository ticketItemRepository;

    @Mock
    private GateControlAdapter gateControlAdapter;

    @Mock
    private DomainTicketFactory ticketFactory;

    @Mock
    private Ticket domainTicket;

    private FareGateControlService underTest;
    private TicketDto ticket;
    private FareGateControlRequestModel request;
    private GateDto gate;

    @BeforeEach
    void setUp() {
        underTest = new FareGateControlService(gateRepository,
                gateControlAdapter,
                ticketRepository,
                ticketItemRepository,
                ticketFactory
        );

        ticket = new TicketDto(TICKET_ID, "ticket", new TicketConfig(List.of()));
        gate = new GateDto(GATE_POSITION, "west", false);
        var usage = new UsageRequestModel(PLAYER_ID, STATION, REQUESTED_AT, UsageType.ENTRY);
        request = new FareGateControlRequestModel(SIGN_POSITION, SIGN_ORIENTATION, usage);
    }

    private void stubCurrentTicket() {
        when(ticketItemRepository.findCurrentTicket(PLAYER_ID)).thenReturn(Optional.of(TICKET_ID));
        when(ticketRepository.get(TICKET_ID)).thenReturn(Optional.of(ticket));
        when(ticketRepository.existsById(TICKET_ID)).thenReturn(true);
        when(ticketRepository.getTicketUsages(TICKET_ID)).thenReturn(List.of());
        when(ticketFactory.from(ticket)).thenReturn(domainTicket);
    }

    @Test
    void controlGate_shouldNotOpenGate_whenValidationAllowsGateButGateDoesNotExist() {
        stubCurrentTicket();
        when(domainTicket.validate(any(ValidationContext.class))).thenReturn(new ValidationResult(true, false, false, null));
        when(gateRepository.findAt(GATE_POSITION)).thenReturn(Optional.empty());

        underTest.controlGate(request);

        verify(gateControlAdapter, never()).openGate(GATE_POSITION);
        verify(ticketRepository, never()).saveTicketUsage(any(), any());
        verify(ticketItemRepository, never()).deleteTicket(any(), any());
        verify(ticketRepository, never()).deleteById(any());
    }

    @ParameterizedTest
    @MethodSource("validationResults")
    void controlGate_shouldApplyActionsBasedOnValidationFlags(ValidationResult result,
                                                              int expectedOpenCalls,
                                                              int expectedRecordCalls,
                                                              int expectedRemoveCalls) {
        stubCurrentTicket();
        when(domainTicket.validate(any(ValidationContext.class))).thenReturn(result);
        lenient().when(gateRepository.findAt(GATE_POSITION)).thenReturn(Optional.of(gate));

        underTest.controlGate(request);

        verify(gateRepository, times(expectedOpenCalls)).findAt(GATE_POSITION);
        verify(gateControlAdapter, times(expectedOpenCalls)).openGate(GATE_POSITION);
        verify(ticketRepository, times(expectedRecordCalls))
                .saveTicketUsage(TICKET_ID, new UsageDto(STATION, REQUESTED_AT, UsageType.ENTRY));
        verify(ticketItemRepository, times(expectedRemoveCalls)).deleteTicket(PLAYER_ID, TICKET_ID);
        verify(ticketRepository, times(expectedRemoveCalls)).deleteById(TICKET_ID);
    }

    /// Provides a stream of test arguments to validate various scenarios for gate control operations.
    /// Each argument consists of the following:
    /// - A [ValidationResult] object for gate validation.
    /// - Expected number of calls to open the gate.
    /// - Expected number of calls to record gate usage.
    /// - Expected number of calls to remove the ticket.
    ///
    /// @return a stream of [Arguments], where each argument contains a [ValidationResult]
    /// object along with the expected counts for open, record, and remove operations.
    private static Stream<Arguments> validationResults() {
        return Stream.of(
                Arguments.of(new ValidationResult(false, false, false, "deny"), 0, 0, 0),
                Arguments.of(new ValidationResult(true, false, false, null), 1, 0, 0),
                Arguments.of(new ValidationResult(false, true, false, null), 0, 1, 0),
                Arguments.of(new ValidationResult(false, false, true, null), 0, 0, 1),
                Arguments.of(new ValidationResult(true, true, true, null), 1, 1, 1)
        );
    }

}
