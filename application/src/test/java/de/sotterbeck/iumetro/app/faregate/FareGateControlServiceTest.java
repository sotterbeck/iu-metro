package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.station.MetroStationRepository;
import de.sotterbeck.iumetro.app.ticket.TicketConfig;
import de.sotterbeck.iumetro.app.ticket.TicketDto;
import de.sotterbeck.iumetro.app.ticket.TicketItemRepository;
import de.sotterbeck.iumetro.app.ticket.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private MetroStationRepository metroStationRepository;


    @Mock
    private GateControlAdapter gateControlAdapter;

    private FareGateControlService underTest;
    private TicketDto ticket;
    private FareGateControlRequestModel request;
    private GateDto gate;

    @BeforeEach
    void setUp() {
        underTest = new FareGateControlService(gateRepository,
                gateControlAdapter,
                ticketRepository,
                ticketItemRepository
        );

        ticket = new TicketDto(TICKET_ID, "ticket", new TicketConfig(List.of()));
        gate = new GateDto(GATE_POSITION, "west", false);
        var usage = new UsageRequestModel(PLAYER_ID, STATION, REQUESTED_AT, UsageType.ENTRY);
        request = new FareGateControlRequestModel(SIGN_POSITION, SIGN_ORIENTATION, usage);
    }

    private void stubValidCurrentTicket() {
        when(ticketItemRepository.findCurrentTicket(PLAYER_ID)).thenReturn(Optional.of(TICKET_ID));
        when(ticketRepository.get(TICKET_ID)).thenReturn(Optional.of(ticket));
        when(ticketRepository.existsById(TICKET_ID)).thenReturn(true);
    }

    @Test
    void controlGate_shouldNotOpenGate_whenGateDoesNotExist() {
        stubValidCurrentTicket();
        when(gateRepository.findAt(GATE_POSITION)).thenReturn(Optional.empty());

        underTest.controlGate(request);

        verify(gateControlAdapter, never()).openGate(GATE_POSITION);
    }

    @Test
    void controlGate_shouldOpenGate_whenGateExists() {
        stubValidCurrentTicket();
        when(gateRepository.findAt(GATE_POSITION)).thenReturn(Optional.of(gate));

        underTest.controlGate(request);

        verify(gateControlAdapter).openGate(GATE_POSITION);
    }

    @Test
    void controlGate_shouldOpenAndRecordUsageWithoutRemovingTicket_whenTicketIsValid() {
        stubValidCurrentTicket();
        when(gateRepository.findAt(GATE_POSITION)).thenReturn(Optional.of(gate));

        underTest.controlGate(request);

        verify(gateControlAdapter).openGate(GATE_POSITION);
        verify(ticketRepository).saveTicketUsage(TICKET_ID, new UsageDto(STATION, REQUESTED_AT, UsageType.ENTRY));
        verify(ticketItemRepository, never()).deleteTicket(PLAYER_ID, TICKET_ID);
    }

    @Test
    void controlGate_shouldNotOpenRecordOrRemove_whenValidationDeniesEntry() {
        ticket = new TicketDto(TICKET_ID, "ticket", new TicketConfig(List.of(new TicketConfig.UsageLimit(1))));
        stubValidCurrentTicket();
        when(ticketRepository.getTicketUsages(TICKET_ID))
                .thenReturn(List.of(new UsageDto(STATION, REQUESTED_AT.minusMinutes(5), UsageType.ENTRY)));

        underTest.controlGate(request);

        verify(gateControlAdapter, never()).openGate(GATE_POSITION);
        verify(ticketRepository, never()).saveTicketUsage(TICKET_ID, new UsageDto(STATION, REQUESTED_AT, UsageType.ENTRY));
        verify(ticketItemRepository, never()).deleteTicket(PLAYER_ID, TICKET_ID);
    }

    @Test
    void controlGate_shouldOpenRecordAndRemove_whenValidationAllowsExitAndRequiresRemoval() {
        ticket = new TicketDto(TICKET_ID, "ticket", new TicketConfig(List.of(new TicketConfig.UsageLimit(1))));
        stubValidCurrentTicket();
        when(ticketRepository.getTicketUsages(TICKET_ID))
                .thenReturn(List.of(new UsageDto(STATION, REQUESTED_AT.minusMinutes(5), UsageType.ENTRY)));
        when(gateRepository.findAt(GATE_POSITION)).thenReturn(Optional.of(gate));
        var exitUsage = new UsageRequestModel(PLAYER_ID, STATION, REQUESTED_AT.plusMinutes(1), UsageType.EXIT);
        var exitRequest = new FareGateControlRequestModel(SIGN_POSITION, SIGN_ORIENTATION, exitUsage);

        underTest.controlGate(exitRequest);

        verify(gateControlAdapter).openGate(GATE_POSITION);
        verify(ticketRepository)
                .saveTicketUsage(TICKET_ID, new UsageDto(STATION, REQUESTED_AT.plusMinutes(1), UsageType.EXIT));
        verify(ticketItemRepository).deleteTicket(PLAYER_ID, TICKET_ID);
    }

}
