package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.ticket.*;
import de.sotterbeck.iumetro.domain.ticket.Ticket;
import de.sotterbeck.iumetro.domain.ticket.ValidationContext;
import de.sotterbeck.iumetro.domain.ticket.ValidationResult;
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
    private GateControlAdapter gateControlAdapter;

    @Mock
    private DomainTicketFactory ticketFactory;

    @Mock
    private PlayerAdapter playerAdapter;

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
                playerAdapter,
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
    void controlGate_shouldNotOpenGateOrRecordUsage_whenValidationAllowsGateButGateDoesNotExist() {
        underTest.controlGate(request);

        verify(gateControlAdapter, never()).openGate(any(), any());
        verify(ticketRepository, never()).saveTicketUsage(any(), any());
        verify(ticketItemRepository, never()).deleteTicket(any(), any());
        verify(ticketRepository, never()).deleteById(any());
    }

    @Test
    void controlGate_shouldRecordAndRemoveUsage_whenPlayerIsBehindGateAfterClose() {
        stubCurrentTicket();
        PositionDto playerBehindGate = new PositionDto(164, 70, 148);
        when(playerAdapter.findPosition(PLAYER_ID)).thenReturn(Optional.of(playerBehindGate));

        ValidationResult result = new ValidationResult(true, true, true, null);
        when(domainTicket.validate(any(ValidationContext.class))).thenReturn(result);
        when(gateRepository.findAt(GATE_POSITION)).thenReturn(Optional.of(gate));

        underTest.controlGate(request);

        var callbackCaptor = org.mockito.ArgumentCaptor.forClass(Runnable.class);
        verify(gateControlAdapter).openGate(eq(GATE_POSITION), callbackCaptor.capture());
        verify(ticketRepository, never()).saveTicketUsage(any(), any());
        verify(ticketItemRepository, never()).deleteTicket(any(), any());
        verify(ticketRepository, never()).deleteById(any());

        callbackCaptor.getValue().run();

        verify(ticketRepository).saveTicketUsage(TICKET_ID, new UsageDto(STATION, REQUESTED_AT, UsageType.ENTRY));
        verify(ticketItemRepository).deleteTicket(PLAYER_ID, TICKET_ID);
        verify(ticketRepository).deleteById(TICKET_ID);
    }

    @Test
    void controlGate_shouldNotRecordUsage_whenPlayerIsNotBehindGateAfterClose() {
        stubCurrentTicket();
        PositionDto playerInFrontOfGate = new PositionDto(166, 70, 148);
        when(playerAdapter.findPosition(PLAYER_ID)).thenReturn(Optional.of(playerInFrontOfGate));
        when(domainTicket.validate(any(ValidationContext.class))).thenReturn(new ValidationResult(true, true, true, null));
        when(gateRepository.findAt(GATE_POSITION)).thenReturn(Optional.of(gate));

        underTest.controlGate(request);

        var callbackCaptor = org.mockito.ArgumentCaptor.forClass(Runnable.class);
        verify(gateControlAdapter).openGate(eq(GATE_POSITION), callbackCaptor.capture());

        callbackCaptor.getValue().run();

        verify(ticketRepository, never()).saveTicketUsage(any(), any());
        verify(ticketItemRepository, never()).deleteTicket(any(), any());
        verify(ticketRepository, never()).deleteById(any());
    }

    @Test
    void controlGate_shouldNotRecordUsage_whenPlayerIsInsideGateBlockAfterClose() {
        stubCurrentTicket();
        when(playerAdapter.findPosition(PLAYER_ID)).thenReturn(Optional.of(GATE_POSITION));
        when(domainTicket.validate(any(ValidationContext.class))).thenReturn(new ValidationResult(true, true, true, null));
        when(gateRepository.findAt(GATE_POSITION)).thenReturn(Optional.of(gate));

        underTest.controlGate(request);

        var callbackCaptor = org.mockito.ArgumentCaptor.forClass(Runnable.class);
        verify(gateControlAdapter).openGate(eq(GATE_POSITION), callbackCaptor.capture());

        callbackCaptor.getValue().run();

        verify(playerAdapter).teleport(PLAYER_ID, new PositionDto(166, 70, 148));

        verify(ticketRepository, never()).saveTicketUsage(any(), any());
        verify(ticketItemRepository, never()).deleteTicket(any(), any());
        verify(ticketRepository, never()).deleteById(any());
    }

    @Test
    void controlGate_shouldApplyActionsImmediately_whenValidationDeniesGate() {
        stubCurrentTicket();
        ValidationResult result = new ValidationResult(false, true, true, "deny");
        when(domainTicket.validate(any(ValidationContext.class))).thenReturn(result);
        when(gateRepository.findAt(GATE_POSITION)).thenReturn(Optional.of(gate));

        underTest.controlGate(request);

        verify(gateControlAdapter, never()).openGate(any(), any());
        verify(ticketRepository).saveTicketUsage(TICKET_ID, new UsageDto(STATION, REQUESTED_AT, UsageType.ENTRY));
        verify(ticketItemRepository).deleteTicket(PLAYER_ID, TICKET_ID);
        verify(ticketRepository).deleteById(TICKET_ID);
        verifyNoInteractions(playerAdapter);
    }

}
