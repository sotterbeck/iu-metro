package de.sotterbeck.iumetro.usecase.ticket;

import de.sotterbeck.iumetro.usecase.ticket.obtain.CreateTicketInteractor;
import de.sotterbeck.iumetro.usecase.ticket.obtain.CreateTicketInteractorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CreateTicketInteractorTest {

    @Mock
    TicketDsGateway ticketDsGateway;

    @Mock
    TicketPresenter ticketPresenter;
    CreateTicketInteractor underTest;

    @Test
    void invoke_ShouldSaveTicket() {
        TicketRequestModel ticketRequestModel = new TicketRequestModel(UUID.randomUUID(), "Single-Use Ticket", 0, Duration.ZERO);
        underTest = new CreateTicketInteractorImpl(ticketDsGateway, ticketPresenter);

        underTest.invoke(ticketRequestModel);

        then(ticketDsGateway).should(times(1)).save(any(TicketDsRequestModel.class));
    }

    @Test
    void invoke_ShouldPrintTicket() {
        TicketRequestModel ticketRequestModel = new TicketRequestModel(UUID.randomUUID(), "Single-Use Ticket", 0, Duration.ZERO);
        underTest = new CreateTicketInteractorImpl(ticketDsGateway, ticketPresenter);

        underTest.invoke(ticketRequestModel);

        then(ticketPresenter).should(times(1)).printTicket(eq(ticketRequestModel));
    }

    @Test
    void invoke_ShouldPrepareSuccessView() {
        TicketRequestModel ticketRequestModel = new TicketRequestModel(UUID.randomUUID(), "Single-Use Ticket", 0, Duration.ZERO);
        underTest = new CreateTicketInteractorImpl(ticketDsGateway, ticketPresenter);

        underTest.invoke(ticketRequestModel);

        then(ticketPresenter).should(times(1)).prepareSuccessView(any());
    }
}