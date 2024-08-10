package de.sotterbeck.iumetro.usecase.ticket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketManagingInteractorTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private TicketPresenter ticketPresenter;

    private TicketManagingInteractor underTest;

    @BeforeEach
    void setUp() {
        underTest = new TicketManagingInteractorImpl(ticketRepository, ticketPresenter);
    }

    @Test
    void create_ShouldSaveTicket() {
        TicketRequestModel ticketRequestModel = new TicketRequestModel(UUID.randomUUID(), "Single-Use Ticket", 0, Duration.ZERO);
        underTest = new TicketManagingInteractorImpl(ticketRepository, ticketPresenter);

        underTest.create(ticketRequestModel);

        then(ticketRepository).should(times(1)).save(any(TicketDto.class));
    }

    @Test
    void create_ShouldPrepareSuccessView() {
        TicketRequestModel ticketRequestModel = new TicketRequestModel(UUID.randomUUID(), "Single-Use Ticket", 0, Duration.ZERO);
        underTest = new TicketManagingInteractorImpl(ticketRepository, ticketPresenter);

        underTest.create(ticketRequestModel);

        then(ticketPresenter).should(times(1)).prepareSuccessView(any());
    }

    @Test
    void delete_ShouldDeleteTicketAndPrepareSuccessView_WhenTicketExits() {
        UUID id = UUID.fromString("1ed0a79e-f95e-4347-bd74-3f6c4ef3dc12");
        TicketDto ticketDto = new TicketDto(id, "Single-use Ticket");

        when(ticketRepository.existsById(id)).thenReturn(true);
        when(ticketRepository.get(id)).thenReturn(Optional.of(ticketDto));
        underTest.delete(id);

        then(ticketRepository).should(times(1)).deleteById(eq(id));
        then(ticketPresenter).should(times(1)).prepareSuccessView(
                eq(new TicketRequestModel(ticketDto.id(),
                        ticketDto.name(),
                        ticketDto.usageLimit(),
                        ticketDto.timeLimit())));
    }

    @Test
    void delete_ShouldNotDeleteTicketAndPrepareFailView_WhenTicketDoesNotExits() {
        UUID id = UUID.fromString("1ed0a79e-f95e-4347-bd74-3f6c4ef3dc12");

        when(ticketRepository.existsById(id)).thenReturn(false);
        underTest.delete(id);

        then(ticketRepository).should(never()).deleteById(eq(id));
        then(ticketPresenter).should(times(1)).prepareFailView(any());
    }

}