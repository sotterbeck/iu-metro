package de.sotterbeck.uimetro.usecase.ticket;

import de.sotterbeck.uimetro.usecase.ticket.obtain.CreateTicketInteractor;
import de.sotterbeck.uimetro.usecase.ticket.obtain.CreateTicketInteractorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CreateTicketInteractorTest {

    @Mock
    TicketDsGateway ticketDsGateway;

    @Mock
    TicketPrintHandler ticketPrintHandler;
    CreateTicketInteractor underTest;

    @Test
    void invoke_ShouldSaveTicket() {
        TicketRequestModel ticketRequestModel = new TicketRequestModel("Single-Use Ticket", BigDecimal.ONE, 0, Duration.ZERO);
        underTest = new CreateTicketInteractorImpl(ticketDsGateway, ticketPrintHandler);

        underTest.invoke(ticketRequestModel);

        then(ticketDsGateway).should(times(1)).save(any(TicketDsRequestModel.class));
    }

    @Test
    void invoke_ShouldPrintTicket() {
        TicketRequestModel ticketRequestModel = new TicketRequestModel("Single-Use Ticket", BigDecimal.ONE, 0, Duration.ZERO);
        underTest = new CreateTicketInteractorImpl(ticketDsGateway, ticketPrintHandler);

        underTest.invoke(ticketRequestModel);

        then(ticketPrintHandler).should(times(1)).printTicket(eq(ticketRequestModel), any());
    }

}