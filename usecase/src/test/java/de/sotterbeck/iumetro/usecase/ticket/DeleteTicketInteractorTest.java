package de.sotterbeck.iumetro.usecase.ticket;

import de.sotterbeck.iumetro.usecase.ticket.obtain.DeleteTicketInteractor;
import de.sotterbeck.iumetro.usecase.ticket.obtain.DeleteTicketInteractorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTicketInteractorTest {

    @Mock
    TicketDsGateway ticketDsGateway;

    @Mock
    TicketPresenter ticketPresenter;
    DeleteTicketInteractor underTest;

    @BeforeEach
    void setUp() {
        underTest = new DeleteTicketInteractorImpl(ticketDsGateway, ticketPresenter);
    }

    @Test
    void delete_ShouldDeleteTicketAndPrepareSuccessView_WhenTicketExits() {
        UUID id = UUID.fromString("1ed0a79e-f95e-4347-bd74-3f6c4ef3dc12");
        TicketDsModel ticketDsModel = new TicketDsModel(id, "Single-use Ticket");

        when(ticketDsGateway.existsById(id)).thenReturn(true);
        when(ticketDsGateway.get(id)).thenReturn(Optional.of(ticketDsModel));
        underTest.delete(id);

        then(ticketDsGateway).should(times(1)).deleteById(eq(id));
        then(ticketPresenter).should(times(1)).prepareSuccessView(
                eq(new TicketRequestModel(ticketDsModel.id(),
                        ticketDsModel.name(),
                        ticketDsModel.usageLimit(),
                        ticketDsModel.timeLimit())));
    }

    @Test
    void delete_ShouldNotDeleteTicketAndPrepareFailView_WhenTicketDoesNotExits() {
        UUID id = UUID.fromString("1ed0a79e-f95e-4347-bd74-3f6c4ef3dc12");

        when(ticketDsGateway.existsById(id)).thenReturn(false);
        underTest.delete(id);

        then(ticketDsGateway).should(never()).deleteById(eq(id));
        then(ticketPresenter).should(times(1)).prepareFailView(any());
    }

}