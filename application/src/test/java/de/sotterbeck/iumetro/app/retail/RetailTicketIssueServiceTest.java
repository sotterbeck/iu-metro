package de.sotterbeck.iumetro.app.retail;

import de.sotterbeck.iumetro.app.ticket.TicketConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetailTicketIssueServiceTest {

    @Mock
    private RetailTicketRepository retailTicketRepository;

    @Mock
    private RetailTicketPresenter presenter;

    private RetailTicketService retailTicketService;

    @BeforeEach
    void setUp() {
        retailTicketService = new RetailTicketService(retailTicketRepository, presenter);
        lenient().when(presenter.prepareSuccessView(any(RetailTicketDto.class))).thenReturn(
                new RetailTicketResponseModel("", "", "", 0, new TicketConfig(List.of()), false, "", ""));
        lenient().when(presenter.prepareFailView(anyString())).thenReturn(
                new RetailTicketResponseModel("", "", "", 0, new TicketConfig(List.of()), false, "", ""));
    }

    @Test
    void create_ShouldPresentFailView_WhenRetailTicketAlreadyExistsWithSameName() {
        String ticketName = "Day Pass";
        when(retailTicketRepository.existsByName(ticketName)).thenReturn(true);
        RetailTicketRequestModel request = createRetailTicketWithoutConstraints(ticketName);

        RetailTicketResponseModel response = retailTicketService.create(request);

        assertNotNull(response);
    }

    @Test
    void create_ShouldPresentSucessViewAndSaveTicket_WhenTicketDoesNotExist() {
        String ticketName = "Day Pass";
        when(retailTicketRepository.existsByName(ticketName)).thenReturn(false);
        RetailTicketRequestModel request = createRetailTicketWithoutConstraints(ticketName);

        retailTicketService.create(request);

        verify(retailTicketRepository).save(any(RetailTicketDto.class));
    }

    @Test
    void update_ShouldPresentFailView_WhenTicketDoesNotExist() {
        String ticketName = "Day Pass";
        String ticketId = "7fe89139-6d9f-4f21-84f7-10a6301d33a6";
        when(retailTicketRepository.existsById(UUID.fromString(ticketId))).thenReturn(false);
        RetailTicketRequestModel request = createRetailTicketWithoutConstraints(ticketName);

        RetailTicketResponseModel response = retailTicketService.update(ticketId, request);

        assertNotNull(response);
    }

    @Test
    void update_ShouldPresentSucessViewAndSaveTicket_WhenTicketExists() {
        String ticketName = "Day Pass";
        String ticketId = "7fe89139-6d9f-4f21-84f7-10a6301d33a6";
        when(retailTicketRepository.existsById(UUID.fromString(ticketId))).thenReturn(true);
        RetailTicketRequestModel request = createRetailTicketWithoutConstraints(ticketName);

        retailTicketService.update(ticketId, request);

        verify(retailTicketRepository).save(any(RetailTicketDto.class));
    }

    @Test
    void delete_ShouldPresentFailView_WhenTicketDoesNotExist() {
        String ticketId = "7fe89139-6d9f-4f21-84f7-10a6301d33a6";
        when(retailTicketRepository.getById(UUID.fromString(ticketId))).thenReturn(Optional.empty());

        RetailTicketResponseModel response = retailTicketService.delete(ticketId);

        assertNotNull(response);
    }

    @Test
    void delete_ShouldPresentSuccessViewAndDeleteTicket_WhenTicketExists() {
        String ticketName = "Day Pass";
        String ticketId = "7fe89139-6d9f-4f21-84f7-10a6301d33a6";
        UUID uuid = UUID.fromString(ticketId);
        RetailTicketDto dto = new RetailTicketDto(uuid, ticketName, "", 0,
                new TicketConfig(List.of()), false, Instant.EPOCH, "");
        when(retailTicketRepository.getById(uuid)).thenReturn(Optional.of(dto));

        retailTicketService.delete(ticketId);

        verify(retailTicketRepository).delete(uuid);
    }

    private static RetailTicketRequestModel createRetailTicketWithoutConstraints(String ticketName) {
        return new RetailTicketRequestModel(ticketName, "", 0L, new TicketConfig(List.of()), false, "");
    }

}
