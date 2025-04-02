package de.sotterbeck.iumetro.usecase.retail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetailTicketManagingInteractorTest {

    @Mock
    private RetailTicketRepository retailTicketRepository;

    @Mock
    private RetailTicketPresenter presenter;

    private RetailTicketManagingInteractor retailTicketManagingInteractor;

    @BeforeEach
    void setUp() {
        retailTicketManagingInteractor = new RetailTicketManagingInteractor(retailTicketRepository, presenter);
    }

    @Test
    void create_ShouldPresentFailView_WhenRetailTicketAlreadyExistsWithSameName() {
        String ticketName = "Day Pass";
        String ticketId = "7fe89139-6d9f-4f21-84f7-10a6301d33a6";
        when(retailTicketRepository.existsByName(ticketName)).thenReturn(true);
        RetailTicketRequestModel request = createRetailTicketWithoutConstraints(ticketId, ticketName);

        retailTicketManagingInteractor.create(request);

        verify(presenter).prepareFailView("Retail ticket with name " + ticketName + " already exists");
    }

    @Test
    void create_ShouldPresentSucessViewAndSaveTicket_WhenTicketDoesNotExist() {
        String ticketName = "Day Pass";
        String ticketId = "7fe89139-6d9f-4f21-84f7-10a6301d33a6";
        when(retailTicketRepository.existsByName(ticketName)).thenReturn(false);
        RetailTicketRequestModel request = createRetailTicketWithoutConstraints(ticketId, ticketName);

        retailTicketManagingInteractor.create(request);

        verify(retailTicketRepository).save(any());
        verify(presenter).prepareSuccessView(request);
    }

    @Test
    void update_ShouldPresentFailView_WhenTicketDoesNotExist() {
        String ticketName = "Day Pass";
        String ticketId = "7fe89139-6d9f-4f21-84f7-10a6301d33a6";
        when(retailTicketRepository.existsById(UUID.fromString(ticketId))).thenReturn(false);
        RetailTicketRequestModel request = createRetailTicketWithoutConstraints(ticketId, ticketName);

        retailTicketManagingInteractor.update(ticketId, request);

        verify(presenter).prepareFailView("Retail ticket with id " + ticketId + " does not exist");
    }

    @Test
    void update_ShouldPresentSucessViewAndSaveTicket_WhenTicketExists() {
        String ticketName = "Day Pass";
        String ticketId = "7fe89139-6d9f-4f21-84f7-10a6301d33a6";
        when(retailTicketRepository.existsById(UUID.fromString(ticketId))).thenReturn(true);
        RetailTicketRequestModel request = createRetailTicketWithoutConstraints(ticketId, ticketName);

        retailTicketManagingInteractor.update(ticketId, request);

        verify(retailTicketRepository).save(any());
        verify(presenter).prepareSuccessView(request);
    }

    @Test
    void delete_ShouldPresentFailView_WhenTicketDoesNotExist() {
        String ticketId = "7fe89139-6d9f-4f21-84f7-10a6301d33a6";
        when(retailTicketRepository.getById(UUID.fromString(ticketId))).thenReturn(Optional.empty());

        retailTicketManagingInteractor.delete(ticketId);

        verify(presenter).prepareFailView("Retail ticket with id " + ticketId + " does not exist");
    }

    @Test
    void delete_ShouldPresentSuccessViewAndDeleteTicket_WhenTicketExists() {
        String ticketName = "Day Pass";
        String ticketId = "7fe89139-6d9f-4f21-84f7-10a6301d33a6";
        RetailTicketDto dto = new RetailTicketDto(UUID.fromString(ticketId), ticketName, "", 0, 0, Duration.ZERO, false, Instant.EPOCH, "");
        when(retailTicketRepository.getById(UUID.fromString(ticketId))).thenReturn(Optional.of(dto));
        RetailTicketRequestModel request = createRetailTicketWithoutConstraints(ticketId, ticketName);

        retailTicketManagingInteractor.delete(ticketId);

        verify(retailTicketRepository).delete(any());
        verify(presenter).prepareSuccessView(request);
    }

    private static RetailTicketRequestModel createRetailTicketWithoutConstraints(String ticketId, String ticketName) {
        return new RetailTicketRequestModel(ticketId, ticketName, "", 0L, 0, Duration.ZERO.toString(), false, "");
    }

}