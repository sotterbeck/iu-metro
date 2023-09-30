package de.sotterbeck.iumetro.usecase.ticket.manage;

import de.sotterbeck.iumetro.usecase.ticket.TicketDsGateway;
import de.sotterbeck.iumetro.usecase.ticket.TicketDsModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllTicketsInteractorTest {

    @Mock
    TicketDsGateway ticketDsGateway;
    GetAllTicketsInteractor underTest;

    @Test
    void invoke_ShouldReturnEmptyList_WhenNoTicketsInDs() {
        List<TicketDsModel> noTickets = emptyList();

        when(ticketDsGateway.getAll()).thenReturn(noTickets);
        underTest = new GetAllTicketsInteractorImpl(ticketDsGateway);
        List<UUID> tickets = underTest.invoke();

        assertThat(tickets).isEmpty();
    }

    @Test
    void invoke_ShouldReturnTickets_WhenTicketsInDs() {
        List<TicketDsModel> dsTickets = List.of(new TicketDsModel(UUID.fromString("b02c2eb7-bf49-445e-80a3-5767c8c47db2"), "Employee Ticket"),
                new TicketDsModel(UUID.fromString("d80f8c5c-2474-4944-8e80-b3f32fff6882"), "Single-use Ticket", 1, Duration.ZERO));

        when(ticketDsGateway.getAll()).thenReturn(dsTickets);
        underTest = new GetAllTicketsInteractorImpl(ticketDsGateway);
        List<UUID> tickets = underTest.invoke();

        assertThat(tickets).size().isEqualTo(2);
    }

}